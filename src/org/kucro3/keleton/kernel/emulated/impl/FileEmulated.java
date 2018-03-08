package org.kucro3.keleton.kernel.emulated.impl;

import org.kucro3.keleton.emulated.Emulated;
import org.kucro3.keleton.emulated.EmulatedHandle;
import org.kucro3.keleton.emulated.security.BootFileAccessPermission;
import org.kucro3.keleton.emulated.security.ModuleDirectoryAccessPermission;
import org.kucro3.keleton.security.ModuleAccessControl;
import org.kucro3.keleton.security.Sealed;

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
        ModuleAccessControl.checkPermission(
                new ModuleDirectoryAccessPermission());

        return moduleRoot;
    }

    @Override
    public EmulatedHandle getBootFile()
    {
        ModuleAccessControl.checkPermission(
                new BootFileAccessPermission());

        return bootFile;
    }

    @Sealed
    private final FileEmulatedHandle root;

    @Sealed
    private final FileEmulatedHandle moduleRoot;

    @Sealed
    private final FileEmulatedHandle bootFile;
}
