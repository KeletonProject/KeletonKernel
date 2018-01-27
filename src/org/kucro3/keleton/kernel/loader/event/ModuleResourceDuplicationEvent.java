package org.kucro3.keleton.kernel.loader.event;

public class ModuleResourceDuplicationEvent {
    public ModuleResourceDuplicationEvent(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    private final String id;
}
