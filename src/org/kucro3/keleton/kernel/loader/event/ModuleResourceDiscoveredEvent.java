package org.kucro3.keleton.kernel.loader.event;

import org.kucro3.keleton.emulated.EmulatedHandle;

public class ModuleResourceDiscoveredEvent {
    public ModuleResourceDiscoveredEvent(EmulatedHandle handle)
    {
        this.handle = handle;
    }

    public void setRegistered(boolean registered)
    {
        this.registered = registered;
    }

    public boolean isRegistered()
    {
        return registered;
    }

    public EmulatedHandle getHandle()
    {
        return handle;
    }

    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    public boolean isCancelled()
    {
        return cancelled;
    }

    private final EmulatedHandle handle;

    private boolean registered;

    private boolean cancelled;
}
