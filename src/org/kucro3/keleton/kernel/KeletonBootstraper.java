package org.kucro3.keleton.kernel;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.kucro3.keleton.emulated.EmulatedHandle;
import org.kucro3.keleton.exception.KeletonException;
import org.kucro3.keleton.exception.KeletonInternalException;
import org.kucro3.keleton.kernel.emulated.EmulatedAPIProvider;
import org.kucro3.keleton.kernel.emulated.EmulationInitializer;
import org.kucro3.keleton.kernel.loader.EmulatedHandleScanner;
import org.kucro3.keleton.kernel.loader.klink.KlinkLibraryConvertingTrigger;
import org.kucro3.keleton.kernel.loader.klink.KlinkLibraryPreloadingTrigger;
import org.kucro3.keleton.kernel.loader.klink.KlinkLibraryRegistryTrigger;
import org.kucro3.keleton.kernel.loader.module.KeletonModuleDiscoveringTrigger;
import org.kucro3.keleton.kernel.loader.module.KeletonModuleLoadCompletionTrigger;
import org.kucro3.keleton.kernel.loader.module.KeletonModuleVerifyingTrigger;
import org.kucro3.keleton.kernel.module.ModuleCollection;
import org.kucro3.keleton.kernel.module.ModuleSequence;
import org.kucro3.keleton.kernel.xmount.XMountAPIProvider;
import org.kucro3.keleton.kernel.xmount.trigger.MountablePreloadingTrigger;
import org.kucro3.keleton.kernel.xmount.trigger.MountableRegistryTrigger;
import org.kucro3.keleton.kernel.xmount.trigger.MountableVerifyingTrigger;
import org.kucro3.keleton.klink.Library;
import org.kucro3.keleton.klink.xmount.Mountable;
import org.kucro3.keleton.module.Module;
import org.kucro3.klink.Executable;
import org.kucro3.klink.SequenceUtil;
import org.kucro3.trigger.Fence;
import org.kucro3.trigger.Pipeline;

import java.io.IOException;

public class KeletonBootstraper {
    public KeletonBootstraper()
    {
        if(bootstraper != null)
            throw new IllegalStateException("Reconstruction");

        bootstraper = this;
    }

    public synchronized boolean initialize() throws KeletonException
    {
        if(launched || initialized)
            return false;

        EmulationInitializer emulationInitializer = new EmulationInitializer(EmulatedAPIProvider::initialize);
        emulationInitializer.initialize(System.getProperties());

        initialized = true;

        return true;
    }

    public synchronized boolean bootstrap() throws KeletonException
    {
        if(!initialized)
            return false;

        Executable executable;
        EmulatedHandle handle = EmulatedAPIProvider.getEmulated().getBootFile();

        try {
            if(!handle.exists() || handle.isDirectory())
                handle.create();
            executable = KeletonKernel.getKlink().compile(SequenceUtil.readFrom(handle.openInput().orElseThrow(
                    () -> new IOException("InputStream access failure")
            )));
        } catch (IOException e) {
            throw new KeletonInternalException("Failed to access boot file", e);
        }

        try {
            if(executable == null)
                throw new IllegalStateException("Empty compilation");

            executable.execute(
                    KeletonKernel.getKlink(),
                    KeletonKernel.getBootEnv()
            );
        } catch (Exception e) {
            throw new KeletonInternalException("BOOT FAILURE", e);
        }

        launched = true;

        return true;
    }

    public synchronized boolean discoverModules() throws KeletonException
    {
        if(!initialized)
            return false;

        EmulatedHandleScanner scanner = new EmulatedHandleScanner(
                EmulatedAPIProvider.getEmulated().getModuleDirectory(),
                (LaunchClassLoader) this.getClass().getClassLoader(),
                KeletonKernel.getEventBus()
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

        scanner.registerClassAnnotationTriggers(
                Mountable.class,
                Pipeline.of(Mountable.class.getCanonicalName())
                        .then(new MountableVerifyingTrigger())
                        .then(new MountablePreloadingTrigger())
                        .then(new MountableRegistryTrigger(XMountAPIProvider.getManager()))
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

    private boolean initialized;

    private Fence moduleTriggersFence;

    private ModuleCollection moduleCollection;

    private static KeletonBootstraper bootstraper;
}
