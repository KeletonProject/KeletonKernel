package org.kucro3.keleton.kernel.xmount.trigger;

import com.theredpixelteam.redtea.trigger.TerminalTrigger;
import com.theredpixelteam.redtea.trigger.TriggerContext;
import org.kucro3.keleton.klink.xmount.Mountable;
import org.kucro3.keleton.klink.xmount.Mounter;
import org.kucro3.keleton.klink.xmount.XMountManager;

public class MountableRegistryTrigger implements TerminalTrigger {
    public MountableRegistryTrigger(XMountManager manager)
    {
        this.manager = manager;
    }

    @Override
    public void trigger(TriggerContext context) throws Exception
    {
        Mounter mounter = context.first(Mounter.class).get();
        Mountable info = context.first(Mountable.class).get();

        if(!manager.putMountable(info.name(), mounter))
            throw new IllegalStateException("Registry failure");
    }

    private final XMountManager manager;
}
