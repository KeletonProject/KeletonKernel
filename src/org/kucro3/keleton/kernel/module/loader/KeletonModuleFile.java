package org.kucro3.keleton.kernel.module.loader;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.kucro3.keleton.kernel.KeletonKernel;
import org.kucro3.keleton.module.KeletonInstance;
import org.kucro3.keleton.module.Module;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class KeletonModuleFile {
    public KeletonModuleFile(File file, LaunchClassLoader loader) throws IOException
    {
        this.file = file;
        this.entries = new HashMap<>();
        this.loader = loader;

        loader.addURL(file.toURI().toURL());
    }

    public Optional<KeletonModuleImpl> scan() throws IOException
    {
        String discoveredClassName = null;
        try(JarFile jarFile = new JarFile(file))
        {
            Enumeration<JarEntry> eEntry = jarFile.entries();
            while(eEntry.hasMoreElements())
            {
                JarEntry entry = eEntry.nextElement();
                entries.put(entry.getName(), entry);

                byte[] byts = readClassFully(jarFile, entry);

                if(byts == null)
                    continue;

                ClassReader cr = new ClassReader(byts);
                ClassNode cn = new ClassNode();

                cr.accept(cn, ClassReader.SKIP_CODE);

                for(AnnotationNode an : (List<AnnotationNode>) cn.visibleAnnotations)
                    if(an.desc.equals(DESCRIPTOR_ANNOTATION_MODULE))
                        if(discoveredClassName == null)
                            discoveredClassName = cn.name;
                        else {
                            KeletonKernel.getLogger().warn("Found multiple module class in file " + file, ", IGNORED.");
                            return Optional.empty();
                        }
            }
        }

        if(discoveredClassName == null)
            return Optional.empty();

        Class<?> clazz;
        try {
            clazz = loader.findClass(discoveredClassName.replace('/', '.'));
        } catch (ClassNotFoundException e) {
            NoClassDefFoundError error = new NoClassDefFoundError(discoveredClassName);
            error.initCause(e);
            throw error;
        }

        Module info = clazz.getAnnotation(Module.class);
        if(info == null)
            throw new IllegalStateException("disappeared metadata");

        Object object;
        try {
            object = clazz.newInstance();
        } catch (Exception e) {
            KeletonKernel.getLogger().warn("Failed to construct module " + file, e);
            return Optional.empty();
        }

        if(!(object instanceof KeletonInstance))
        {
            KeletonKernel.getLogger().warn("Module in file " + file + " ");
            return Optional.empty();
        }
    }

    private static byte[] readClassFully(JarFile file, JarEntry entry) throws IOException
    {
        InputStream is = file.getInputStream(entry);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // check magic value
        int ch;
        for(int i = 0; i < 4; i++)
            if((ch = is.read()) != MAGICVALUE[i])
                return null;
            else
                bos.write(ch);

        while((ch = is.read()) > 0)
            bos.write(ch);

        return bos.toByteArray();
    }

    public File getFile()
    {
        return file;
    }

    private static final int[] MAGICVALUE = {0xCA, 0xFE, 0xBA, 0xBE};

    private static final String DESCRIPTOR_ANNOTATION_MODULE = Type.getDescriptor(Module.class);

    private final Map<String, JarEntry> entries;

    private final File file;

    private final LaunchClassLoader loader;
}
