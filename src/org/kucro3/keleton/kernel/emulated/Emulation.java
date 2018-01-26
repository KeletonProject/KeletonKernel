package org.kucro3.keleton.kernel.emulated;

public class Emulation {
    private Emulation()
    {
    }

    public static Emulated getEmulated()
    {
        return emulated;
    }

    private static Emulated emulated;
}
