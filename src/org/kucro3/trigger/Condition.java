package org.kucro3.trigger;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class Condition<T> {
    public Condition()
    {
    }

    public boolean bind(Predicate<T> predicate, Pipeline pipeline)
    {
        return set.add(new Binded<>(predicate, pipeline));
    }

    public boolean unbind(Pipeline pipeline)
    {
        return set.remove(pipeline);
    }

    public int trigger(T object, TriggerContext context)
    {
        int i = 0;

        for(Binded<T> binded : set)
            if(binded.trigger(object, context))
                i++;

        return i;
    }

    private final Set<Binded<T>> set = new HashSet<>();

    private static class Binded<T>
    {
        Binded(Predicate<T> predicate, Pipeline pipeline)
        {
            this.predicate = predicate;
            this.pipeline = pipeline;
        }

        boolean trigger(T object, TriggerContext context)
        {
            if(predicate.test(object))
            {
                pipeline.trigger(context);
                return true;
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return pipeline.hashCode();
        }

        @Override
        public boolean equals(Object object)
        {
            if(object == null)
                return false;

            if(object instanceof Pipeline)
                return pipeline == object;

            if(!(object instanceof Binded))
                return false;

            Binded<T> binded = (Binded<T>) object;

            return pipeline == binded.pipeline;
        }

        private final Predicate<T> predicate;

        private final Pipeline pipeline;
    }
}
