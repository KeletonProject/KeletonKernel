package org.kucro3.keleton.kernel.xmount.trigger;

import org.kucro3.trigger.Gradation;

import java.util.Optional;

public enum MountableTriggerGradation implements Gradation {
    VERIFYING,
    PRELOADING,
    REGISTERING;

    @Override
    public int getOrdinal()
    {
        return this.ordinal();
    }

    @Override
    public String getName()
    {
        return this.name();
    }

    @Override
    public Optional<String> getTitle()
    {
        return Optional.of(TITLE);
    }

    private static final String TITLE = "xmount.trigger";
}
