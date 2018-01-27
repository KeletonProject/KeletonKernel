package org.kucro3.keleton.kernel.loader;

import org.kucro3.keleton.kernel.KeletonKernel;
import org.kucro3.keleton.kernel.module.KeletonModuleImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class KeletonModuleFileScanner {
    public KeletonModuleFileScanner(File dir)
    {
        if(!dir.isDirectory())
            throw new IllegalArgumentException("not a directory: " + dir);

        this.dir = dir;
    }

    public Collection<KeletonModuleImpl> scan()
    {
        List<KeletonModuleImpl> list = new ArrayList<>();

        File[] files = dir.listFiles((dir, name) -> name.endsWith(".jar"));
        Optional<KeletonModuleImpl> optional;

        for(File file : files)
        {
            KeletonModuleFile kmf = new KeletonModuleFile(file, KeletonKernel.getLaunchClassLoader());
            if((optional = kmf.scan()).isPresent())
                list.add(optional.get());
        }

        return list;
    }

    public File getDirectory()
    {
        return dir;
    }

    private final File dir;
}
