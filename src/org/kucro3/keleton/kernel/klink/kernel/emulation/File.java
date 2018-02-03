package org.kucro3.keleton.kernel.klink.kernel.emulation;

import org.kucro3.keleton.kernel.emulated.impl.ImmutableFileEmulatedHandle;
import org.kucro3.klink.Ref;
import org.kucro3.klink.expression.Expression;
import org.kucro3.klink.expression.ExpressionCompiler;
import org.kucro3.klink.expression.ExpressionInstance;
import org.kucro3.klink.expression.ExpressionLibrary;
import org.kucro3.klink.syntax.Sequence;

public class File implements ExpressionCompiler.Level1 {
    @Override
    public ExpressionInstance compile(ExpressionLibrary expressionLibrary, Ref[] refs, Sequence sequence)
    {
        final String path = sequence.leftToString();
        return (sys, env) -> env.setReturnSlot(new ImmutableFileEmulatedHandle(new java.io.File(path)));
    }

    public static Expression instance()
    {
        return INSTANCE;
    }

    private static final Expression INSTANCE = new Expression("kernel:emulation::File", new File());
}
