package org.kucro3.trigger;

import java.util.*;

@SuppressWarnings("unchecked")
public class TriggerContext {
    TriggerContext()
    {
    }

    public static TriggerContext of()
    {
        return new TriggerContext();
    }

    public static TriggerContext of(Pair... pairs)
    {
        TriggerContext context = of();
        for(Pair pair : pairs)
            context.set(pair.name, pair.value);
        return context;
    }

    public <T> Optional<T> put(String name, T value)
    {
        return Optional.ofNullable((T) values.put(name, value));
    }

    public <T> void set(String name, T value)
    {
        values.put(name, value);
    }

    public <T> Optional<T> get(String name)
    {
        return Optional.ofNullable((T) values.get(name));
    }

    public boolean contains(String name)
    {
        return values.containsKey(name);
    }

    public <T> Optional<T> first(Class<T> type)
    {
        for(Map.Entry<String, Object> entry : values.entrySet())
            if(type.isInstance(entry.getValue()))
                return Optional.of((T) entry.getValue());
        return Optional.empty();
    }

    public <T> Optional<T> last(Class<T> type)
    {
        T value = null;
        for(Map.Entry<String, Object> entry : values.entrySet())
            if(type.isInstance(entry.getValue()))
                value = (T) entry.getValue();
        return Optional.ofNullable(value);
    }

    public <T> Iterator<T> iterator(Class<T> type)
    {
        ArrayList<T> list = new ArrayList<>();
        for(Map.Entry<String, Object> entry : values.entrySet())
            if(type.isInstance(entry.getValue()))
                list.add((T) entry.getValue());
        return list.iterator();
    }

    private final LinkedHashMap<String, Object> values = new LinkedHashMap<>();
}
