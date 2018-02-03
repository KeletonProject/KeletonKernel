package org.kucro3.trigger;

public class Pipeline {
    Pipeline(PipelineHead head)
    {
        this.head = head;
    }

    public static PipelineHead of() {return of(null);}

    public static PipelineHead of(String name)
    {
        return of(name, ExceptionHandlerGroup.of());
    }

    public static PipelineHead of(String name, ExceptionHandlerGroup handlers)
    {
        return new PipelineHead(name == null ? "" : name, handlers);
    }

    public void trigger(TriggerContext context)
    {
        head.trigger(context);
    }

    public String getName()
    {
        return head.name;
    }

    public ExceptionHandlerGroup getHandlerGroup()
    {
        return head.handlers;
    }

    private final PipelineHead head;
}
