package org.kucro3.keleton.kernel.emulated.impl;

import java.io.File;

public class WriteOnlyFileEmulatedHandle extends FileEmulatedHandle {
    WriteOnlyFileEmulatedHandle(File file)
    {
        super(file, false, true, false);
    }

    WriteOnlyFileEmulatedHandle(File file, File root)
    {
        super(file, root, false, true, false);
    }
}
