package org.kucro3.keleton.kernel.loader.klink;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.kucro3.keleton.exception.KeletonInternalException;
import org.kucro3.trigger.NormalTrigger;
import org.kucro3.trigger.TriggerContext;
import org.objectweb.asm.tree.ClassNode;

public class KlinkLibraryPreloadingTrigger implements NormalTrigger {
    @Override
    public boolean trigger(TriggerContext context)
    {
        try {
            ClassNode cn = context.first(ClassNode.class).get();
            LaunchClassLoader loader = context.first(LaunchClassLoader.class).get();

            context.set("loaded", loader.findClass(cn.name.replace('/', '.')));
        } catch (Exception e) {
            throw new KeletonInternalException(e);
        }

        return true;
    }
}
