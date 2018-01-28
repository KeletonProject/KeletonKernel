package org.kucro3.keleton.kernel.url.inmemory;

import javax.annotation.concurrent.ThreadSafe;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
public class InMemoryResources {
    public InMemoryResources()
    {
    }

    public Optional<byte[]> getResource(String name)
    {
        return Optional.ofNullable(resources.get(name));
    }

    public boolean hasResource(String name)
    {
        return resources.containsKey(name);
    }

    public void setResource(String name, byte[] byts)
    {
        resources.put(name, byts);
    }

    public boolean setResourceIfAbsent(String name, byte[] byts)
    {
        return resources.putIfAbsent(name, byts) == null;
    }

    public Optional<InputStream> getResourceAsStream(String name)
    {
        byte[] byts = resources.get(name);

        if(byts == null)
            return Optional.empty();

        return Optional.of(new ByteArrayInputStream(byts));
    }

    public Map<String, byte[]> getResources()
    {
        return resources;
    }

    private final Map<String, byte[]> resources = new ConcurrentHashMap<>();
}
