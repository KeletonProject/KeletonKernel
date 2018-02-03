package org.kucro3.trigger;

public class PipelineHead extends Appendable {
    PipelineHead(String name, ExceptionHandlerGroup handlers)
    {
        this.name = name;
        this.handlers = handlers;
    }

    public PipelineNode then(NormalTrigger trigger)
    {
        return then(trigger, null);
    }

    public PipelineNode then(NormalTrigger trigger, Fence fence)
    {
        return super.then(trigger, this, fence);
    }

    public PipelineTerminal then(TerminalTrigger trigger)
    {
        return then(trigger, null);
    }

    public PipelineTerminal then(TerminalTrigger trigger, Fence fence)
    {
        return super.then(trigger, this, fence);
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

    final ExceptionHandlerGroup handlers;
}
