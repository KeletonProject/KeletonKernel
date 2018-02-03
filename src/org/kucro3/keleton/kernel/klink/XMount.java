package org.kucro3.keleton.kernel.klink;

import org.kucro3.keleton.kernel.xmount.XMountAPIProvider;
import org.kucro3.keleton.klink.xmount.LoadedMounter;
import org.kucro3.keleton.klink.xmount.XMountManager;
import org.kucro3.klink.Ref;
import org.kucro3.klink.exception.ScriptException;
import org.kucro3.klink.expression.Expression;
import org.kucro3.klink.expression.ExpressionCompiler;
import org.kucro3.klink.expression.ExpressionInstance;
import org.kucro3.klink.expression.ExpressionLibrary;
import org.kucro3.klink.syntax.Sequence;
import org.kucro3.klink.syntax.misc.Vector;

public class XMount implements ExpressionCompiler.Level1 {
    public XMount(boolean unmount)
    {
        this.unmount = unmount;
    }

    @Override
    public ExpressionInstance compile(ExpressionLibrary expressionLibrary, Ref[] refs, Sequence sequence)
    {
        final Vector vector = VECTOR.clone().parse(sequence);

        if(!vector.getResult().passed())
            throw new ScriptException("Syntax error: " + vector.getResult().getMessage());

        final String name = vector.getLastParsed()[0];

        return (sys, env) -> {
            XMountManager manager = XMountAPIProvider.getManager();
            LoadedMounter mounter = unmount ?
                    manager.getMounted(name)
                            .orElseThrow(() -> new ScriptException("No such mounted object: \"" + name + "\""))
                    : manager.getMountable(name)
                    .orElseThrow(() -> new ScriptException("No such mountable object: \"" + name + "\" (Not found or already mounted)"));

            if (unmount)
                mounter.unmount(expressionLibrary);
            else
                mounter.mount(expressionLibrary);
        };
    }

    public static Expression instance()
    {
        return new Expression("xmount", new XMount(false));
    }

    private final boolean unmount;

    private static final Vector VECTOR = new Vector("<", ">", ",", 1, 1);
}
