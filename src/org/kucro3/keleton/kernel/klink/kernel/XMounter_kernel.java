package org.kucro3.keleton.kernel.klink.kernel;

import org.kucro3.keleton.klink.xmount.Mounter;
import org.kucro3.klink.Klink;
import org.kucro3.klink.expression.ExpressionLibrary;

public class XMounter_kernel implements Mounter {
    @Override
    public void mount(Klink klink)
    {
        ExpressionLibrary lib = klink.getExpressions();

        lib.putExpression(IgnoreModule.instance());
    }

    @Override
    public void unmount(Klink klink)
    {
        ExpressionLibrary lib = klink.getExpressions();

        lib.removeExpression(IgnoreModule.instance().getName());
    }
}
