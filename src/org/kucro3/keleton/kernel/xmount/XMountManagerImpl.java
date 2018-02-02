package org.kucro3.keleton.kernel.xmount;

import org.kucro3.keleton.klink.xmount.LoadedMounter;
import org.kucro3.keleton.klink.xmount.Mounter;
import org.kucro3.keleton.klink.xmount.XMountManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class XMountManagerImpl implements XMountManager {
    public XMountManagerImpl(BiFunction<String, Mounter, LoadedMounter> loadedMounterProvider)
    {
        this.loadedMounterProvider = loadedMounterProvider;
    }

    @Override
    public boolean hasMoutable(String s)
    {
        LoadedMounter loaded = map.get(s);

        if(loaded == null)
            return false;

        return !loaded.isMounted();
    }

    @Override
    public Optional<LoadedMounter> getMountable(String s)
    {
        LoadedMounter loaded = map.get(s);

        if(loaded == null)
            return Optional.empty();

        if(loaded.isMounted())
            return Optional.empty();

        return Optional.of(loaded);
    }

    @Override
    public boolean putMountable(String s, Mounter mounter)
    {
        if(loadedMounterProvider == null)
            throw new UnsupportedOperationException();

        LoadedMounter loaded = loadedMounterProvider.apply(s, mounter);
        return map.putIfAbsent(loaded.getName(), loaded) == null;
    }

    public boolean putMountable(LoadedMounter loaded)
    {
        return map.put(loaded.getName(), loaded) == null;
    }

    @Override
    public boolean isMounted(String s)
    {
        LoadedMounter loaded = map.get(s);

        if(loaded == null)
            return false;

        return loaded.isMounted();
    }

    @Override
    public Optional<LoadedMounter> getMounted(String s)
    {
        LoadedMounter loaded = map.get(s);

        if(loaded == null)
            return Optional.empty();

        if(!loaded.isMounted())
            return Optional.empty();

        return Optional.of(loaded);
    }

    private final Map<String, LoadedMounter> map = new HashMap<>();

    private final BiFunction<String, Mounter, LoadedMounter> loadedMounterProvider;
}
