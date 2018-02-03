package org.kucro3.keleton.kernel.klink.kernel;

import org.kucro3.keleton.kernel.xmount.XMounterLoadedImpl;
import org.kucro3.keleton.klink.xmount.LoadedMounter;
import org.kucro3.keleton.klink.xmount.Mounter;
import org.kucro3.klink.Klink;
import org.kucro3.klink.expression.ExpressionLibrary;

public class XMounter_kernel implements Mounter {
    @Override
    public void mount(Klink klink)
    {
        ExpressionLibrary lib = klink.getExpressions();

        lib.putExpression(IgnoreModule.instance());
        lib.putExpression(ScanModules.instance());
    }

    @Override
    public void unmount(Klink klink)
    {
        ExpressionLibrary lib = klink.getExpressions();

        lib.removeExpression(IgnoreModule.instance().getName());
        lib.removeExpression(ScanModules.instance().getName());
    }

    public static LoadedMounter instance()
    {
        return INSTANCE;
    }

    private static final LoadedMounter INSTANCE = new XMounterLoadedImpl("kernel", new XMounter_kernel());
}
