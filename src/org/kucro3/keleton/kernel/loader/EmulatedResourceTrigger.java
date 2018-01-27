package org.kucro3.keleton.kernel.loader;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.kucro3.keleton.kernel.loader.event.ModuleResourceDiscoveredEvent;

@FunctionalInterface
public interface EmulatedResourceTrigger {
    /**
     *
     * @return Whether to trigger the next trigger
     */
    public boolean trigger(LaunchClassLoader loader, ModuleResourceDiscoveredEvent event, String name, byte[] byts);
}
