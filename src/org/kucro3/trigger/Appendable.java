package org.kucro3.trigger;

abstract class Appendable extends Triggerable {
    void check()
    {
        if(this.next != null)
            throw new IllegalStateException("Already connected");
    }

   PipelineNode then(NormalTrigger trigger, PipelineHead head, Fence fence)
    {
        check();

        PipelineNode node = new PipelineNode(trigger, head, fence);
        this.next = node;
        return node;
    }

    PipelineTerminal then(TerminalTrigger trigger, PipelineHead head, Fence fence)
    {
        check();

        PipelineTerminal terminal = new PipelineTerminal(trigger, head, fence);
        this.next = terminal;
        return terminal;
    }

    Triggerable next;
}
