package org.kucro3.trigger;

public class TriggerException extends RuntimeException {
    public TriggerException(Object source)
    {
        this.source = source;
    }

    public TriggerException(Object source, String message)
    {
        super(message);
        this.source = source;
    }

    public TriggerException(Object source, Throwable cause)
    {
        super(cause);
        this.source = source;
    }

    public TriggerException(Object source, String message, Throwable cause)
    {
        super(message, cause);
        this.source = source;
    }

    public Object getSourceTrigger()
    {
        return source;
    }

    private final Object source;
}
