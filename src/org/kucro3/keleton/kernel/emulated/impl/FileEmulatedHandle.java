package org.kucro3.keleton.kernel.emulated.impl;

import org.kucro3.keleton.kernel.emulated.EmulatedHandle;

import java.io.*;
import java.util.Optional;

public class FileEmulatedHandle implements EmulatedHandle {
    FileEmulatedHandle(File file, boolean canRead, boolean canWrite, boolean canDelete)
    {
        this(file, null, canRead, canWrite, canDelete);
    }

    FileEmulatedHandle(File file, File root, boolean canRead, boolean canWrite, boolean canDelete)
    {
        this.file = file;
        this.root = root;
        this.canDelete = canDelete;
        this.canRead = canRead;
        this.canWrite = canWrite;
    }

    @Override
    public EmulatedHandle[] listHandles()
    {
        return new EmulatedHandle[0];
    }

    @Override
    public EmulatedHandle[] listHandles(HandleFilter filter)
    {
        return new EmulatedHandle[0];
    }

    @Override
    public boolean makeDirectory()
    {
        return file.mkdirs();
    }

    @Override
    public boolean isDirectory()
    {
        return file.isDirectory();
    }

    @Override
    public boolean exists()
    {
        return file.exists();
    }

    @Override
    public String getName()
    {
        return file.getName();
    }

    @Override
    public String getPath()
    {
        return file.getPath().replaceFirst(root.getPath().replace("\\", "\\\\"), "");
    }

    @Override
    public Optional<EmulatedHandle> getParentHandle()
    {
        File parent = file.getParentFile();
        if(parent.equals(root))
            return Optional.empty();
        return Optional.of(new FileEmulatedHandle(parent, root, false, false , true));
    }

    @Override
    public boolean canDelete()
    {
        return canDelete;
    }

    @Override
    public boolean canRead()
    {
        return canRead;
    }

    @Override
    public boolean canWrite()
    {
        return canWrite;
    }

    @Override
    public boolean delete()
    {
        if(!canDelete)
            return false;
        return file.delete();
    }

    @Override
    public Optional<InputStream> openInput() throws IOException
    {
        if(!canRead)
            return Optional.empty();
        return Optional.of(new BufferedInputStream(new FileInputStream(file)));
    }

    @Override
    public Optional<OutputStream> openOutput() throws IOException
    {
        if(!canWrite)
            return Optional.empty();
        return Optional.of(new BufferedOutputStream(new FileOutputStream(file)));
    }

    @Override
    public void create() throws IOException
    {
        file.createNewFile();
    }

    private final File root;

    private final File file;

    private final boolean canDelete;

    private final boolean canWrite;

    private final boolean canRead;
}
