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
 * Klink Boot File Emulation: (TBC)
 *  -Dkeleton.kernel.emulation=bootfile
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
        String emulationPolicy = properties.getProperty(PROPERTY_EMULATION);

        emulationPolicy = emulationPolicy == null ? EMULATION_DEFAULT : emulationPolicy;

        if(emulationPolicy.equalsIgnoreCase(EMULATION_DEFAULT))
            consumer.accept(new LocalEmulated());
        else if(emulationPolicy.equalsIgnoreCase(EMULATION_CUSTOM))
        {
            File root = new File(requireProperty(PROPERTY_EMULATION_ROOT, properties));
            File modules = new File(requireProperty(PROPERTY_EMULATION_MODULES, properties));
            File bootFile = new File(requireProperty(PROPERTY_EMULATION_BOOTFILE, properties));

            consumer.accept(new CustomEmulated(root, modules, bootFile));
        }
        else if(emulationPolicy.equalsIgnoreCase(EMULATION_BOOTFILE))
        {
            // TBC
        }
        else
            throw new IllegalStateException("Illegal property: " + PROPERTY_EMULATION + "=" + emulationPolicy);
    }

    private static String requireProperty(String name, Properties properties)
    {
        return Optional.ofNullable(properties.getProperty(name))
                .orElseThrow(() -> new IllegalStateException("Property undefined: " + name));
    }

    private static final String EMULATION_DEFAULT = "default";

    private static final String EMULATION_CUSTOM = "custom";

    private static final String EMULATION_BOOTFILE = "bootfile";

    private static final String PROPERTY_EMULATION = "keleton.kernel.emulation";

    private static final String PROPERTY_EMULATION_ROOT = "keleton.kernel.emulation.root";

    private static final String PROPERTY_EMULATION_MODULES = "keleton.kernel.emulation.modules";

    private static final String PROPERTY_EMULATION_BOOTFILE = "keleton.kernel.emulation.bootfile";

    private final Consumer<Emulated> consumer;
}
