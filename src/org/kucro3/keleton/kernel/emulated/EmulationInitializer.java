package org.kucro3.keleton.kernel.emulated;

import org.kucro3.keleton.emulated.Emulated;
import org.kucro3.keleton.kernel.emulated.impl.CustomEmulated;
import org.kucro3.keleton.kernel.emulated.impl.LocalEmulated;

import java.io.File;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * Default Emulation:
 *  -Dkeleton.kernel.emulation=default
 *
 * Custom Emulation:
 *  -Dkeleton.kernel.emulation=custom
 *  -Dkeleton.kernel.emulation.root= ROOT DIRECTORY
 *  -Dkeleton.kernel.emulation.modules= MODULE DIRECTORY
 *  -Dkeleton.kernel.emulation.bootfile= BOOT FILE
 *
 * Net Emulation:
 *  TBC
 *
 * KeletonFabric Emulation:
 *  TBC
 *
 * @author Kumonda221
 */
public class EmulationInitializer {
    public EmulationInitializer(Consumer<Emulated> consumer)
    {
        this.consumer = consumer;
    }

    public void initialize(Properties properties)
    {
        String emulationPolicy = properties.getProperty("keleton.kernel.emulation");

        emulationPolicy = emulationPolicy == null ? "default" : emulationPolicy;

        if(emulationPolicy.equalsIgnoreCase("default"))
            consumer.accept(new LocalEmulated());
        else if(emulationPolicy.equalsIgnoreCase("custom"))
        {
            File root;
            File modules;
            File bootFile;

            root = new File(Optional.of(properties.getProperty("keleton.kernel.emulation.root"))
                    .orElseThrow(() -> new IllegalStateException("Property undefined: keleton.kernel.emulation.root")));

            modules = new File(Optional.of(properties.getProperty("keleton.kernel.emulation.modules"))
                    .orElseThrow(() -> new IllegalStateException("Property undefined: keleton.kernel.emulation.modules")));

            bootFile = new File(Optional.of("keleton.kernel.emulation.bootfile")
                    .orElseThrow(() -> new IllegalStateException("Property undefined: keleton.kernel.emulation.bootfile")));

            consumer.accept(new CustomEmulated(root, modules, bootFile));
        }
        else
            throw new IllegalStateException("Illegal property: keleton.kernel.emulation=" + emulationPolicy);
    }

    private final Consumer<Emulated> consumer;
}
