package org.kucro3.keleton.kernel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.kucro3.keleton.api.APIProvider;
import org.kucro3.keleton.exception.KeletonException;
import org.kucro3.keleton.kernel.api.KeletonAPIManagerImpl;
import org.kucro3.keleton.kernel.module.loader.KeletonBootstraper;
import org.kucro3.keleton.kernel.module.loader.KeletonModuleManagerImpl;
import org.kucro3.keleton.module.event.KeletonLoaderEvent;
import org.kucro3.keleton.module.event.KeletonModuleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

import java.util.Arrays;

@APIProvider
public class KeletonKernel extends DummyModContainer {
    public KeletonKernel() throws Exception
    {
        super(new ModMetadata());

        ModMetadata metadata = getMetadata();
        metadata.modId = "keletonkernel";
        metadata.name = "KeletonKernel";
        metadata.version = "1.0";
        metadata.authorList = Arrays.asList("Kumonda221");

        logger = LoggerFactory.getLogger("keletonkernel");
        manager = new KeletonModuleManagerImpl();
        apimanager = new KeletonAPIManagerImpl();

        apimanager.export("kernel", this.getClass());
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        (eventBus = bus).register(this);
        return true;
    }

    @Subscribe
    public void onPreInitialization(FMLPreInitializationEvent event) throws KeletonException
    {
        new KeletonBootstraper();

        KeletonBootstraper.getBootstraper().bootstrap();
    }

    @Subscribe
    public void onDiscoverModule(KeletonLoaderEvent.Discovered event)
    {
        logger.info("Discovered module: " + event.getModule().getId());
    }

    @Subscribe
    public void onStateChange(KeletonModuleEvent.StateTransformation.Transformed event)
    {
        logger.info("State of module \"" + event.getModule().getId() + "\" has been transformed from " + event.from().name() + " to " + event.to().name());
    }

    public static void postEvent(Event event)
    {
        eventBus.post(event);

        if(event instanceof Cancellable)
            if(((Cancellable) event).isCancelled())
                return;

        Sponge.getEventManager().post(event);
    }

    public static KeletonModuleManagerImpl getModuleManagerImpl()
    {
        return manager;
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
}
