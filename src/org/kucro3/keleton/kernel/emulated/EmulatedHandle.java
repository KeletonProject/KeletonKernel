package org.kucro3.keleton.kernel.emulated;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

public interface EmulatedHandle {
    public boolean makeDirectory();

    public boolean isDirectory();

    public boolean exists();

    public String getName();

    public String getPath();

    public Optional<String> getParent();

    public Optional<EmulatedHandle> getParentHandle();

    public boolean canDelete();

    public boolean canRead();

    public boolean canWrite();

    public boolean delete();

    public Optional<InputStream> openInput() throws IOException;

    public Optional<OutputStream> openOutput() throws IOException;

    public void create() throws IOException;

    public boolean rename(String name);
}
