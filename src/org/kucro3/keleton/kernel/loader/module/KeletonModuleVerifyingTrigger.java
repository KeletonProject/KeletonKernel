package org.kucro3.keleton.kernel.loader.module;

import com.google.common.eventbus.EventBus;
import org.kucro3.keleton.kernel.asm.AnnotationUtil;
import org.kucro3.keleton.kernel.loader.event.ModuleResourceDiscoveredEvent;
import org.kucro3.keleton.kernel.loader.event.ModuleResourceDuplicationEvent;
import org.kucro3.keleton.kernel.module.ModuleCollection;
import org.kucro3.trigger.NormalTrigger;
import org.kucro3.trigger.TriggerContext;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.HashMap;
import java.util.Iterator;
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
        ModuleResourceDiscoveredEvent event = context.first(ModuleResourceDiscoveredEvent.class).get();

        Map<String, Object> values = AnnotationUtil.values(an);

        Object idObject = values.get("id");
        if(idObject == null)
        {
            event.setCancelled(true);
            return false;
        }

        String id = (String) idObject;
        if(collection.hasModule(id))
        {
            bus.post(new ModuleResourceDuplicationEvent(id));

            event.setCancelled(true);
            return false;
        }

        return true;
    }

    private final ModuleCollection collection;
}
