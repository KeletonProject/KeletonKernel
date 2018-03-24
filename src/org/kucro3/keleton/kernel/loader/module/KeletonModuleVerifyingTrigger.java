package org.kucro3.keleton.kernel.loader.module;

import com.google.common.eventbus.EventBus;
import com.theredpixelteam.redtea.trigger.NormalTrigger;
import com.theredpixelteam.redtea.trigger.TriggerContext;
import org.kucro3.keleton.kernel.asm.AnnotationUtil;
import org.kucro3.keleton.kernel.loader.event.ModuleResourceDuplicationEvent;
import org.kucro3.keleton.kernel.module.ModuleCollection;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.Map;

@SuppressWarnings("unchecked")
public class KeletonModuleVerifyingTrigger implements NormalTrigger {
    public KeletonModuleVerifyingTrigger(ModuleCollection collection)
    {
        this.collection = collection;
    }

    @Override
    public boolean trigger(TriggerContext context)
    {
        EventBus bus = context.first(EventBus.class).get();
        AnnotationNode an = context.first(AnnotationNode.class).get();

        Map<String, Object> values = AnnotationUtil.values(an);

        Object idObject = values.get("id");
        if(idObject == null)
            return false;

        String id = (String) idObject;
        if(collection.hasModule(id))
        {
            bus.post(new ModuleResourceDuplicationEvent(id));
            return false;
        }

        return true;
    }

    private final ModuleCollection collection;
}
