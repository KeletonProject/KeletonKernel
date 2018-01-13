package org.kucro3.keleton.kernel.module;

import java.net.*;

public class URLUtil {
    public static URL createURL(String mid, URLStreamHandler handler) throws MalformedURLException
    {
        return new URL(null, "kmodule://" + mid + "/", handler);
    }
}
