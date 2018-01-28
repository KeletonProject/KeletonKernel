package org.kucro3.keleton.kernel.loader.klink;

import org.kucro3.keleton.kernel.asm.AnnotationUtil;
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
    public boolean trigger(TriggerContext context)
    {
        try {
            ClassNode cn = context.first(ClassNode.class).get();
            AnnotationNode an = context.first(AnnotationNode.class).get();

            Map<String, byte[]> resources = context.<Map<String, byte[]>>get("resources").get();
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

            resources.put(entryName, converted);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
