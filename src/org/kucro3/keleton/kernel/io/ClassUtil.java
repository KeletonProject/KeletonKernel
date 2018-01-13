package org.kucro3.keleton.kernel.io;

public final class ClassUtil {
    private ClassUtil()
    {
    }

    public static boolean checkMagicValue(byte[] byts)
    {
        if(byts.length < 4)
            return false;
        return byts[0] == 0xCA
                && byts[1] == 0xFE
                && byts[2] == 0xBA
                && byts[3] == 0xBE;
    }

    public static final int MAGICVALUE = 0xCAFEBABE;
}
