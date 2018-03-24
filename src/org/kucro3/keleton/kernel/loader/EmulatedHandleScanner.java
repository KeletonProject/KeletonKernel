package org.kucro3.keleton.kernel.loader;

import com.google.common.eventbus.EventBus;
import com.theredpixelteam.redtea.trigger.Condition;
import com.theredpixelteam.redtea.trigger.Pair;
import com.theredpixelteam.redtea.trigger.Pipeline;
import com.theredpixelteam.redtea.trigger.TriggerContext;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.kucro3.keleton.emulated.EmulatedHandle;
import org.kucro3.keleton.kernel.io.ClassUtil;
import org.kucro3.keleton.kernel.io.StreamUtil;
import org.kucro3.keleton.kernel.loader.event.ModuleResourceDiscoveredEvent;
import org.kucro3.keleton.kernel.loader.event.ModuleResourceFailureEvent;
import org.kucro3.keleton.kernel.url.URLFactory;
import org.kucro3.keleton.kernel.url.inmemory.InMemoryResources;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

@SuppressWarnings("unchecked")
public class EmulatedHandleScanner {
    public EmulatedHandleScanner(EmulatedHandle handle, LaunchClassLoader launchClassLoader)
    {
        this(handle, launchClassLoader, new EventBus("EmulatedHandleScanner"));
    }

    public EmulatedHandleScanner(EmulatedHandle handle, LaunchClassLoader launchClassLoader, EventBus eventBus)
    {
        if(!handle.exists())
            handle.makeDirectory();
        else if(!handle.isDirectory())
            throw new IllegalArgumentException("Not a directory: " + handle.getPath());

        this.handle = handle;
        this.launchClassLoader = launchClassLoader;
        this.bus = eventBus;
    }

    public void scan()
    {
        EmulatedHandle[] handles = handle.listHandles((h) -> h.getPath().endsWith(".jar") && !h.isDirectory());
        List<InMemoryResources> resourcesList = new ArrayList<>();
        List<URL> urlList = new ArrayList<>();

        for(EmulatedHandle handle : handles) try {
            try(InputStream is = handle.openInput().get()) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                StreamUtil.pour(is, buffer);

                JarInputStream jis = new JarInputStream(new ByteArrayInputStream(buffer.toByteArray()));
                JarEntry entry;

                ModuleResourceDiscoveredEvent discoveredEvent = new ModuleResourceDiscoveredEvent(handle);
                bus.post(discoveredEvent);

                if (discoveredEvent.isCancelled())
                    continue;

                InMemoryResources buffered = new InMemoryResources();
                while ((entry = jis.getNextJarEntry()) != null) {
                    if (entry.isDirectory()) {
                        jis.closeEntry();
                        continue;
                    }

                    buffered.setResource(entry.getName(), StreamUtil.readFully(jis));

                    jis.closeEntry();
                }

                URL url;
                launchClassLoader.addURL(url = URLFactory.inMemoryURL(buffered, handle.getName()));

                urlList.add(url);
                resourcesList.add(buffered);
            } catch (Throwable e) {
                bus.post(new ModuleResourceFailureEvent(handle, e));
            }
        } catch (Throwable e) {
            bus.post(new ModuleResourceFailureEvent(handle, e));
        }

        for(int i = 0; i < resourcesList.size(); i++)
        {
            URL url = urlList.get(i);
            InMemoryResources buffered = resourcesList.get(i);

            for(Map.Entry<String, byte[]> bufferedEntry : buffered.getResources().entrySet()) try {
                if(!ClassUtil.checkMagicValue(bufferedEntry.getValue()))
                    continue;

                ClassNode cn = new ClassNode();
                ClassReader cr = new ClassReader(bufferedEntry.getValue());

                cr.accept(cn, 0);

                if(cn.visibleAnnotations != null)
                    for(AnnotationNode an : (List<AnnotationNode>) cn.visibleAnnotations)
                        classAnnotationCondition.trigger(an, TriggerContext.of(
                                Pair.of("eventBus", bus),
                                Pair.of("emulated", handle),
                                Pair.of("entryName", bufferedEntry.getKey()),
                                Pair.of("buffered", bufferedEntry.getValue()),
                                Pair.of("resources", buffered),
                                Pair.of("class", cn),
                                Pair.of("annotation", an),
                                Pair.of("launchloader", launchClassLoader),
                                Pair.of("url", url)
                        ));

                if(cn.methods != null)
                    for(MethodNode mn : (List<MethodNode>) cn.methods)
                        if(mn.visibleAnnotations != null)
                            for(AnnotationNode an : (List<AnnotationNode>) mn.visibleAnnotations)
                                methodAnnotationCondition.trigger(an, TriggerContext.of(
                                        Pair.of("eventBus", bus),
                                        Pair.of("emulated", handle),
                                        Pair.of("resources", buffered),
                                        Pair.of("class", cn),
                                        Pair.of("method", mn),
                                        Pair.of("annotation", an)
                                ));
            } catch (Throwable e) {
                bus.post(new ModuleResourceFailureEvent(handle, e));
            }
        }
    }

    public EmulatedHandle getHandle()
    {
        return handle;
    }

    public void registerListener(Object listener)
    {
        bus.register(listener);
    }

    public void unregisterListener(Object listener)
    {
        bus.unregister(listener);
    }

    public boolean registerClassAnnotationTriggers(Class<? extends Annotation> annotationType, Pipeline pipeline)
    {
        final String descriptor = Type.getDescriptor(annotationType);
        return classAnnotationCondition.bind((node) -> node.desc.equals(descriptor), pipeline);
    }

    public boolean registerMethodAnnotationTriggers(Class<? extends Annotation> annotationType, Pipeline pipeline)
    {
        final String descriptor = Type.getDescriptor(annotationType);
        return methodAnnotationCondition.bind((node) -> node.desc.equals(descriptor), pipeline);
    }

    private final Condition<AnnotationNode> methodAnnotationCondition = new Condition<>();

    private final Condition<AnnotationNode> classAnnotationCondition = new Condition<>();

    private final EmulatedHandle handle;

    private final EventBus bus;

    private final LaunchClassLoader launchClassLoader;
}
