package org.kucro3.keleton.kernel.loader.klink;

import org.kucro3.keleton.exception.KeletonInternalException;
import org.kucro3.keleton.kernel.asm.AnnotationUtil;
import org.kucro3.keleton.kernel.url.inmemory.InMemoryResources;
import org.kucro3.klink.expression.ExpressionFunction;
import org.kucro3.trigger.NormalTrigger;
import org.kucro3.trigger.TriggerContext;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class KlinkLibraryConvertingTrigger implements NormalTrigger {
    @Override
    public boolean trigger(TriggerContext context) throws Exception
    {
        ClassNode cn = context.first(ClassNode.class).get();
        AnnotationNode an = context.first(AnnotationNode.class).get();

        InMemoryResources resources = context.first(InMemoryResources.class).get();
        String entryName = context.<String>get("entryName").get();

        Map<String, Object> values = AnnotationUtil.values(an);

        String namespace = (String) values.get("value");

        if(namespace == null || namespace.isEmpty())
            return true;

        if(cn.methods != null)
            for(MethodNode mn : (List<MethodNode>) cn.methods)
                AnnotationUtil.getAnnotation(mn, ExpressionFunction.class).ifPresent((annotation) -> {
                    Map<String, Object> exprInfo = AnnotationUtil.values(annotation);

                    String name;
                    exprInfo.put(
                            "name",
                            namespace + ':' + ((name = (String) exprInfo.get("name")) == null ? "" : name));

                    AnnotationUtil.setValues(annotation, exprInfo);
                });

        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        byte[] converted = cw.toByteArray();

        resources.setResource(entryName, converted);

        return true;
    }
}
