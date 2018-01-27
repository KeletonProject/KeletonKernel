package org.kucro3.keleton.kernel.loader;

import net.minecraft.launchwrapper.LaunchClassLoader;
import org.kucro3.keleton.kernel.loader.event.ModuleResourceDiscoveredEvent;

import java.util.Map;

@FunctionalInterface
public interface EmulatedHandleTrigger {
    public void trigger(LaunchClassLoader loader, ModuleResourceDiscoveredEvent event, Map<String, byte[]> resources);
}
