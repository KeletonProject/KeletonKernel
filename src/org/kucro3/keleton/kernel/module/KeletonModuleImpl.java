package org.kucro3.keleton.kernel.module;

import org.kucro3.keleton.emulated.EmulatedHandle;
import org.kucro3.keleton.module.KeletonInstance;
import org.kucro3.keleton.module.KeletonModule;
import org.kucro3.keleton.module.Module;
import org.kucro3.keleton.module.exception.KeletonModuleException;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class KeletonModuleImpl implements KeletonModule {
    public KeletonModuleImpl(EmulatedHandle source, URL url, KeletonInstance instance, Module info)
    {
        this.source = source;
        this.instance = instance;
        this.info = info;
        this.state = State.MOUNTED;
        this.url = url;
    }

    boolean touchState(State state)
    {
        switch(this.state)
        {
            case FENCED:
                return false;

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

    DisablingCallback callback;

    ModuleSequence seq;

    volatile State fencedState;

    private volatile State state;

    private final KeletonInstance instance;

    private final Module info;

    private final EmulatedHandle source;

    private final URL url;

    private final Object lock = new Object();

    static interface DisablingCallback
    {
        void onDisable(KeletonModuleImpl module) throws KeletonModuleException;
    }

    static interface ActionFunction
    {
        CompletableFuture<Void> get() throws Exception;
    }
}
