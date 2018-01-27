package org.kucro3.trigger;

import java.util.Objects;

abstract class Appendable extends Triggerable {
    void check()
    {
        if(this.next != null)
            throw new IllegalStateException("Already connected");
    }

   PipelineNode then(NormalTrigger trigger, PipelineHead head)
    {
        Objects.requireNonNull(trigger, "trigger");
        check();

        PipelineNode node = new PipelineNode(trigger, head);
        this.next = node;
        return node;
    }

    PipelineTerminal then(TerminalTrigger trigger, PipelineHead head)
    {
        Objects.requireNonNull(trigger, "trigger");
        check();

        PipelineTerminal terminal = new PipelineTerminal(trigger, head);
        this.next = terminal;
        return terminal;
    }

    Triggerable next;
}
