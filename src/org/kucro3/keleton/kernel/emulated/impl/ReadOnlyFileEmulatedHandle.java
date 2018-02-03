package org.kucro3.keleton.kernel.emulated.impl;

import java.io.File;

public class ReadOnlyFileEmulatedHandle extends FileEmulatedHandle {
    public ReadOnlyFileEmulatedHandle(File file)
    {
        super(file, true, false, false);
    }

    public ReadOnlyFileEmulatedHandle(File file, File root)
    {
        super(file, root, true, false, false);
    }
}
