package org.kucro3.keleton.kernel.emulated.impl;

import org.kucro3.keleton.emulated.Emulated;
import org.kucro3.keleton.emulated.EmulatedHandle;

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
        if(modules == null)
            throw new IllegalStateException("MODULES not intialized");

        return modules;
    }

    @Override
    public EmulatedHandle getBootFile()
    {
        return bootFile;
    }

    public void setModules(EmulatedHandle modules)
    {
        this.modules = modules;
    }

    public void setRoot(EmulatedHandle root)
    {
        this.root = root;
    }

    private EmulatedHandle root;

    private EmulatedHandle modules;

    private final EmulatedHandle bootFile;
}
