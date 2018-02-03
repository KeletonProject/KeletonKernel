package org.kucro3.keleton.kernel.klink.kernel.emulation;

import org.kucro3.klink.expression.Expression;

public class SetModules {
    private SetModules()
    {
    }

    public static Expression instance()
    {
        return INSTANCE;
    }

    private static final Expression INSTANCE = new Expression("kernel:emualtion::SetModules", new SetRoot(false));
}
