package org.kucro3.keleton.kernel.emulated.impl;

import java.io.File;

public class LocalEmulated extends FileEmulated {
    public LocalEmulated()
    {
        super(ROOT, MODULE_ROOT, BOOT_FILE);
    }

    private static final File ROOT = new File(".\\keleton");

    private static final File MODULE_ROOT = new File(".\\keleton\\modules");

    private static final File BOOT_FILE = new File(".\\keleton\\boot.klnk");
}
