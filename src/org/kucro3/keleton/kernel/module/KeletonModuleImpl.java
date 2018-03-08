package org.kucro3.keleton.kernel.module;

import org.kucro3.keleton.emulated.EmulatedHandle;
import org.kucro3.keleton.kernel.KeletonKernel;
import org.kucro3.keleton.module.KeletonInstance;
import org.kucro3.keleton.module.KeletonModule;
import org.kucro3.keleton.module.Module;
import org.kucro3.keleton.module.event.KeletonModuleEvent;
import org.kucro3.keleton.module.exception.KeletonModuleException;
import org.kucro3.keleton.module.security.ModuleStateTransformingPermission;
import org.kucro3.keleton.security.ModuleAccessControl;
import org.kucro3.keleton.security.Sealed;
import org.spongepowered.api.event.cause.Cause;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KeletonModuleImpl implements KeletonModule {
    public KeletonModuleImpl(EmulatedHandle source, URL url, KeletonInstance instance, Module info)
    {
        this.source = source;
        this.instance = instance;
        this.info = info;
        this.state = State.MOUNTED;
        this.url = url;
    }

    @Override
    public String getId()
    {
        return this.info.id();
    }

    @Override
    public Set<String> getDependencies()
    {
        return new HashSet<>(Arrays.asList(this.info.dependencies()));
    }

    @Override
    public KeletonInstance getInstance()
    {
        return this.instance;
    }

    @Override
    public boolean supportDisabling()
    {
        return this.info.supportDisabling();
    }

    @Override
    public State getState()
    {
        return this.state;
    }

    @Override
    public EmulatedHandle getSource()
    {
        return this.source;
    }

    @Override
    public URL getResourceURL()
    {
        return this.url;
    }

    @Override
    public boolean load()
    {
        ModuleAccessControl.checkPermission(
                new ModuleStateTransformingPermission(State.LOADED));

        return false;
    }

    @Override
    public boolean enable()
    {
        ModuleAccessControl.checkPermission(
                new ModuleStateTransformingPermission(State.ENABLED));

        return false;
    }

    @Override
    public boolean disable()
    {
        ModuleAccessControl.checkPermission(
                new ModuleStateTransformingPermission(State.DISABLED));

        return false;
    }

    @Override
    public boolean destroy()
    {
        ModuleAccessControl.checkPermission(
                new ModuleStateTransformingPermission(State.DESTROYED));

        return false;
    }

    @Sealed
    void load0()
    {
        ModuleAccessControl.checkPermission(
                new ModuleStateTransformingPermission(State.LOADED));

        transform(State.LOADED, instance::onLoad);
    }

    @Sealed
    void enable0()
    {
        ModuleAccessControl.checkPermission(
                new ModuleStateTransformingPermission(State.ENABLED));

        transform(State.ENABLED, instance::onEnable);
    }

    @Sealed
    void disable0()
    {
        ModuleAccessControl.checkPermission(
                new ModuleStateTransformingPermission(State.DISABLED));

        transform(State.DISABLED, () -> {
            DisablingCallback callback = this.callback;
            if(callback != null)
                callback.onDisable(this);

            instance.onDisable();
        });
    }

    @Sealed
    void destroy0()
    {
        ModuleAccessControl.checkPermission(
                new ModuleStateTransformingPermission(State.DESTROYED));

        transform(State.DESTROYED, instance::onDestroy);
    }

    @Sealed
    private synchronized void transform(State to, ExceptionalAction action)
    {
        KeletonModuleEvent.StateTransformation.Pre preEvent =
                new StateTransformationEventImpl.Pre(this, this.state, to, Cause.source(this).build());

        KeletonKernel.postEvent(preEvent);

        if(preEvent.isCancelled())
        {
            Cause cause = preEvent.isCancelledWithCause()
                    ? preEvent.getCancellationCause().get()
                    : Cause.source(this).build();

            KeletonModuleEvent.StateTransformation.Cancelled cancelledEvent =
                    new StateTransformationEventImpl.Cancelled(this, this.state, to, cause);

            KeletonKernel.postEvent(cancelledEvent);

            return;
        }

        if(!touchState(to)) // ignored
        {
            KeletonModuleEvent.StateTransformation.Ignored ignoredEvent =
                    new StateTransformationEventImpl.Ignored(this, this.state, to, Cause.source(this).build()
                    , "Module \' " + this.getId() + " \' in state \"" + this.state.name() + "\" cannot be transformed to \"" + to.name() + "\"" );

            KeletonKernel.postEvent(ignoredEvent);

            return;
        }

        try {
            action.act();
        } catch (Throwable e) {
            State oldState = this.state;
            this.state = State.FAILED;

            KeletonModuleEvent.StateTransformation.Failed failedEvent =
                    new StateTransformationEventImpl.Failed(this, oldState, to, Cause.source(this).build(), e);

            KeletonKernel.postEvent(failedEvent);

            return;
        }

        State oldState = this.state;
        this.state = to;

        KeletonModuleEvent.StateTransformation.Transformed succeededEvent =
                new StateTransformationEventImpl.Transformed(this, oldState, to, Cause.source(this).build());

        KeletonKernel.postEvent(succeededEvent);
    }

    @Override
    public void escapeState(State state)
    {
        while(!this.state.equals(state));
    }

    boolean touchState(State state)
    {
        switch(this.state)
        {
            case MOUNTED:
            case DESTROYED:
            case FAILED:
                switch(state)
                {
                    case LOADED:
                        return true;

                    default:
                        return false;
                }

            case LOADED:
            case DISABLED:
                switch(state)
                {
                    case ENABLED:
                    case DESTROYED:
                        return true;

                    default:
                        return false;
                }

            case ENABLED:
                switch(state)
                {
                    case DISABLED:
                        return true;

                    default:
                        return false;
                }
        }

        return true;
    }

    @Sealed
    DisablingCallback callback;

    @Sealed
    ModuleSequence seq;

    @Sealed
    private volatile State state;

    @Sealed
    private final KeletonInstance instance;

    @Sealed
    private final Module info;

    @Sealed
    private final EmulatedHandle source;

    @Sealed
    private final URL url;

    static interface ExceptionalAction
    {
        void act() throws Exception;
    }

    static interface DisablingCallback
    {
        void onDisable(KeletonModuleImpl module) throws KeletonModuleException;
    }
}
