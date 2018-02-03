package org.kucro3.keleton.kernel.emulated.impl;

import java.io.File;

public class WriteOnlyFileEmulatedHandle extends FileEmulatedHandle {
    public WriteOnlyFileEmulatedHandle(File file)
    {
        super(file, false, true, false);
    }

    public WriteOnlyFileEmulatedHandle(File file, File root)
    {
        super(file, root, false, true, false);
    }
}
