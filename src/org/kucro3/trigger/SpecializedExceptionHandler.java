package org.kucro3.trigger;

public interface SpecializedExceptionHandler<X extends Exception> {
    public void handle(Object trigger, X exception);
}
