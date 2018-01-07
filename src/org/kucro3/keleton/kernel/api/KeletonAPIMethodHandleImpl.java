package org.kucro3.keleton.kernel.api;

import org.kucro3.keleton.api.APIMethodHandle;
import org.kucro3.keleton.api.APIMethodNamespace;
import org.kucro3.keleton.api.exception.APICallingException;

import java.lang.reflect.Method;

public class KeletonAPIMethodHandleImpl implements APIMethodHandle {
    KeletonAPIMethodHandleImpl(KeletonAPINamespaceImpl namespace, String name, Method method)
    {
        this.namespace = namespace;
        this.name = name;
        this.method = method;
    }

    @Override
    public Object call(Object... objects)
    {
        try {
            return method.invoke(null, objects);
        } catch (Exception e) {
            throw new APICallingException(e);
        }
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public APIMethodNamespace getNamespace()
    {
        return namespace;
    }

    private final KeletonAPINamespaceImpl namespace;

    private final String name;

    public final Method method;
}
