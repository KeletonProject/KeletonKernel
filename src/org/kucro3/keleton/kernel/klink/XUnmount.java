package org.kucro3.keleton.kernel.klink;

import org.kucro3.klink.expression.Expression;

public class XUnmount {
    private XUnmount()
    {
    }

    public static Expression instance()
    {
        return new Expression("xunmount", new XMount(true));
    }
}
