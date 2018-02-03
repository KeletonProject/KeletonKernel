package org.kucro3.keleton.kernel.klink.kernel;

import org.kucro3.keleton.exception.KeletonException;
import org.kucro3.keleton.kernel.KeletonBootstraper;
import org.kucro3.klink.exception.ScriptException;
import org.kucro3.klink.expression.Expression;
import org.kucro3.klink.expression.ExpressionCompiler;
import org.kucro3.klink.expression.ExpressionInstance;
import org.kucro3.klink.expression.ExpressionLibrary;
import org.kucro3.klink.syntax.Sequence;

public class ScanModules implements ExpressionCompiler.Level0 {
    @Override
    public ExpressionInstance compile(ExpressionLibrary expressionLibrary, Sequence sequence)
    {
        return (sys, env) -> {
            KeletonBootstraper bootstraper = KeletonBootstraper.getBootstraper();

            if(bootstraper == null)
                throw BootstraperNotInitialized();

            try {
                if(!bootstraper.discoverModules())
                    throw FailedToScanModules();
            } catch (KeletonException e) {
                throw FailedToScanModules(e);
            }
        };
    }

    public static ScriptException BootstraperNotInitialized()
    {
        throw new ScriptException("Not ready to boot");
    }

    public static ScriptException FailedToScanModules()
    {
        throw new ScriptException("Failed to scan modules");
    }

    public static ScriptException FailedToScanModules(KeletonException e)
    {
        throw new ScriptException("Failed to scan modules", e);
    }

    public static Expression instance()
    {
        return INSTANCE;
    }

    private static final Expression INSTANCE = new Expression("kernel::ScanModules", new ScanModules());
}
