package org.kucro3.keleton.kernel;

import org.kucro3.keleton.api.APIProvider;
import org.kucro3.keleton.api.ExportAPI;
import org.kucro3.keleton.module.KeletonModule;
import org.kucro3.keleton.module.KeletonModuleManager;
import org.kucro3.klink.Environment;
import org.kucro3.klink.Klink;

@APIProvider(namespace = "kernel")
public class KernelAPIProvider {
    @ExportAPI(name = "GetMajorVersion")
    public static int GetMajorVersion()
    {
        return 1;
    }

    @ExportAPI(name = "GetMinorVersion")
    public static int GetMinorVersion()
    {
        return 0;
    }

    @ExportAPI(name = "GetModuleManager")
    public static KeletonModuleManager GetModuleManager()
    {
        return KeletonKernel.getModuleManagerImpl();
    }

    @ExportAPI(name = "GetKernelKlinkInstance")
    public static Klink GetKernelKlinkInstance()
    {
        return KeletonKernel.getKlink();
    }

    @ExportAPI(name = "GetKernelKlinkEnvironment")
    public static Environment GetKernelKlinkEnvironment()
    {
        return KeletonKernel.getRuntimeEnv();
    }
}
