package org.kucro3.keleton.kernel.module;

import java.util.Collection;
import java.util.HashSet;

public class ModuleCollection {
    public ModuleCollection()
    {
    }

    public void addModule(KeletonModuleImpl impl)
    {
        modules.add(impl);
        ids.add(impl.getId());
    }

    public boolean hasModule(String id)
    {
        return ids.contains(id);
    }

    public Collection<KeletonModuleImpl> getModules()
    {
        return modules;
    }

    private final Collection<String> ids = new HashSet<>();

    private final Collection<KeletonModuleImpl> modules = new HashSet<>();
}
