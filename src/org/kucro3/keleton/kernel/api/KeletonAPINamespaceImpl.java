package org.kucro3.keleton.kernel.api;

import org.kucro3.keleton.api.APIMethodHandle;
import org.kucro3.keleton.api.APIMethodNamespace;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KeletonAPINamespaceImpl implements APIMethodNamespace {
    KeletonAPINamespaceImpl(String name)
    {
        this.exported = new HashMap<>();
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Map<String, APIMethodHandle> getExported()
    {
        return Collections.unmodifiableMap(exported);
    }

    private final String name;

    final Map<String, APIMethodHandle> exported;
}
