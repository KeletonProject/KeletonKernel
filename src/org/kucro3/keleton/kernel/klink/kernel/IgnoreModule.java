package org.kucro3.keleton.kernel.klink.kernel;

import com.google.common.eventbus.Subscribe;
import org.kucro3.keleton.kernel.KeletonKernel;
import org.kucro3.keleton.module.event.KeletonLoaderEvent;
import org.kucro3.klink.Ref;
import org.kucro3.klink.expression.Expression;
import org.kucro3.klink.expression.ExpressionCompiler;
import org.kucro3.klink.expression.ExpressionInstance;
import org.kucro3.klink.expression.ExpressionLibrary;
import org.kucro3.klink.syntax.Sequence;
import org.spongepowered.api.event.cause.Cause;

public class IgnoreModule implements ExpressionCompiler.Level1 {
    @Override
    public ExpressionInstance compile(ExpressionLibrary expressionLibrary, Ref[] refs, Sequence sequence)
    {
        final String name = sequence.next();

        return (sys, env) -> KeletonKernel.getEventBus().register(new Object() {
            @Subscribe
            public void onDiscover(KeletonLoaderEvent.Pre event)
            {
                if(event.getInfo().id().equals(name))
                    event.cancel(Cause.source(KeletonKernel.getKlink()).build());
            }
        });
    }

    public static Expression instance()
    {
        return INSTANCE;
    }

    private static final Expression INSTANCE = new Expression("kernel:IgnoreModule", new IgnoreModule());
}
