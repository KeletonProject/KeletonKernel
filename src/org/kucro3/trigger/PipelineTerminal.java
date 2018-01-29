package org.kucro3.trigger;

public final class PipelineTerminal extends Triggerable {
    PipelineTerminal(TerminalTrigger trigger, PipelineHead head, Fence fence)
    {
        this.trigger = trigger;
        this.head = head;
        this.fence = fence;
    }

    public Pipeline end()
    {
        return new Pipeline(head);
    }

    @Override
    void trigger(TriggerContext context)
    {
        if(fence != null && !fence.dismantling && !fence.isDismantled())
            fence.fence(context);
        else
            trigger.trigger(context);
    }

    private final Fence fence;

    private final PipelineHead head;

    private final TerminalTrigger trigger;
}
