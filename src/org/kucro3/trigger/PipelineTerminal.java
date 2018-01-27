package org.kucro3.trigger;

public final class PipelineTerminal extends Triggerable {
    PipelineTerminal(TerminalTrigger trigger, PipelineHead head)
    {
        this.trigger = trigger;
        this.head = head;
    }

    public Pipeline end()
    {
        return new Pipeline(head);
    }

    @Override
    void trigger(TriggerContext context)
    {
        trigger.trigger(context);
    }

    private final PipelineHead head;

    private final TerminalTrigger trigger;
}
