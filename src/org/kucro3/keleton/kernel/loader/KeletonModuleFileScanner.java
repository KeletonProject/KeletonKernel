package org.kucro3.keleton.kernel.loader;

import org.kucro3.keleton.emulated.EmulatedHandle;
import org.kucro3.keleton.kernel.KeletonKernel;
import org.kucro3.keleton.kernel.module.KeletonModuleImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class KeletonModuleFileScanner {
    public KeletonModuleFileScanner(EmulatedHandle handle)
    {
        if(!handle.isDirectory())
            throw new IllegalArgumentException("not a directory: " + handle.getPath());

        this.emulated = handle;
    }

    public Collection<KeletonModuleImpl> scan()
    {
        List<KeletonModuleImpl> list = new ArrayList<>();

//        EmulatedHandle[] handles = emulated.listHandles((emulatedHandle -> emulatedHandle.getName().endsWith(".jar")));
//        Optional<KeletonModuleImpl> optional;
//
//        for(EmulatedHandle handle : handles)
//        {
//            KeletonModuleFile kmf = new KeletonModuleFile(file, KeletonKernel.getLaunchClassLoader());
//            if((optional = kmf.scan()).isPresent())
//                list.add(optional.get());
//        }

        return list;
    }

    public EmulatedHandle getEmulated()
    {
        return emulated;
    }

    private final EmulatedHandle emulated;
}
