package org.kucro3.keleton.kernel.xmount.trigger;

import com.theredpixelteam.redtea.trigger.Gradation;
import com.theredpixelteam.redtea.trigger.GradationalTrigger;
import com.theredpixelteam.redtea.trigger.NormalTrigger;
import com.theredpixelteam.redtea.trigger.TriggerContext;
import org.kucro3.keleton.kernel.asm.AnnotationUtil;
import org.kucro3.keleton.kernel.xmount.XMountAPIProvider;
import org.kucro3.keleton.kernel.xmount.XMountManagerImpl;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.Map;

public class MountableVerifyingTrigger implements NormalTrigger, GradationalTrigger {
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

    @Override
    public Gradation getGradation()
    {
        return MountableTriggerGradation.VERIFYING;
    }
}
