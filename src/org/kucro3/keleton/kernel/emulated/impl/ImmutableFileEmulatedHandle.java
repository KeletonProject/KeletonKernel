package org.kucro3.keleton.kernel.emulated.impl;

import java.io.File;

public class ImmutableFileEmulatedHandle extends FileEmulatedHandle {
    public ImmutableFileEmulatedHandle(File file)
    {
        super(file, false, false, false);
    }

    public ImmutableFileEmulatedHandle(File file, File root)
    {
        super(file, root, false, false, false);
    }
}
