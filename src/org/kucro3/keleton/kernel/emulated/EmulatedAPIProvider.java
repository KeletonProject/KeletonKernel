package org.kucro3.keleton.kernel.emulated;

import org.kucro3.keleton.api.APIProvider;
import org.kucro3.keleton.api.ExportAPI;
import org.kucro3.keleton.emulated.Emulated;

import java.util.Objects;

@APIProvider(namespace = "kernel-emulated")
public final class EmulatedAPIProvider {
    private EmulatedAPIProvider()
    {
    }

    @ExportAPI(name = "GetEmulated")
    public static Emulated GetEmulated()
    {
        return getEmulated();
    }

    public static void initialize(Emulated emulated)
    {
        if(EmulatedAPIProvider.emulated != null)
            throw new IllegalStateException("Already initialized");
        EmulatedAPIProvider.emulated = Objects.requireNonNull(emulated);
    }

    public static Emulated getEmulated()
    {
        return emulated;
    }

    private static Emulated emulated;
}
