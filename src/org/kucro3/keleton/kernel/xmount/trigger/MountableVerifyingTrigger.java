package org.kucro3.keleton.kernel.xmount.trigger;

import org.kucro3.keleton.kernel.asm.AnnotationUtil;
import org.kucro3.keleton.kernel.xmount.XMountAPIProvider;
import org.kucro3.keleton.kernel.xmount.XMountManagerImpl;
import org.kucro3.trigger.NormalTrigger;
import org.kucro3.trigger.TriggerContext;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.Map;

public class MountableVerifyingTrigger implements NormalTrigger {
    @Override
    public boolean trigger(TriggerContext context)
    {
        AnnotationNode an = context.first(AnnotationNode.class).get();
        Map<String, Object> values = AnnotationUtil.values(an);

        String name = (String) values.get("name");

        if(name == null)
            return false;

        XMountManagerImpl impl = XMountAPIProvider.getManager();

        if(impl.hasMoutable(name) || impl.isMounted(name))
            throw new IllegalStateException("Duplicated mountable object: " + name);

        return false;
    }
}
