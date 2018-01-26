package org.kucro3.keleton.kernel.emulated.impl;

import org.kucro3.keleton.kernel.emulated.Emulated;
import org.kucro3.keleton.kernel.emulated.EmulatedHandle;

public class LocalEmulated implements Emulated {
    @Override
    public EmulatedHandle getHandle(String path)
    {
        return null;
    }

    @Override
    public EmulatedHandle getModuleDirectory()
    {
        return null;
    }

    @Override
    public EmulatedHandle getBootFile()
    {
        return null;
    }
}
