package org.kucro3.keleton.kernel.xmount;

import org.kucro3.keleton.api.APIProvider;
import org.kucro3.keleton.api.ExportAPI;
import org.kucro3.keleton.klink.xmount.XMountManager;

@APIProvider(namespace = "kernel-klink-xmount")
public class XMountAPIProvider {
    @ExportAPI(name = "GetXMountManager")
    public static XMountManager GetXMountManager()
    {
        return getManager();
    }

    public static XMountManagerImpl getManager()
    {
        return manager;
    }

    private static final XMountManagerImpl manager = new XMountManagerImpl(XMounterLoadedImpl::new);
}
