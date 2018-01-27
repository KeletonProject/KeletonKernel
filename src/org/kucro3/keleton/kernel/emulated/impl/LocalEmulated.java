package org.kucro3.keleton.kernel.emulated.impl;

import java.io.File;

public class LocalEmulated extends FileEmulated {
    public LocalEmulated(File root, File moduleRoot, File bootFile)
    {
        super(root, moduleRoot, bootFile);
    }
}
