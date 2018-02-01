package org.kucro3.keleton.kernel.emulated.impl;

import java.io.File;

public class CustomEmulated extends FileEmulated {
    public CustomEmulated(File root, File moduleRoot, File bootFile)
    {
        super(root, moduleRoot, bootFile);
    }
}
