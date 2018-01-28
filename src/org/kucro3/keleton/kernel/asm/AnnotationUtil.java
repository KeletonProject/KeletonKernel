package org.kucro3.keleton.kernel.asm;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.annotation.Annotation;
import java.util.*;

@SuppressWarnings("unchecked")
public class AnnotationUtil {
    private AnnotationUtil()
    {
    }

    public static Map<String, Object> values(AnnotationNode node)
    {
        Map<String, Object> map = new HashMap<>();
        if(node != null && node.values != null)
        {
            Iterator<Object> iter = node.values.iterator();
            while(iter.hasNext())
                map.put((String) iter.next(), iter.next());
        }
        return map;
    }

    public static void setValues(AnnotationNode node, Map<String, Object> map)
    {
        if(node.values == null)
            node.values = new ArrayList<>();
        else
            node.values.clear();

        for(Map.Entry<String, Object> entry : map.entrySet())
        {
            node.values.add(entry.getKey());
            node.values.add(entry.getValue());
        }
    }

    public static boolean isAnnotated(ClassNode cn, Class<? extends Annotation> type)
    {
        return getAnnotation(cn, type).isPresent();
    }

    public static boolean isAnnotated(MethodNode mn, Class<? extends Annotation> type)
    {
        return getAnnotation(mn, type).isPresent();
    }

    public static boolean isAnnotated(FieldNode fn, Class<? extends Annotation> type)
    {
        return getAnnotation(fn, type).isPresent();
    }

    static Optional<AnnotationNode> getAnnotation(List<AnnotationNode> list, Class<? extends Annotation> type)
    {
        if(list == null)
            return Optional.empty();

        String descriptor = Type.getDescriptor(type);
        for(AnnotationNode an : list)
            if(an.desc.equals(descriptor))
                return Optional.of(an);

        return Optional.empty();
    }

    public static Optional<AnnotationNode> getAnnotation(ClassNode cn, Class<? extends Annotation> type)
    {
        return getAnnotation(cn.visibleAnnotations, type);
    }

    public static Optional<AnnotationNode> getAnnotation(MethodNode mn, Class<? extends Annotation> type)
    {
        return getAnnotation(mn.visibleAnnotations, type);
    }

    public static Optional<AnnotationNode> getAnnotation(FieldNode fn, Class<? extends Annotation> type)
    {
        return getAnnotation(fn.visibleAnnotations, type);
    }
}
