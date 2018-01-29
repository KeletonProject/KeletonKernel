package org.kucro3.keleton.kernel.url.inmemory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class InMemoryURLStreamHandler extends URLStreamHandler {
    public InMemoryURLStreamHandler(InMemoryResources resources)
    {
        this.resources = resources;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException
    {
        if(!u.getProtocol().equals(PROTOCOL))
            throw new MalformedURLException("Unknown protocol: " + u.getProtocol());

        return new InMemoryURLConnection(resources, u);
    }

    private final InMemoryResources resources;

    public static final String PROTOCOL = "in-memory";
}
