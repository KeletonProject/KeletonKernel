package org.kucro3.trigger;

import java.util.ArrayList;
import java.util.List;

public class Fence {
    public Fence()
    {
    }

    public boolean dismantleIfOwned()
    {
        if(triggerable == null)
            return false;
        dismantle();
        return true;
    }

    public synchronized void dismantle()
    {
        try {
            dismantling = true;

            if (dismantled)
                throw new IllegalStateException("Already dismantled");

            if (triggerable == null)
                throw new IllegalStateException("Not owned by a trigger");

            for (TriggerContext context : fenced)
                triggerable.trigger(context);

            fenced.clear();
            dismantled = true;
        } finally {
            dismantling = false;
        }
    }

    public boolean isDismantled()
    {
        return dismantled;
    }

    void initializeOwner(Triggerable triggerable)
    {
        if(this.triggerable != null)
            throw new IllegalStateException("Already owned by a trigger");
        this.triggerable = triggerable;
    }

    void fence(TriggerContext context)
    {
        fenced.add(context);
    }

    private boolean dismantled;

    boolean dismantling;

    private List<TriggerContext> fenced = new ArrayList<>();

    private Triggerable triggerable;
}
