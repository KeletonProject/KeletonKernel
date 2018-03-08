package org.kucro3.keleton.kernel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.*;
import org.kucro3.keleton.exception.KeletonException;
import org.kucro3.keleton.kernel.api.KeletonAPIManagerImpl;
import org.kucro3.keleton.kernel.emulated.EmulatedAPIProvider;
import org.kucro3.keleton.kernel.klink.kernel.XMounter_kernel;
import org.kucro3.keleton.kernel.klink.kernel.emulation.XMounter_kernel_emulation;
import org.kucro3.keleton.kernel.loader.event.ModuleResourceFailureEvent;
import org.kucro3.keleton.kernel.mac.MACAPIProvider;
import org.kucro3.keleton.kernel.module.KeletonModuleManagerImpl;
import org.kucro3.keleton.kernel.xmount.XMountAPIProvider;
import org.kucro3.keleton.kernel.xmount.XMountManagerImpl;
import org.kucro3.keleton.module.event.KeletonLoaderEvent;
import org.kucro3.keleton.module.event.KeletonModuleEvent;
import org.kucro3.keleton.security.Sealed;
import org.kucro3.klink.CompileMode;
import org.kucro3.klink.Environment;
import org.kucro3.klink.Klink;
import org.kucro3.klink.functional.KlinkFunctionRegistry;
import org.kucro3.klink.functional.NativeFunctionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

import java.util.Collections;

@Sealed
public class KeletonKernel extends DummyModContainer {
    public KeletonKernel() throws Exception
    {
        super(new ModMetadata());

        ModMetadata metadata = getMetadata();
        metadata.modId = "keletonkernel";
        metadata.name = "KeletonKernel";
        metadata.version = "1.0";
        metadata.authorList = Collections.singletonList("Kumonda221");

        logger = LoggerFactory.getLogger("keletonkernel");
        manager = new KeletonModuleManagerImpl();
        apimanager = new KeletonAPIManagerImpl();

        apimanager.export(KernelAPIProvider.class);
        apimanager.export(KernelAPIManagerProvider.class);
        apimanager.export(EmulatedAPIProvider.class);
        apimanager.export(XMountAPIProvider.class);
        apimanager.export(MACAPIProvider.class);

        klink = new Klink();
        bootEnv = klink.createEnv("BOOT");
        runtimeEnv = klink.createEnv("RUNTIME");

        klink.setCompileMode(CompileMode.FIRST_EXECUTE);

        klink.provideService(KlinkFunctionRegistry.class, new KlinkFunctionRegistry());
        klink.provideService(NativeFunctionRegistry.class, new NativeFunctionRegistry());

        XMountManagerImpl xmount = XMountAPIProvider.getManager();
        xmount.initialize(klink);
        xmount.putMountable(XMounter_kernel.instance());
        xmount.putMountable(XMounter_kernel_emulation.instance());

        bootstraper = new KeletonBootstraper();
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        (eventBus = bus).register(this);
        return true;
    }

    @Subscribe
    public void onConstruction(FMLConstructionEvent event) throws KeletonException
    {
        bootstraper.initialize();
    }

    @Subscribe
    public void onPreInitialization(FMLPreInitializationEvent event) throws KeletonException
    {
        bootstraper.bootstrap();
    }

    @Subscribe
    public void onInitialization(FMLInitializationEvent event) throws KeletonException
    {
        bootstraper.loadModules();
    }

    @Subscribe
    public void onPostInitialization(FMLPostInitializationEvent event) throws KeletonException
    {
        bootstraper.enableModules();
    }

    @Subscribe
    public void onStopping(FMLServerStoppingEvent event) throws KeletonException
    {
        bootstraper.disableModules();
        bootstraper.destroyModules();
    }

    @Subscribe
    public void onDiscoverModule(KeletonLoaderEvent.Discovered event)
    {
        logger.info("Discovered module: " + event.getModule().getId());
    }

    @Subscribe
    public void onModuleFailure(KeletonLoaderEvent.Failed event)
    {
        String message = "Failed to load module: " + event.getInfo().id();
        if(event.getException().isPresent())
            logger.error(message, event.getException().get());
        else
            logger.error(message);
    }

    @Subscribe
    public void onStateChange(KeletonModuleEvent.StateTransformation.Transformed event)
    {
        logger.info("State of module \"" + event.getModule().getId() + "\" has been transformed from " + event.from().name() + " to " + event.to().name());
    }

    @Subscribe
    public void onStateChangeIgnored(KeletonModuleEvent.StateTransformation.Ignored event)
    {
        logger.info("Ignored state transformation of module \"" + event.getModule().getId() + "\": " + event.getMessage());
    }

    @Subscribe
    public void onResourceFailure(ModuleResourceFailureEvent event)
    {
        logger.error("Failed to load resource: " + event.getHandle().getPath(), event.getCause());
    }

    public static <T extends Event> T postEvent(T event)
    {
        eventBus.post(event);

        if(event instanceof Cancellable)
            if(((Cancellable) event).isCancelled())
                return event;

        Sponge.getEventManager().post(event);

        return event;
    }

    public static LaunchClassLoader getLaunchClassLoader()
    {
        return (LaunchClassLoader) KeletonKernel.class.getClassLoader();
    }

    public static KeletonModuleManagerImpl getModuleManagerImpl()
    {
        return manager;
    }

    public static Klink getKlink()
    {
        return klink;
    }

    public static Environment getRuntimeEnv()
    {
        return runtimeEnv;
    }

    public static Environment getBootEnv()
    {
        return bootEnv;
    }

    public static EventBus getEventBus()
    {
        return eventBus;
    }

    public static KeletonAPIManagerImpl getAPIManagerImpl()
    {
        return apimanager;
    }

    public static Logger getLogger()
    {
        return logger;
    }

    private static KeletonAPIManagerImpl apimanager;

    private static KeletonModuleManagerImpl manager;

    private static EventBus eventBus;

    private static Logger logger;

    private static Klink klink;

    private static Environment bootEnv;

    private static Environment runtimeEnv;

    private static KeletonBootstraper bootstraper;
}
