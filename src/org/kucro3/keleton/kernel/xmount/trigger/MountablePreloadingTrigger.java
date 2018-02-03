package org.kucro3.keleton.kernel.xmount.trigger;

import org.kucro3.trigger.Gradation;
import org.kucro3.trigger.GradationalTrigger;
import org.kucro3.trigger.NormalTrigger;
import org.kucro3.trigger.TriggerContext;

public class MountablePreloadingTrigger implements NormalTrigger, GradationalTrigger {
    @Override
    public boolean trigger(TriggerContext context) throws Exception
    {
        return false;
    }

    @Override
    public Gradation getGradation()
    {
        return MountableTriggerGradation.PRELOADING;
    }
}
