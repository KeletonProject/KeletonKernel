package org.kucro3.keleton.kernel.module.loader;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.kucro3.keleton.exception.KeletonInternalException;
import org.kucro3.keleton.kernel.KeletonKernel;
import org.kucro3.keleton.kernel.io.JarUtil;
import org.kucro3.keleton.kernel.module.URLUtil;
import org.kucro3.keleton.module.KeletonInstance;
import org.kucro3.keleton.module.Module;
import org.kucro3.keleton.module.exception.KeletonLoaderException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.api.event.cause.Cause;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class KeletonModuleFile extends URLStreamHandler {
    public KeletonModuleFile(File file, LaunchClassLoader loader) throws IOException
    {
        this.file = file;
        this.entries = new HashMap<>();
        this.loader = loader;
    }

    public Optional<KeletonModuleImpl> scan() throws IOException
    {
        boolean registered = false;

        try {
            String discoveredClassName = null;
            Map<String, Object> values = new HashMap<>();
            try (JarFile jarFile = new JarFile(file)) {
                Enumeration<JarEntry> eEntry = jarFile.entries();
                while (eEntry.hasMoreElements()) {
                    JarEntry entry = eEntry.nextElement();
                    entries.put(entry.getName(), entry);

                    byte[] byts = JarUtil.readClassFully(jarFile, entry);

                    if (byts == null)
                        continue;

                    ClassReader cr = new ClassReader(byts);
                    ClassNode cn = new ClassNode();

                    cr.accept(cn, ClassReader.SKIP_CODE);

                    for (AnnotationNode an : (List<AnnotationNode>) cn.visibleAnnotations)
                        if (an.desc.equals(DESCRIPTOR_ANNOTATION_MODULE))
                            if (discoveredClassName == null) {
                                discoveredClassName = cn.name;

                                Iterator<Object> iter = an.values.iterator();
                                while (iter.hasNext())
                                    values.put((String) iter.next(), iter.next());
                            } else {
                                KeletonKernel.getLogger().warn("Found multiple module class in file " + file, ", IGNORED.");
                                return Optional.empty();
                            }
                }
            }

            Object id = values.get("id");
            if(id == null)
                throw new IllegalStateException("disappeared metadata");

            String mid = (String) id;
            URL url;
            try {
                url = URLUtil.createURL(mid, this);
            } catch (Exception e) {
                throw new KeletonInternalException(e);
            }

            loader.addURL(url);
            registered = true;

            Class<?> clazz;
            try {
                clazz = loader.findClass(discoveredClassName.replace('/', '.'));
            } catch (ClassNotFoundException e) {
                NoClassDefFoundError error = new NoClassDefFoundError(discoveredClassName);
                error.initCause(e);
                throw error;
            }

            Module info = clazz.getAnnotation(Module.class);
            if (info == null)
                throw new IllegalStateException("disappeared metadata");

            LoaderEventImpl.Pre pre = KeletonKernel.postEvent(new LoaderEventImpl.Pre(createCause(info), info));
            if (pre.isCancelled()) {
                Cause cause = createCause(info);
                if (pre.isCancelledWithCause())
                    cause = cause.merge(pre.getCancellationCause().get());

                KeletonKernel.postEvent(new LoaderEventImpl.Cancelled(cause, info));
                return Optional.empty();
            }

            Object object;
            try {
                object = clazz.newInstance();
            } catch (Exception e) {
                KeletonKernel.postEvent(new LoaderEventImpl.Failed(
                        createCause(info),
                        info,
                        new KeletonLoaderException("Failed to construct module " + file, e)));
                return Optional.empty();
            }

            if (!(object instanceof KeletonInstance)) {
                KeletonKernel.postEvent(new LoaderEventImpl.Ignored(
                        createCause(info),
                        info,
                        "Module " + info.id() + " (In file " + file + ") is not an instance of KeletonInstance"));
                return Optional.empty();
            }

            KeletonInstance instance = (KeletonInstance) object;
            KeletonModuleImpl module = new KeletonModuleImpl(instance, info);

            KeletonKernel.postEvent(new LoaderEventImpl.Discovered(createCause(info), module, info));

            return Optional.of(module);
        } finally {
            if(!registered)
                loader.addURL(file.toURI().toURL());
        }
    }

    public File getFile()
    {
        return file;
    }

    public static Cause createCause(Module info)
    {
        return Cause.source(info).build();
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException
    {
        return null;
    }

    private static final String DESCRIPTOR_ANNOTATION_MODULE = Type.getDescriptor(Module.class);

    private final Map<String, JarEntry> entries;

    private final File file;

    private final LaunchClassLoader loader;
}
