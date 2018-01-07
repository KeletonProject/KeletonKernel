package org.kucro3.keleton.kernel;

import org.kucro3.keleton.api.APIMethodNamespace;
import org.kucro3.keleton.api.APIProvider;
import org.kucro3.keleton.api.ExportAPI;
import org.kucro3.keleton.api.exception.APIExportingException;

import java.util.Map;

@APIProvider(namespace = "kernel-api")
public class KernelAPIManagerProvider {
    @ExportAPI(name = "GetNamespaces")
    public static Map<String, APIMethodNamespace> GetNamespaces()
    {
        return (Map) KeletonKernel.getAPIManagerImpl().getNamespaces();
    }

    @ExportAPI(name = "ExportAPI")
    public static void ExportAPI(Class<?> apiProvider) throws APIExportingException
    {
        KeletonKernel.getAPIManagerImpl().export(apiProvider);
    }
}
