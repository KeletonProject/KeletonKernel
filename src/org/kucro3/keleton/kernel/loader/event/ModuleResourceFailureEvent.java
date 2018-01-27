package org.kucro3.keleton.kernel.loader.event;

import org.kucro3.keleton.emulated.EmulatedHandle;

public class ModuleResourceFailureEvent {
    public ModuleResourceFailureEvent(EmulatedHandle handle)
    {
        this.handle = handle;
    }

    public EmulatedHandle getHandle()
    {
        return handle;
    }

    private final EmulatedHandle handle;
}
