package org.kucro3.trigger;

public class Pipeline {
    Pipeline(PipelineHead head)
    {
        this.head = head;
    }

    public static PipelineHead of(String name)
    {
        return new PipelineHead(name == null ? "" : name);
    }

    public void trigger(TriggerContext context)
    {
        head.trigger(context);
    }

    public String getName()
    {
        return head.name;
    }

    private final PipelineHead head;
}
