package org.kucro3.keleton.kernel.xmount.trigger;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.kucro3.keleton.klink.xmount.Mounter;
import org.kucro3.trigger.Gradation;
import org.kucro3.trigger.GradationalTrigger;
import org.kucro3.trigger.NormalTrigger;
import org.kucro3.trigger.TriggerContext;
import org.objectweb.asm.tree.ClassNode;

public class MountablePreloadingTrigger implements NormalTrigger, GradationalTrigger {
    @Override
    public boolean trigger(TriggerContext context) throws Exception
    {
        ClassNode cn = context.first(ClassNode.class).get();
        LaunchClassLoader loader = context.first(LaunchClassLoader.class).get();

        Class<?> clazz = loader.findClass(cn.name.replace('/', '.'));
        Mounter mounter = (Mounter) clazz.newInstance();

        context.set("loaded", mounter);

        return true;
    }

    @Override
    public Gradation getGradation()
    {
        return MountableTriggerGradation.PRELOADING;
    }
}
