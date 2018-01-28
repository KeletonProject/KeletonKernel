package org.kucro3.keleton.kernel.url.inmemory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class InMemoryURLConnection extends URLConnection {
    protected InMemoryURLConnection(InMemoryResources resources, URL url)
    {
        super(url);
        this.resources = resources;
    }

    @Override
    public void connect() throws IOException
    {
        connected = true;
    }

    @Override
    public void setDoInput(boolean b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getDoInput()
    {
        return true;
    }

    @Override
    public void setDoOutput(boolean b)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getDoOutput()
    {
        return false;
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        return resources.getResourceAsStream(url.getPath())
                .orElseThrow(() -> new IOException("No such in-memory resource: " + url.getPath()));
    }

    private final InMemoryResources resources;
}
