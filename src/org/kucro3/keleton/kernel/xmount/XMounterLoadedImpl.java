package org.kucro3.keleton.kernel.xmount;

import org.kucro3.keleton.klink.xmount.LoadedMounter;
import org.kucro3.keleton.klink.xmount.Mountable;
import org.kucro3.keleton.klink.xmount.Mounter;
import org.kucro3.klink.Klink;

import java.util.Objects;

public class XMounterLoadedImpl implements LoadedMounter {
    public XMounterLoadedImpl(String name, Mounter mounter)
    {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(mounter, "mounter");

        this.mounter = mounter;
        this.mounted = false;
        this.name = name;
    }

    public XMounterLoadedImpl(Mountable info, Mounter mounter)
    {
        this(info.name(), mounter);
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean isMounted()
    {
        return mounted;
    }

    @Override
    public void mount(Klink klink)
    {
        if(mounted)
            throw new IllegalStateException("Already mounted");

        mounter.mount(klink);
        mounted = true;
    }

    @Override
    public void unmount(Klink klink)
    {
        if(!mounted)
            throw new IllegalStateException("Not mounted yet");

        mounter.unmount(klink);
        mounted = false;
    }

    private final String name;

    private final Mounter mounter;

    private boolean mounted;
}
