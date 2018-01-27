package org.kucro3.keleton.kernel.loader.event;

import org.kucro3.keleton.emulated.EmulatedHandle;

public class ModuleResourceFailureEvent {
    public ModuleResourceFailureEvent(EmulatedHandle handle, Throwable cause)
    {
        this.handle = handle;
        this.cause = cause;
    }

    public EmulatedHandle getHandle()
    {
        return handle;
    }

    public Throwable getCause()
    {
        return cause;
    }

    private final Throwable cause;

    private final EmulatedHandle handle;
}
