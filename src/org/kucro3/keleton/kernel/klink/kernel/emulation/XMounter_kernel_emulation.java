package org.kucro3.keleton.kernel.klink.kernel.emulation;

import org.kucro3.keleton.kernel.xmount.XMounterLoadedImpl;
import org.kucro3.keleton.klink.xmount.LoadedMounter;
import org.kucro3.keleton.klink.xmount.Mounter;
import org.kucro3.klink.Klink;
import org.kucro3.klink.expression.ExpressionLibrary;

public class XMounter_kernel_emulation implements Mounter {
    @Override
    public void mount(Klink klink)
    {
        ExpressionLibrary lib = klink.getExpressions();

        lib.putExpression(File.instance());
    }

    @Override
    public void unmount(Klink klink)
    {
        ExpressionLibrary lib = klink.getExpressions();

        lib.removeExpression(File.instance().getName());
    }

    public static LoadedMounter instance()
    {
        return INSTANCE;
    }

    private static final LoadedMounter INSTANCE = new XMounterLoadedImpl("kernel-emulation", new XMounter_kernel_emulation());
}
