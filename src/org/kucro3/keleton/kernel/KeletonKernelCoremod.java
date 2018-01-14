package org.kucro3.keleton.kernel;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.10.2")
public class KeletonKernelCoremod implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] {
                "org.kucro3.keleton.kernel.api.KeletonAPIClassTransformer",
                "org.kucro3.keleton.kernel.module.KeletonInstanceClassTransformer"
        };
    }

    @Override
    public String getModContainerClass()
    {
        return "org.kucro3.keleton.kernel.KeletonKernel";
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> map)
    {

    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
