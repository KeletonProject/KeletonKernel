package org.kucro3.trigger;

public class TriggerException extends RuntimeException {
    public TriggerException()
    {
    }

    public TriggerException(String message)
    {
        super(message);
    }

    public TriggerException(Throwable cause)
    {
        super(cause);
    }

    public TriggerException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
