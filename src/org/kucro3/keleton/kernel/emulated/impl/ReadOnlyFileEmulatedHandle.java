package org.kucro3.keleton.kernel.emulated.impl;

import java.io.File;

public class ReadOnlyFileEmulatedHandle extends FileEmulatedHandle {
    ReadOnlyFileEmulatedHandle(File file)
    {
        super(file, true, false, false);
    }

    ReadOnlyFileEmulatedHandle(File file, File root)
    {
        super(file, root, true, false, false);
    }
}
