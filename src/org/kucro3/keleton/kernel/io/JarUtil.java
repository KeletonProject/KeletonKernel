package org.kucro3.keleton.kernel.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class JarUtil {
    private JarUtil()
    {

    }

    public static byte[] readClassFully(JarFile file, JarEntry entry) throws IOException
    {
        InputStream is = file.getInputStream(entry);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // check magic value
        int ch;
        for(int i = 0; i < 4; i++)
            if((ch = is.read()) != MAGICVALUE[i])
                return null;
            else
                bos.write(ch);

        while((ch = is.read()) != -1)
            bos.write(ch);

        return bos.toByteArray();
    }

    public static byte[] readFully(JarFile file, JarEntry entry) throws IOException
    {
        return StreamUtil.readFully(file.getInputStream(entry));
    }

    private static final int[] MAGICVALUE = {0xCA, 0xFE, 0xBA, 0xBE};
}
