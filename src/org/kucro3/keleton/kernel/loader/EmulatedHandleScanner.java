package org.kucro3.keleton.kernel.loader;

import com.google.common.eventbus.EventBus;
import org.kucro3.keleton.emulated.EmulatedHandle;

public class EmulatedHandleScanner {
    public EmulatedHandleScanner(EmulatedHandle handle)
    {
        if(!handle.exists())
            handle.makeDirectory();
        else if(!handle.isDirectory())
            throw new IllegalArgumentException("Not a directory: " + handle.getPath());

        this.handle = handle;
    }

    public void scan()
    {
        EmulatedHandle[] handles = handle.listHandles((h) -> h.getPath().endsWith(".jar"));

        for(EmulatedHandle handle : handles)
            if(!handle.isDirectory())
            {

            }
    }

    public EmulatedHandle getHandle()
    {
        return handle;
    }

    public void registerListener(Object listener)
    {
        bus.register(listener);
    }

    public void unregisterListener(Object listener)
    {
        bus.unregister(listener);
    }



    private final EmulatedHandle handle;

    private final EventBus bus = new EventBus(this.toString());
}
