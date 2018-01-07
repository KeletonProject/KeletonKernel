package org.kucro3.keleton.kernel.api;

import org.kucro3.keleton.api.APIProvider;
import org.kucro3.keleton.api.ExportAPI;
import org.kucro3.keleton.api.exception.APIExportingException;
import org.kucro3.keleton.api.exception.APIExportingMetadataNotFoundException;
import org.kucro3.keleton.api.exception.APIExportingNamespaceDuplicatedException;
import org.kucro3.keleton.api.exception.APIExportingUnsatisfiedMethodException;
import org.kucro3.keleton.kernel.KeletonKernel;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class KeletonAPIManagerImpl {
    public KeletonAPIManagerImpl()
    {
        this.namespaces = new HashMap<>();
    }

    public Map<String, KeletonAPINamespaceImpl> getNamespaces()
    {
        return namespaces;
    }

    public synchronized void export(Class<?> clazz) throws APIExportingException
    {
        APIProvider providerInfo;
        if((providerInfo = clazz.getAnnotation(APIProvider.class)) == null)
            throw new APIExportingMetadataNotFoundException("Annotation not found");

        String namespace = providerInfo.namespace();

        KeletonAPINamespaceImpl nsp;
        if(namespaces.get(namespace) != null)
            throw new APIExportingNamespaceDuplicatedException(namespace);

        namespaces.put(namespace, nsp = new KeletonAPINamespaceImpl(namespace));

        Method[] methods = clazz.getMethods();
        for(Method method : methods)
        {
            ExportAPI info = method.getAnnotation(ExportAPI.class);

            if(info == null)
                continue;

            int modifier = method.getModifiers();

            if(!Modifier.isStatic(modifier) || Modifier.isAbstract(modifier))
                throw new APIExportingUnsatisfiedMethodException("Unsatisfied modifier");

            KeletonAPIMethodHandleImpl mh = new KeletonAPIMethodHandleImpl(nsp, info.name(), method);

            nsp.exported.put(info.name(), mh);
        }
    }

    private final Map<String, KeletonAPINamespaceImpl> namespaces;
}
