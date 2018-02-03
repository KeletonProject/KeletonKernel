package org.kucro3.keleton.kernel.emulated.impl;

import org.kucro3.keleton.emulated.EmulatedHandle;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
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
    public String toString()
    {
        return getPath();
    }

    @Override
    public EmulatedHandle[] listHandles()
    {
        return listHandles((unused) -> true);
    }

    @Override
    public EmulatedHandle[] listHandles(EmulatedHandle.HandleFilter filter)
    {
        if(!isDirectory())
            return new EmulatedHandle[0];

        File[] files = file.listFiles();
        ArrayList<EmulatedHandle> handles = new ArrayList<>();

        EmulatedHandle handle;
        for(File file : files)
        {
            handle = new FileEmulatedHandle(file, root, true, true, true);
            if(filter.filter(handle))
                handles.add(handle);
        }

        return handles.toArray(new EmulatedHandle[handles.size()]);
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
    public URL toURL() throws IOException
    {
        return file.toURI().toURL();
    }

    @Override
    public Optional<EmulatedHandle> subHandle(String path)
    {
        if(!isDirectory())
            return Optional.empty();
        File file = new File(root, path);
        return Optional.of(new FileEmulatedHandle(file, root, true, true, true));
    }

    @Override
    public Optional<EmulatedHandle> getParentHandle()
    {
        File parent = file.getParentFile();
        if(parent.equals(root))
            return Optional.empty();
        return Optional.of(new FileEmulatedHandle(parent, root, true, true, true));
    }

    @Override
    public boolean canDelete()
    {
        return canDelete;
    }

    @Override
    public boolean canRead()
    {
        if(isDirectory())
            return false;
        return canRead;
    }

    @Override
    public boolean canWrite()
    {
        if(isDirectory())
            return false;
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
    public boolean create() throws IOException
    {
        return file.createNewFile();
    }

    protected final File root;

    protected final File file;

    private final boolean canDelete;

    private final boolean canWrite;

    private final boolean canRead;
}
