package org.kucro3.keleton.kernel.loader.module;

import org.kucro3.keleton.kernel.KeletonKernel;
import org.kucro3.keleton.module.KeletonModule;
import org.kucro3.keleton.module.Module;
import org.kucro3.trigger.TerminalTrigger;
import org.kucro3.trigger.TriggerContext;
import org.spongepowered.api.event.cause.Cause;

public class KeletonModuleLoadCompletionTrigger implements TerminalTrigger {
    @Override
    public void trigger(TriggerContext context)
    {
        Module info = context.first(Module.class).get();
        KeletonModule module = context.first(KeletonModule.class).get();

        KeletonKernel.postEvent(new LoaderEventImpl.Discovered(Cause.source(info).build(), module, info));
    }
}
