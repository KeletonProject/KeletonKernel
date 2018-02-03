package org.kucro3.keleton.kernel.klink.kernel.emulation;

import org.kucro3.keleton.kernel.xmount.XMounterLoadedImpl;
import org.kucro3.keleton.klink.xmount.LoadedMounter;
import org.kucro3.keleton.klink.xmount.Mounter;
import org.kucro3.klink.Klink;
import org.kucro3.klink.expression.ExpressionLibrary;

public class XMounter_kernel_emulation implements Mounter {
    @Override
    public void mount(ExpressionLibrary lib)
    {
        lib.putExpression(File.instance());
        lib.putExpression(SetRoot.instance());
        lib.putExpression(SetModules.instance());
    }

    @Override
    public void unmount(ExpressionLibrary lib)
    {
        lib.removeExpression(File.instance().getName());
        lib.removeExpression(SetRoot.instance().getName());
        lib.removeExpression(SetModules.instance().getName());
    }

    public static LoadedMounter instance()
    {
        return INSTANCE;
    }

    private static final LoadedMounter INSTANCE = new XMounterLoadedImpl("kernel-emulation", new XMounter_kernel_emulation());
}
