package org.kucro3.trigger;

public final class PipelineNode extends Appendable {
    PipelineNode(NormalTrigger trigger, PipelineHead head)
    {
        this.trigger = trigger;
        this.head = head;
    }

    public PipelineNode then(NormalTrigger trigger)
    {
        return super.then(trigger, head);
    }

    public PipelineTerminal then(TerminalTrigger trigger)
    {
        return super.then(trigger, head);
    }

    @Override
    void trigger(TriggerContext context)
    {
        if(trigger.trigger(context))
            super.next.trigger(context);
    }

    private final PipelineHead head;

    private final NormalTrigger trigger;
}
