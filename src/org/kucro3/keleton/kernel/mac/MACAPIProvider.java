package org.kucro3.keleton.kernel.mac;

import org.kucro3.keleton.api.APIProvider;
import org.kucro3.keleton.api.ExportAPI;

import java.security.Permission;

@APIProvider(namespace = "mac")
public class MACAPIProvider {
    @ExportAPI(name = "Enabled")
    public static boolean IsEnabled()
    {
        return false;
    }

    @ExportAPI(name = "CheckPermission")
    public static void CheckPermission(Permission permission)
    {

    }
}
