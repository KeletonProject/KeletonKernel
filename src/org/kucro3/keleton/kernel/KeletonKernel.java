package org.kucro3.keleton.kernel;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

import java.util.Arrays;

public class KeletonKernel extends DummyModContainer {
    public KeletonKernel()
    {
        super(new ModMetadata());

        ModMetadata metadata = getMetadata();
        metadata.modId = "keletonkernel";
        metadata.name = "KeletonKernel";
        metadata.version = "1.0";
        metadata.authorList = Arrays.asList("Kumonda221");
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        (eventBus = bus).register(this);
        return true;
    }

    public static void postEvent(Event event)
    {
        eventBus.post(event);

        if(event instanceof Cancellable)
            if(((Cancellable) event).isCancelled())
                return;

        Sponge.getEventManager().post(event);
    }

    private static EventBus eventBus;
}
