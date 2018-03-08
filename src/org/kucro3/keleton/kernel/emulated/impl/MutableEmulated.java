package org.kucro3.keleton.kernel.emulated.impl;

import org.kucro3.keleton.emulated.Emulated;
import org.kucro3.keleton.emulated.EmulatedHandle;
import org.kucro3.keleton.emulated.security.BootFileAccessPermission;
import org.kucro3.keleton.emulated.security.ModuleDirectoryAccessPermission;
import org.kucro3.keleton.security.ModuleAccessControl;
import org.kucro3.keleton.security.Sealed;

public class MutableEmulated implements Emulated {
    public MutableEmulated(EmulatedHandle bootFile)
    {
        this.bootFile = bootFile;
    }

    @Override
    public EmulatedHandle getHandle(String s)
    {
        if(root == null)
            throw new IllegalStateException("ROOT not initialized");

        return root.subHandle(s)
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    public EmulatedHandle getModuleDirectory()
    {
        ModuleAccessControl.checkPermission(
                new ModuleDirectoryAccessPermission());

        if(modules == null)
            throw new IllegalStateException("MODULES not intialized");

        return modules;
    }

    @Override
    public EmulatedHandle getBootFile()
    {
        ModuleAccessControl.checkPermission(
                new BootFileAccessPermission());

        return bootFile;
    }

    @Sealed
    public void setModules(EmulatedHandle modules)
    {
        this.modules = modules;
    }

    @Sealed
    public void setRoot(EmulatedHandle root)
    {
        this.root = root;
    }

    @Sealed
    private EmulatedHandle root;

    @Sealed
    private EmulatedHandle modules;

    @Sealed
    private final EmulatedHandle bootFile;
}
