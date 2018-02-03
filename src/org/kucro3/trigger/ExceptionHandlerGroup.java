package org.kucro3.trigger;

import com.google.common.eventbus.Subscribe;

import java.util.function.Predicate;

public class ExceptionHandlerGroup {
    public ExceptionHandlerGroup()
    {
    }

    public ExceptionHandlerGroup(ExceptionHandler globalHandler)
    {

    }

    private static interface HandlerInGroup
    {
        boolean handle(Object trigger, Exception exception);
    }

    private class NormalExceptionHandlerInGroup implements HandlerInGroup
    {
        NormalExceptionHandlerInGroup(
                Predicate<Object> triggerPredicate,
                ExceptionHandler handler)
        {
            this.triggerPredicate = triggerPredicate;
            this.handler = handler;
        }

        @Override
        public boolean handle(Object trigger, Exception exception)
        {
            if(!triggerPredicate.test(trigger))
                return false;

            handler.handle(trigger, exception);
            return true;
        }

        private final Predicate<Object> triggerPredicate;

        private final ExceptionHandler handler;
    }

    @SuppressWarnings("unchecked")
    private class SpecializedExceptionHandlerInGroup implements HandlerInGroup
    {
        <X extends Exception> SpecializedExceptionHandlerInGroup(
                Predicate<Object> triggerPredicate,
                Predicate<X> exceptionPredicate,
                SpecializedExceptionHandler<X> handler)
        {
            this.triggerPredicate = triggerPredicate;
            this.exceptionPredicate = (Predicate) exceptionPredicate;
            this.handler = (SpecializedExceptionHandler) handler;
        }

        @Override
        public boolean handle(Object trigger, Exception exception)
        {
            if(!triggerPredicate.test(trigger))
                return false;

            if(!exceptionPredicate.test(exception))
                return false;

            handler.handle(trigger, exception);
            return true;
        }

        private final Predicate<Object> triggerPredicate;

        private final Predicate<Exception> exceptionPredicate;

        private final SpecializedExceptionHandler<Exception> handler;
    }

    private class GlobalExceptionHandleriInGroup implements HandlerInGroup
    {
        GlobalExceptionHandleriInGroup(ExceptionHandler handler)
        {
            this.handler = handler;
        }

        @Override
        public boolean handle(Object trigger, Exception exception)
        {
            handler.handle(trigger, exception);
            return true;
        }

        private final ExceptionHandler handler;
    }
}
