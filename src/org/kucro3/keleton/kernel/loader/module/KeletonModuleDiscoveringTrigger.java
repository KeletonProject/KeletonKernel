package org.kucro3.keleton.kernel.loader.module;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.theredpixelteam.redtea.trigger.NormalTrigger;
import com.theredpixelteam.redtea.trigger.TriggerContext;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.kucro3.keleton.emulated.EmulatedHandle;
import org.kucro3.keleton.kernel.KeletonKernel;
import org.kucro3.keleton.kernel.module.KeletonModuleImpl;
import org.kucro3.keleton.kernel.module.ModuleCollection;
import org.kucro3.keleton.module.KeletonInstance;
import org.kucro3.keleton.module.Module;
import org.kucro3.keleton.module.event.KeletonLoaderEvent;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.event.cause.Cause;

import java.net.URL;
import java.util.Objects;

public class KeletonModuleDiscoveringTrigger implements NormalTrigger {
    public KeletonModuleDiscoveringTrigger(ModuleCollection collection)
    {
        this.collection = collection;
    }

    @Override
    public boolean trigger(TriggerContext context) throws Exception
    {
        URL url = context.first(URL.class).get();
        ClassNode cn = context.first(ClassNode.class).get();
        EmulatedHandle handle = context.first(EmulatedHandle.class).get();
        LaunchClassLoader loader = context.first(LaunchClassLoader.class).get();

        Class<?> mainClass = loader.findClass(cn.name.replace("/", "."));
        Module info = Objects.requireNonNull(mainClass.getAnnotation(Module.class));

        KeletonLoaderEvent.Pre pre = new LoaderEventImpl.Pre(Cause.source(info).build(), info);
        KeletonKernel.postEvent(pre);

        if (pre.isCancelled()) {
            Cause cause = Cause.source(info).build();

            if (pre.isCancelledWithCause())
                cause = cause.merge(pre.getCancellationCause().get());

            KeletonKernel.postEvent(new LoaderEventImpl.Cancelled(cause, info));

            return false;
        }

        // construct instance
        Object object;
        try {
            Injector injector = Guice.createInjector(new KeletonModuleInjection(info));
            object = injector.getInstance(mainClass);
        } catch (Exception e) {
            KeletonKernel.postEvent(new LoaderEventImpl.Ignored(
                    Cause.source(info).build(),
                    info,
                    "Failed to construct module \"" + info.id() +  "\" of emulated: " + handle.getPath()));

            return false;
        }

        // check instance
        if(!(object instanceof KeletonInstance))
        {
            KeletonKernel.postEvent(new LoaderEventImpl.Ignored(
                    Cause.source(info).build(),
                    info,
                    "Class " + mainClass.getCanonicalName()
                            + " seems to be a module with id \"" + info.id()
                            + "\" but not a instance of KeletonInstance, IGNORED"
            ));

            return false;
        }

        KeletonInstance instance = (KeletonInstance) object;
        KeletonModuleImpl impl = new KeletonModuleImpl(handle, url, instance, info);
        collection.addModule(impl);

        context.put("module", impl);
        context.put("info", info);

        return true;
    }

    private final ModuleCollection collection;

    public static class KeletonModuleInjection extends AbstractModule
    {
        public KeletonModuleInjection(Module info)
        {
            this.info = info;
        }

        @Override
        protected void configure()
        {
        }

        @Provides
        public Logger provideLogger()
        {
            return LoggerFactory.getLogger(info.id());
        }

        private final Module info;

    }
}
