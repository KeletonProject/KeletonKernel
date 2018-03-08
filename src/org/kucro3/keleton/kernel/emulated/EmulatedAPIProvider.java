package org.kucro3.keleton.kernel.emulated;

import org.kucro3.keleton.api.APIProvider;
import org.kucro3.keleton.api.ExportAPI;
import org.kucro3.keleton.emulated.Emulated;
import org.kucro3.keleton.security.ModuleAccessControl;
import org.kucro3.keleton.security.Sealed;

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

    @Sealed
    public static void initialize(Emulated emulated)
    {
        if(EmulatedAPIProvider.emulated != null)
            throw new IllegalStateException("Already initialized");
        EmulatedAPIProvider.emulated = Objects.requireNonNull(emulated);
    }

    @Sealed
    public static Emulated getEmulated()
    {
        return emulated;
    }

    @Sealed
    private static Emulated emulated;
}
