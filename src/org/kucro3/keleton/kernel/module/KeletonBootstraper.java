package org.kucro3.keleton.kernel.module;

import org.kucro3.keleton.exception.KeletonException;
import org.kucro3.keleton.kernel.KeletonKernel;

import java.io.File;

public class KeletonBootstraper {
    public KeletonBootstraper()
    {
        if(bootstraper != null)
            throw new IllegalStateException("Reconstruction");

        bootstraper = this;
    }

    public synchronized boolean bootstrap() throws KeletonException
    {
        if(launched)
            return false;

        ModuleSequence sequence = new ModuleSequence(new KeletonModuleFileScanner(new File("modules\\")).scan());
        KeletonModuleManagerImpl impl = KeletonKernel.getModuleManagerImpl();

        impl.sequence = sequence;

        sequence.loadAll();
        sequence.enableAll();

        return true;
    }

    public synchronized boolean destroy() throws KeletonException
    {
        if(!launched)
            return false;

        KeletonModuleManagerImpl impl = KeletonKernel.getModuleManagerImpl();

        impl.sequence.disableAll();
        impl.sequence.destroyAll();

        return true;
    }

    public static KeletonBootstraper getBootstraper()
    {
        return bootstraper;
    }

    private boolean launched;

    private static KeletonBootstraper bootstraper;
}