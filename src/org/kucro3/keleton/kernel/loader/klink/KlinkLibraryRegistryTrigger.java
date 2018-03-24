package org.kucro3.keleton.kernel.loader.klink;

import com.theredpixelteam.redtea.trigger.TerminalTrigger;
import com.theredpixelteam.redtea.trigger.TriggerContext;
import org.kucro3.klink.Klink;
import org.kucro3.klink.expression.ExpressionLoader;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class KlinkLibraryRegistryTrigger implements TerminalTrigger {
    public KlinkLibraryRegistryTrigger(Klink sys)
    {
        this.sys = new WeakReference<>(Objects.requireNonNull(sys));
    }

    @Override
    public void trigger(TriggerContext context)
    {
        Klink sys = this.sys.get();

        if(sys == null)
            throw new IllegalStateException("The klink script engine is not available");

        ExpressionLoader.load(
                sys,
                sys.getExpressions(),
                context.first(Class.class).get()
        );
    }

    private final WeakReference<Klink> sys;
}
