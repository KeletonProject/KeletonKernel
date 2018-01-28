package org.kucro3.keleton.kernel.loader.klink;

import org.kucro3.keleton.kernel.asm.AnnotationUtil;
import org.kucro3.trigger.NormalTrigger;
import org.kucro3.trigger.TriggerContext;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

@SuppressWarnings("unchecked")
public class KlinkLibraryConvertingTrigger implements NormalTrigger {
    @Override
    public boolean trigger(TriggerContext context)
    {
        try {
            ClassNode cn = context.first(ClassNode.class).get();
            AnnotationNode an = context.first(AnnotationNode.class).get();

            Map<String, Object> values = AnnotationUtil.values(an);

            if(!values.containsKey("value"))
                return true;

            String namespace = (String) values.get("value");
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
