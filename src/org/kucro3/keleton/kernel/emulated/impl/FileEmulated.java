package org.kucro3.keleton.kernel.emulated.impl;

import org.kucro3.keleton.emulated.Emulated;
import org.kucro3.keleton.emulated.EmulatedHandle;

import java.io.*;

public class FileEmulated implements Emulated {
    public FileEmulated(File root, File moduleRoot, File bootFile)
    {
        this.root = new ImmutableFileEmulatedHandle(root, root);
        this.moduleRoot = new ImmutableFileEmulatedHandle(moduleRoot, root);
        this.bootFile = new ReadOnlyFileEmulatedHandle(bootFile, root);
    }

    @Override
    public EmulatedHandle getHandle(String path)
    {
        return root.subHandle(path)
                .orElseThrow(IllegalStateException::new);
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

    private final FileEmulatedHandle root;

    private final FileEmulatedHandle moduleRoot;

    private final FileEmulatedHandle bootFile;
}
