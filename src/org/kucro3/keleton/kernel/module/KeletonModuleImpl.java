package org.kucro3.keleton.kernel.module;

import org.kucro3.keleton.emulated.EmulatedHandle;
import org.kucro3.keleton.module.KeletonInstance;
import org.kucro3.keleton.module.KeletonModule;
import org.kucro3.keleton.module.Module;
import org.kucro3.keleton.module.exception.KeletonModuleException;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
        return false;
    }

    @Override
    public boolean enable()
    {
        return false;
    }

    @Override
    public boolean disable()
    {
        return false;
    }

    @Override
    public boolean destroy()
    {
        return false;
    }

    void load0()
    {

    }

    void enable0()
    {

    }

    void disable0()
    {

    }

    void destroy0()
    {

    }

    @Override
    public void escapeState(State state)
    {

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

    DisablingCallback callback;

    ModuleSequence seq;

    volatile State fencedState;

    private volatile State state;

    private final KeletonInstance instance;

    private final Module info;

    private final EmulatedHandle source;

    private final URL url;

    static interface DisablingCallback
    {
        void onDisable(KeletonModuleImpl module) throws KeletonModuleException;
    }
}
