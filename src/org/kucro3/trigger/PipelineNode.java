package org.kucro3.trigger;

public final class PipelineNode extends Appendable {
    PipelineNode(NormalTrigger trigger, PipelineHead head, Fence fence)
    {
        this.trigger = trigger;
        this.head = head;
        this.fence = fence;

        if(fence != null)
            fence.initializeOwner(this);
    }

    public PipelineNode then(NormalTrigger trigger)
    {
        return then(trigger, null);
    }

    public PipelineNode then(NormalTrigger trigger, Fence fence)
    {
        return super.then(trigger, head, fence);
    }

    public PipelineTerminal then(TerminalTrigger trigger)
    {
        return then(trigger, null);
    }

    public PipelineTerminal then(TerminalTrigger trigger, Fence fence)
    {
        return super.then(trigger, head, fence);
    }

    @Override
    void trigger(TriggerContext context)
    {
        if(fence != null && !fence.dismantling && !fence.isDismantled())
            fence.fence(context);
        else try {
            if (trigger.trigger(context))
                super.next.trigger(context);
        } catch (TriggerException unhandled) {
            throw unhandled;
        } catch (Exception e) {
            if(!head.handlers.handle(this, e))
                throw new TriggerException(this, e);
        }
    }

    private final Fence fence;

    private final PipelineHead head;

    private final NormalTrigger trigger;
}
