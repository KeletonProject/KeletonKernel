package org.kucro3.keleton.kernel.klink.kernel.emulation;

import org.kucro3.keleton.emulated.Emulated;
import org.kucro3.keleton.emulated.EmulatedHandle;
import org.kucro3.keleton.kernel.emulated.EmulatedAPIProvider;
import org.kucro3.keleton.kernel.emulated.impl.MutableEmulated;
import org.kucro3.klink.Executable;
import org.kucro3.klink.Ref;
import org.kucro3.klink.Snapshot;
import org.kucro3.klink.Util;
import org.kucro3.klink.exception.ScriptException;
import org.kucro3.klink.expression.Expression;
import org.kucro3.klink.expression.ExpressionCompiler;
import org.kucro3.klink.expression.ExpressionInstance;
import org.kucro3.klink.expression.ExpressionLibrary;
import org.kucro3.klink.flow.Flow;
import org.kucro3.klink.syntax.Sequence;

public class SetRoot implements ExpressionCompiler.Level3 {
    public SetRoot(boolean root)
    {
        this.root = root;
    }

    @Override
    public ExpressionInstance compile(ExpressionLibrary expressionLibrary, Ref[] refs, Sequence sequence, Flow flow, Snapshot snapshot)
    {
        final Executable executable = Util.requireOperation(sequence, refs, flow, snapshot);
        return (sys, env) -> {
            Emulated emulated = EmulatedAPIProvider.getEmulated();

            if(!(emulated instanceof MutableEmulated))
                throw NotEmulatedFromKlink();

            executable.execute(sys, env);
            Object returned = env.popReturnSlot();

            if(!(returned instanceof EmulatedHandle))
                throw EmulatedHandleRequired();

            EmulatedHandle handle = (EmulatedHandle) returned;

            if(!handle.exists())
                if(!handle.makeDirectory())
                    throw FailedToCreateDirectory();
                else;
            else if(!handle.isDirectory())
                throw NotADirectory();

            MutableEmulated mutable = (MutableEmulated) emulated;

            if(root)
                mutable.setRoot(handle);
            else
                mutable.setModules(handle);
        };
    }

    public static Expression instance()
    {
        return INSTANCE;
    }

    public static ScriptException NotEmulatedFromKlink()
    {
        return new ScriptException("Illegal emlation mode");
    }

    public static ScriptException FailedToCreateDirectory()
    {
        throw new ScriptException("Failed to create directory");
    }

    public static ScriptException NotADirectory()
    {
        throw new ScriptException("Not a directory");
    }

    public static ScriptException EmulatedHandleRequired()
    {
        return new ScriptException("Emulated handle required");
    }

    private final boolean root;

    private static final Expression INSTANCE = new Expression("kernel:emulation::SetRoot", new SetRoot(true));
}
