package org.kucro3.keleton.kernel.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class StreamUtil {
    private StreamUtil()
    {
    }

    public static byte[] readFully(InputStream is) throws IOException
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int ch;
        while((ch = is.read()) != -1)
            buffer.write(ch);

        return buffer.toByteArray();
    }

    public static void pour(InputStream is, OutputStream os) throws IOException
    {
        byte[] buffer = new byte[1024];
        while(is.read(buffer) > 0)
            os.write(buffer);
    }
}
