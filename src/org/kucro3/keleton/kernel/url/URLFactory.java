package org.kucro3.keleton.kernel.url;

import org.kucro3.keleton.exception.KeletonInternalException;
import org.kucro3.keleton.kernel.url.inmemory.InMemoryResources;
import org.kucro3.keleton.kernel.url.inmemory.InMemoryURLStreamHandler;

import java.io.IOException;
import java.net.URL;

public class URLFactory {
    private URLFactory()
    {
    }

    public static URL inMemoryURL(InMemoryResources resources, String host)
    {
        return inMemoryURL(resources, host, "");
    }

    public static URL inMemoryURL(InMemoryResources resources, String host, String file)
    {
        try {
            return new URL(InMemoryURLStreamHandler.PROTOCOL, host, 0, file, new InMemoryURLStreamHandler(resources));
        } catch (IOException e) {
            throw new KeletonInternalException(e);
        }
    }
}
