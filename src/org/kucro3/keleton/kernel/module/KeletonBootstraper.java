package org.kucro3.keleton.kernel.module;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.kucro3.keleton.exception.KeletonException;
import org.kucro3.keleton.kernel.KeletonKernel;
import org.kucro3.keleton.kernel.emulated.EmulatedAPIProvider;
import org.kucro3.keleton.kernel.emulated.impl.LocalEmulated;
import org.kucro3.keleton.kernel.loader.EmulatedHandleScanner;
import org.kucro3.keleton.kernel.loader.klink.KlinkLibraryConvertingTrigger;
import org.kucro3.keleton.kernel.loader.klink.KlinkLibraryPreloadingTrigger;
import org.kucro3.keleton.kernel.loader.klink.KlinkLibraryRegistryTrigger;
import org.kucro3.keleton.kernel.loader.module.KeletonModuleDiscoveringTrigger;
import org.kucro3.keleton.kernel.loader.module.KeletonModuleLoadCompletionTrigger;
import org.kucro3.keleton.kernel.loader.module.KeletonModuleVerifyingTrigger;
import org.kucro3.keleton.klink.Library;
import org.kucro3.keleton.module.Module;
import org.kucro3.trigger.Fence;
import org.kucro3.trigger.Pipeline;

public class KeletonBootstraper {
    public KeletonBootstraper()
    {
        if(bootstraper != null)
            throw new IllegalStateException("Reconstruction");

        bootstraper = this;
    }

    public synchronized boolean initialize() throws KeletonException
    {
        if(launched)
            return false;

        String emulationPolicy = System.getProperty("keleton.kernel.emulation");

        if(emulationPolicy == null || emulationPolicy.equalsIgnoreCase("default"))
            EmulatedAPIProvider.initialize(new LocalEmulated());
        else
            throw new IllegalStateException("Illegal property: keleton.kernel.emulation=" + emulationPolicy);

        return true;
    }

    public synchronized boolean bootstrap() throws KeletonException
    {
        // TODO
//        if(launched)
//            return false;
//
//        ModuleSequence sequence = new ModuleSequence(new KeletonModuleFileScanner(new File("modules\\")).scan());
//        KeletonModuleManagerImpl impl = KeletonKernel.getModuleManagerImpl();
//
//        impl.sequence = sequence;
//
//        sequence.loadAll();
//        sequence.enableAll();

        return true;
    }

    public synchronized boolean discoverModules() throws KeletonException
    {
        EmulatedHandleScanner scanner = new EmulatedHandleScanner(
                EmulatedAPIProvider.getEmulated().getModuleDirectory(),
                (LaunchClassLoader) this.getClass().getClassLoader()
        );

        ModuleCollection collection = moduleCollection = new ModuleCollection();
        Fence moduleTriggersFence = this.moduleTriggersFence = new Fence();
        scanner.registerClassAnnotationTriggers(
                Module.class,
                Pipeline.of(Module.class.getCanonicalName())
                    .then(new KeletonModuleVerifyingTrigger(collection))
                    .then(new KeletonModuleDiscoveringTrigger(collection), moduleTriggersFence)
                    .then(new KeletonModuleLoadCompletionTrigger())
                .end()
        );

        scanner.registerClassAnnotationTriggers(
                Library.class,
                Pipeline.of(Pipeline.class.getCanonicalName())
                    .then(new KlinkLibraryConvertingTrigger())
                    .then(new KlinkLibraryPreloadingTrigger())
                    .then(new KlinkLibraryRegistryTrigger(KeletonKernel.getKlink()))
                .end()
        );

        scanner.scan();

        return true;
    }

    public synchronized boolean loadModules() throws KeletonException
    {
        if(!launched)
            return false;

        moduleTriggersFence.dismantle();

        (KeletonKernel.getModuleManagerImpl().sequence = new ModuleSequence(moduleCollection.getModules())).loadAll();

        return true;
    }

    public synchronized boolean enableModules() throws KeletonException
    {
        if(!launched)
            return false;

        KeletonKernel.getModuleManagerImpl().sequence.enableAll();
        return true;
    }

    public synchronized boolean disableModules() throws KeletonException
    {
        if(!launched)
            return false;

        KeletonKernel.getModuleManagerImpl().sequence.disableAll();
        return true;
    }

    public synchronized boolean destroyModules() throws KeletonException
    {
        if(!launched)
            return false;

        KeletonKernel.getModuleManagerImpl().sequence.destroyAll();
        return true;
    }

    public static KeletonBootstraper getBootstraper()
    {
        return bootstraper;
    }

    private boolean launched;

    private Fence moduleTriggersFence;

    private ModuleCollection moduleCollection;

    private static KeletonBootstraper bootstraper;
}
