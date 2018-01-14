package org.kucro3.keleton.kernel.api;

import net.minecraft.launchwrapper.IClassTransformer;
import org.kucro3.keleton.kernel.KeletonKernel;
import org.kucro3.keleton.kernel.api.KeletonAPIManagerImpl;
import org.kucro3.keleton.kernel.api.KeletonAPIMethodHandleImpl;
import org.kucro3.keleton.kernel.api.KeletonAPINamespaceImpl;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.api.Sponge;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class KeletonAPIClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String className, String transformedClassName, byte[] bytes)
    {
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);

        reader.accept(node, 0);

        List<AnnotationNode> annos = node.visibleAnnotations;

        // search for annotation
        boolean flag = false;
        if(annos != null)
            for (AnnotationNode anno : annos) {
                if (anno.desc.equals(DESCRIPTOR_ANNOTATION_APICONTAINER)) {
                    System.out.println("FOUND API CONTAINER: " + transformedClassName);
                    flag = true;
                    break;
                }
            }

        if(!flag)
            return bytes;

        List<MethodNode> methods = node.methods;
        KeletonAPIManagerImpl manager = KeletonKernel.getAPIManagerImpl();

        for(MethodNode method : methods)
            if((annos = method.visibleAnnotations) != null)
                for(AnnotationNode anno : annos)
                    if(anno.desc.equals(DESCRIPTOR_ANNOATTION_IMPORTAPI))
                    {
                        Map<String, String> attrs = new HashMap<>();

                        Iterator<Object> iter = anno.values.iterator();
                        while(iter.hasNext())
                            attrs.put((String) iter.next(), (String) iter.next());

                        String namespace = attrs.get("namespace");
                        String name = attrs.get("name");

                        KeletonAPINamespaceImpl nsp = manager.getNamespaces().get(namespace);
                        KeletonAPIMethodHandleImpl mh;

                        String exceptionMessage = null;
                        boolean unsatisfied = false;

                        if(nsp == null)
                        {
                            unsatisfied = true;
                            exceptionMessage = "Unknown namespace: " + namespace;
                        }

                        if((mh = (KeletonAPIMethodHandleImpl) nsp.getExported(name).orElse(null)) == null)
                        {
                            unsatisfied = true;
                            exceptionMessage = "No such function exported: " + name;
                        }

                        if(!Type.getReturnType(method.desc).equals(Type.getReturnType(mh.method)))
                        {
                            unsatisfied = true;
                            exceptionMessage = "Unsatisfied return type";
                        }

                        Type[] arguments;
                        if(!Arrays.equals(arguments = Type.getArgumentTypes(method.desc), Type.getArgumentTypes(mh.method)))
                        {
                            unsatisfied = true;
                            exceptionMessage = "Unsatisfied method arguments";
                        }

                        method.instructions.clear();
                        method.access = ACC_PUBLIC | ACC_STATIC;
                        if(unsatisfied)
                        {
                            method.visitTypeInsn(NEW, "java/lang/UnsatisfiedLinkError");
                            method.visitInsn(DUP);
                            method.visitLdcInsn(exceptionMessage);
                            method.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsatisfiedLinkError", "<init>", "(Ljava/lang/String;)V", false);
                            method.visitInsn(ATHROW);
                            method.visitMaxs(0, 0);
                            method.visitEnd();
                        }
                        else
                        {
                            int depth = arguments.length;
                            for(int i = 0; i < depth; i++)
                                method.visitVarInsn(loadInsn(arguments[i].getDescriptor()), i);

                            method.visitMethodInsn(INVOKESTATIC,
                                    Type.getType(mh.method.getDeclaringClass()).getInternalName(),
                                    mh.method.getName(),
                                    Type.getMethodDescriptor(mh.method),
                                    false);
                            method.visitInsn(returnInsn(Type.getReturnType(method.desc).getDescriptor()));
                            method.visitMaxs(0, 0);
                            method.visitEnd();
                        }
                    }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(cw);
        return cw.toByteArray();
    }

    static int loadInsn(String descriptor)
    {
        switch(descriptor.charAt(0))
        {
            case 'L':
                return ALOAD;

            case 'B':
            case 'C':
            case 'S':
            case 'I':
            case 'Z':
                return ILOAD;

            case 'J':
                return LLOAD;

            case 'F':
                return FLOAD;

            case 'D':
                return DLOAD;

            default: // unknown
                throw new IllegalStateException("unknown load on-stack type");
        }
    }

    static int returnInsn(String returnDescriptor)
    {
        switch(returnDescriptor.charAt(0))
        {
            case 'V':
                return RETURN;

            case 'L':
                return ARETURN;

            case 'B':
            case 'C':
            case 'S':
            case 'I':
            case 'Z':
                return IRETURN;

            case 'J':
                return LRETURN;

            case 'F':
                return FRETURN;

            case 'D':
                return DRETURN;

            default: // unknown
                throw new IllegalStateException("unknown return on-stack type");
        }
    }

    private static final String DESCRIPTOR_ANNOTATION_APICONTAINER = "Lorg/kucro3/keleton/api/APIContainer;";

    private static final String DESCRIPTOR_ANNOATTION_IMPORTAPI = "Lorg/kucro3/keleton/api/ImportAPI;";
}
