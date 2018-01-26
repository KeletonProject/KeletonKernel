package org.kucro3.keleton.kernel.emulated.impl;

import java.io.*;

import org.kucro3.keleton.kernel.emulated.Emulated;
import org.kucro3.keleton.kernel.emulated.EmulatedHandle;

public class FileEmulated implements Emulated {
    public FileEmulated(File root, File moduleRoot, File bootFile)
    {
        this.root = root;
        this.moduleRoot = new ImmutableFileEmulatedHandle(moduleRoot);
        this.bootFile = new ReadOnlyFileEmulatedHandle(bootFile);
    }

    @Override
    public EmulatedHandle getHandle(String path)
    {
        return new FileEmulatedHandle(new File(root, path), root, true, true, true);
    }

    @Override
    public EmulatedHandle getModuleDirectory()
    {
        return moduleRoot;
    }

    @Override
    public EmulatedHandle getBootFile()
    {
        return bootFile;
    }

    private final File root;

    private final FileEmulatedHandle moduleRoot;

    private final FileEmulatedHandle bootFile;
}
