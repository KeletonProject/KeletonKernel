package org.kucro3.trigger;

public class PipelineHead extends Appendable {
    PipelineHead(String name)
    {
        this.name = name;
    }

    public PipelineNode then(NormalTrigger trigger)
    {
        return super.then(trigger, this);
    }

    public PipelineTerminal then(TerminalTrigger trigger)
    {
        return super.then(trigger, this);
    }

    public String getName()
    {
        return name;
    }

    @Override
    void trigger(TriggerContext context)
    {
        super.next.trigger(context);
    }

    String name;
}
