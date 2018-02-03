package org.kucro3.keleton.kernel.xmount.trigger;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.kucro3.keleton.klink.xmount.Mountable;
import org.kucro3.keleton.klink.xmount.Mounter;
import org.kucro3.trigger.Gradation;
import org.kucro3.trigger.GradationalTrigger;
import org.kucro3.trigger.NormalTrigger;
import org.kucro3.trigger.TriggerContext;
import org.objectweb.asm.tree.ClassNode;

import java.util.Objects;

public class MountablePreloadingTrigger implements NormalTrigger, GradationalTrigger {
    @Override
    public boolean trigger(TriggerContext context) throws Exception
    {
        ClassNode cn = context.first(ClassNode.class).get();
        LaunchClassLoader loader = context.first(LaunchClassLoader.class).get();

        Class<?> clazz = loader.findClass(cn.name.replace('/', '.'));
        Mounter mounter = (Mounter) clazz.newInstance();
        Mountable info = Objects.requireNonNull(clazz.getAnnotation(Mountable.class), "info");

        context.set("loaded", mounter);
        context.set("info", info);

        return true;
    }

    @Override
    public Gradation getGradation()
    {
        return MountableTriggerGradation.PRELOADING;
    }
}
