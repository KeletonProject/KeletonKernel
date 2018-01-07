package org.kucro3.keleton.kernel.module.loader;

import org.kucro3.keleton.Keleton;
import org.kucro3.keleton.exception.KeletonException;

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

        org.kucro3.keleton.kernel.module.loader.ModuleSequence sequence = new org.kucro3.keleton.kernel.module.loader.ModuleSequence(ModuleDiscoverer.discover());
        org.kucro3.keleton.kernel.module.loader.KeletonModuleManagerImpl impl = (org.kucro3.keleton.kernel.module.loader.KeletonModuleManagerImpl) Keleton.getModuleManager();

        impl.sequence = sequence;

        sequence.loadAll();
        sequence.enableAll();

        return true;
    }

    public synchronized boolean destroy() throws KeletonException
    {
        if(!launched)
            return false;

        org.kucro3.keleton.kernel.module.loader.KeletonModuleManagerImpl impl = (org.kucro3.keleton.kernel.module.loader.KeletonModuleManagerImpl) Keleton.getModuleManager();

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
