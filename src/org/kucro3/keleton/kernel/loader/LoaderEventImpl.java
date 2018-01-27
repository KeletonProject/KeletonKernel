package org.kucro3.keleton.kernel.loader;

import org.kucro3.keleton.module.KeletonModule;
import org.kucro3.keleton.module.Module;
import org.kucro3.keleton.module.event.KeletonLoaderEvent;
import org.spongepowered.api.event.cause.Cause;

import java.util.Optional;

abstract class LoaderEventImpl implements KeletonLoaderEvent {
    LoaderEventImpl(Cause cause, Module info)
    {
        this.cause = cause;
        this.info = info;
    }

    @Override
    public Cause getCause()
    {
        return cause;
    }

    @Override
    public Module getInfo()
    {
        return info;
    }

    private final Module info;

    private final Cause cause;

    static class Pre extends LoaderEventImpl implements KeletonLoaderEvent.Pre
    {
        Pre(Cause cause, Module info)
        {
            super(cause, info);
        }

        @Override
        public Optional<Cause> getCancellationCause()
        {
            return Optional.ofNullable(cause);
        }

        @Override
        public void cancel(Cause cause)
        {
            setCancelled(true);
            this.cause = cause;
        }

        @Override
        public boolean isCancelled()
        {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancel)
        {
            this.cancelled = cancel;
            if(!cancel)
                this.cause = null;
        }

        private boolean cancelled;

        private Cause cause;
    }

    static class Cancelled extends LoaderEventImpl implements KeletonLoaderEvent.Cancelled
    {
        Cancelled(Cause cause, Module info)
        {
            super(cause, info);
        }
    }

    static class Discovered extends LoaderEventImpl implements KeletonLoaderEvent.Discovered
    {
        Discovered(Cause cause, KeletonModule module, Module info)
        {
            super(cause, info);
            this.module = module;
        }

        @Override
        public KeletonModule getModule()
        {
            return module;
        }

        private final KeletonModule module;
    }

    static class Failed extends LoaderEventImpl implements KeletonLoaderEvent.Failed
    {
        Failed(Cause cause, Module info, Exception exception)
        {
            super(cause, info);
            this.exception = exception;
        }

        @Override
        public Optional<Exception> getException()
        {
            return Optional.ofNullable(exception);
        }

        private final Exception exception;
    }

    static class Ignored extends LoaderEventImpl implements KeletonLoaderEvent.Ignored
    {
        Ignored(Cause cause, Module info, String message)
        {
            super(cause, info);
            this.message = message;
        }

        @Override
        public String getMessage()
        {
            return message;
        }

        private final String message;
    }
}
