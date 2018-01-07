package org.kucro3.keleton.kernel;

import org.kucro3.keleton.api.APIProvider;
import org.kucro3.keleton.api.ExportAPI;
import org.kucro3.keleton.module.KeletonModule;
import org.kucro3.keleton.module.KeletonModuleManager;

@APIProvider(namespace = "kernel")
public class KernelAPIProvider {
    @ExportAPI(name = "GetMajorVersion")
    public int GetMajorVersion()
    {
        return 1;
    }

    @ExportAPI(name = "GetMinorVersion")
    public int GetMinorVersion()
    {
        return 0;
    }

    @ExportAPI(name = "GetModuleManager")
    public KeletonModuleManager GetModuleManager()
    {
        return KeletonKernel.getModuleManagerImpl();
    }

    @ExportAPI(name = "GetKernelFenceEstablisher")
    public KeletonModule.FenceEstablisher GetKernelFenceEstablisher()
    {
        return KERNEL_FENCE_ESTABLISHER;
    }

    private static final KeletonModule.FenceEstablisher KERNEL_FENCE_ESTABLISHER = () -> "keletonkernel";
}
