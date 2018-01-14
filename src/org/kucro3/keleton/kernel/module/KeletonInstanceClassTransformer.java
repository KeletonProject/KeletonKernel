package org.kucro3.keleton.kernel.module;

import com.google.common.eventbus.EventBus;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;
import org.kucro3.keleton.module.KeletonInstance;
import org.kucro3.keleton.module.Module;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.plugin.meta.version.ArtifactVersion;

import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

@SuppressWarnings("unchecked")
public class KeletonInstanceClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String className, String transformedClassName, byte[] bytes)
    {
        ClassReader cr = new ClassReader(bytes);
        ClassNode cn = new ClassNode();

        cr.accept(cn, 0);

        if(!cn.interfaces.contains(INTERNAL_NAME_KELETONINSTANCE)) // instanceof KeletonInstance
            return bytes;

        Map<String, Object> metadata = new HashMap<>();
        if(cn.visibleAnnotations != null)
            for(AnnotationNode an : (List<AnnotationNode>) cn.visibleAnnotations)
                if(an.desc.equals(DESCRIPTOR_MODULE))
                {
                    Iterator<Object> iter = an.values.iterator();
                    while(iter.hasNext())
                        metadata.put((String) iter.next(), iter.next());
                }

        if(metadata.isEmpty())
            return bytes;

        String id = (String) metadata.get("id");
        String name = (String) metadata.get("name");
        String version = (String) metadata.get("version");
        String description = (String) metadata.get("description");
        List<String> authors = (List<String>) metadata.get("authors");

        // implements ModContainer
        cn.interfaces.add(INTERNAL_NAME_MODCONTAINER);

        // private boolean initialized;
        cn.visitField(ACC_PRIVATE | ACC_SYNTHETIC, "gen$initialized", "Z", null, false);

        // private int classVersion;
        cn.visitField(ACC_PRIVATE | ACC_SYNTHETIC, "impl$classVersion", "I", null, 0);

        // private ModMetadata metadata;
        cn.visitField(ACC_PRIVATE | ACC_SYNTHETIC, "impl$metadata", DESCRIPTOR_MODMETADATA, null, null);

        // private ArtifactVersion processedVersion;
        cn.visitField(ACC_PRIVATE | ACC_SYNTHETIC, "impl$processedVersion", DESCRIPTOR_ARTIFACTVERSION, null, null);

        // before <init>(...)V <- initialize()V
        MethodVisitor constructor = cn.visitMethod(ACC_PRIVATE | ACC_SYNTHETIC, "gen$initialize", "()V", null, null);
        {
            Label label = new Label();

            // if(initialized) return;
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitFieldInsn(GETFIELD, cn.name, "gen$initialized", "Z");
            constructor.visitJumpInsn(IFNE, label);

            // this.processedVersion = new DefaultArtifactVersion(%id%, %version%);
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitTypeInsn(NEW, INTERNAL_NAME_DEFAULTARTIFACTVERSION);
            constructor.visitInsn(DUP);
            constructor.visitLdcInsn(id);
            constructor.visitLdcInsn(version);
            constructor.visitMethodInsn(INVOKESPECIAL, INTERNAL_NAME_DEFAULTARTIFACTVERSION, "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", false);
            constructor.visitFieldInsn(PUTFIELD, cn.name, "impl$processedVersion", DESCRIPTOR_ARTIFACTVERSION);

            // this.metadata = new ModMetadata();
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitTypeInsn(NEW, INTERNAL_NAME_MODMETADATA);
            constructor.visitInsn(DUP_X1);
            constructor.visitMethodInsn(INVOKESPECIAL, INTERNAL_NAME_MODMETADATA, "<init>", "()V", false);
            constructor.visitFieldInsn(PUTFIELD, cn.name, "impl$metadata", DESCRIPTOR_MODMETADATA);

            // this.metadata.modId = %id%;
            constructor.visitInsn(DUP);
            constructor.visitLdcInsn(id);
            constructor.visitFieldInsn(PUTFIELD, INTERNAL_NAME_MODMETADATA, "modId", "Ljava/lang/String;");

            // this.metadata.name = %name%;
            constructor.visitInsn(DUP);
            constructor.visitLdcInsn(name);
            constructor.visitFieldInsn(PUTFIELD, INTERNAL_NAME_MODMETADATA, "name", "Ljava/lang/String;");

            // this.metadata.version = %version%;
            constructor.visitInsn(DUP);
            constructor.visitLdcInsn(version);
            constructor.visitFieldInsn(PUTFIELD, INTERNAL_NAME_MODMETADATA, "version", "Ljava/lang/String;");

            // this.metadata.description = %description%;
            constructor.visitInsn(DUP);
            constructor.visitLdcInsn(description);
            constructor.visitFieldInsn(PUTFIELD, INTERNAL_NAME_MODMETADATA, "description", "Ljava/lang/String;");

            // this.metadata.authorList = Arrays.asList(%authors%);
            constructor.visitInsn(DUP);
            constructor.visitLdcInsn(authors.size());
            constructor.visitTypeInsn(ANEWARRAY, "java/lang/String");
            for(int i = 0; i < authors.size(); i++)
            {
                constructor.visitInsn(DUP);
                constructor.visitLdcInsn(i);
                constructor.visitLdcInsn(authors.get(i));
                constructor.visitInsn(AASTORE);
            }
            constructor.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "asList", "([Ljava/lang/Object;)Ljava/util/List;", false);
            constructor.visitFieldInsn(PUTFIELD, INTERNAL_NAME_MODMETADATA, "authorList", "Ljava/util/List;");

            // this.initialized = true;
            constructor.visitInsn(ICONST_1);
            constructor.visitFieldInsn(PUTFIELD, cn.name, "gen$initialized", "Z");

            constructor.visitLabel(label);
            constructor.visitInsn(RETURN);

            constructor.visitMaxs(0, 0);
            constructor.visitEnd();
        }

        // call initialize();
        MethodNode call = new MethodNode();
        {
            call.visitVarInsn(ALOAD, 0);
            call.visitMethodInsn(INVOKESPECIAL, cn.name, "gen$initialize", "()V", false);
        }

        // insert call
        for(MethodNode mn : (List<MethodNode>) cn.methods)
            if(mn.name.equals("<init>"))
                mn.instructions.insert(call.instructions);

        // method implementations
        MethodVisitor mv;
        {
            // public void bindMetadata(MetadataCollection)
            mv = cn.visitMethod(ACC_PUBLIC, "bindMetadata", "(" + DESCRIPTOR_METADATACOLLECTION + ")V", null, null);
            {
                // return;
                mv.visitInsn(RETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public List getDependants()
            mv = cn.visitMethod(ACC_PUBLIC, "getDependants", "()Ljava/util/List;", null, null);
            {
                // return Collections.emptyList();
                returnEmptyList(mv);
            }

            // public List getDependencies()
            mv = cn.visitMethod(ACC_PUBLIC, "getDependencies", "()Ljava/util/List;", null, null);
            {
                // return Collections.emptyList();
                returnEmptyList(mv);
            }

            // public Set getRequirements()
            mv = cn.visitMethod(ACC_PUBLIC, "getRequirements", "()Ljava/util/Set;", null, null);
            {
                // return Collections.emptySet();
                returnEmptySet(mv);
            }

            // public ModMetadata getMetadata()
            mv = cn.visitMethod(ACC_PUBLIC, "getMetadata", "()" + DESCRIPTOR_MODMETADATA, null, null);
            {
                // return this.metadata;
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, cn.name, "impl$metadata", DESCRIPTOR_MODMETADATA);
                mv.visitInsn(ARETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public Object getMod()
            mv = cn.visitMethod(ACC_PUBLIC, "getMod", "()Ljava/lang/Object;", null, null);
            {
                // return this;
                mv.visitVarInsn(ALOAD, 0);
                mv.visitInsn(ARETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public String getModId()
            mv = cn.visitMethod(ACC_PUBLIC, "getModId", "()Ljava/lang/String;", null, null);
            {
                // return metadata.modId;
                returnMetadataField(mv, cn.name, "modId", "Ljava/lang/String;");
            }

            // public String getName()
            mv = cn.visitMethod(ACC_PUBLIC, "getName", "()Ljava/lang/String;", null, null);
            {
                // return metadata.name;
                returnMetadataField(mv, cn.name, "name", "Ljava/lang/String;");
            }

            // public String getSortingRules()
            mv = cn.visitMethod(ACC_PUBLIC, "getSortingRules", "()Ljava/lang/String;", null, null);
            {
                // return "";
                mv.visitLdcInsn("");
                mv.visitInsn(ARETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public File getSource()
            mv = cn.visitMethod(ACC_PUBLIC, "getSource", "()Ljava/io/File;", null, null);
            {
                // return null;
                mv.visitInsn(ACONST_NULL);
                mv.visitInsn(ARETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public String getVersion()
            mv = cn.visitMethod(ACC_PUBLIC, "getVersion", "()Ljava/lang/String;", null, null);
            {
                // return metadata.version;
                returnMetadataField(mv, cn.name, "version", "Ljava/lang/String;");
            }

            // public boolean matches(Object mod)
            mv = cn.visitMethod(ACC_PUBLIC, "matches", "(Ljava/lang/Object;)Z", null, null);
            {
                // return mod == this;
                Label label0 = new Label();
                Label label1 = new Label();

                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitJumpInsn(IF_ACMPEQ, label0);
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, label1);
                mv.visitLabel(label0);
                mv.visitInsn(ICONST_1);
                mv.visitLabel(label1);
                mv.visitInsn(IRETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public void setEnabledState(boolean)
            mv = cn.visitMethod(ACC_PUBLIC, "setEnabledState", "(Z)V", null, null);
            {
                // return;
                mv.visitInsn(RETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public boolean registerBus(EventBus, LoadController)
            mv = cn.visitMethod(ACC_PUBLIC, "registerBus", "(" + DESCRIPTOR_EVENTBUS + DESCRIPTOR_LOADCONTROLLER + ")Z", null, null);
            {
                // return true;
                mv.visitInsn(ICONST_0);
                mv.visitInsn(IRETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public ArtifactVersion getProcessedVersion()
            mv = cn.visitMethod(ACC_PUBLIC, "getProcessedVersion", "()" + DESCRIPTOR_ARTIFACTVERSION, null, null);
            {
                // return processedVersion;
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, cn.name, "impl$processedVersion", DESCRIPTOR_ARTIFACTVERSION);
                mv.visitInsn(ARETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public boolean isImmutable()
            mv = cn.visitMethod(ACC_PUBLIC, "isImmutable", "()Z", null, null);
            {
                // return false;
                mv.visitInsn(ICONST_0);
                mv.visitInsn(IRETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public String getDisplayVersion()
            mv = cn.visitMethod(ACC_PUBLIC, "getDisplayVersion", "()Ljava/lang/String;", null, null);
            {
                // return metadata.version;
                returnMetadataField(mv, cn.name, "version", "Ljava/lang/String;");
            }

            // public VersionRange acceptableMinecraftVersionRange()
            mv = cn.visitMethod(ACC_PUBLIC, "acceptableMinecraftVersionRange", "()" + DESCRIPTOR_VERSIONRANGE, null, null);
            {
                // return Loader.instance().getMinecraftModContainer().getStaticVersionRange(); // using Helper
                mv.visitMethodInsn(INVOKESTATIC, INTERNAL_NAME_KELETONINSTANCECLASSTRANSFORMER, "getStaticVersionRangeHelper", "()" + DESCRIPTOR_VERSIONRANGE, false);
                mv.visitInsn(ARETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public Certificate getSigningCertificate()
            mv = cn.visitMethod(ACC_PUBLIC, "getSigningCertificate", "()" + DESCRIPTOR_CERTIFICATE, null, null);
            {
                // return null;
                mv.visitInsn(ACONST_NULL);
                mv.visitInsn(ARETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public String toString()
            mv = cn.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
            {
                // return metadata.modId;
                returnMetadataField(mv, cn.name, "modId", "Ljava/lang/String;");
            }

            // public Map getCustomModProperties()
            mv = cn.visitMethod(ACC_PUBLIC, "getCustomModProperties", "()Ljava/util/Map;", null, null);
            {
                // return ModContainer.EMPTY_PROPERTIES;
                mv.visitFieldInsn(GETSTATIC, INTERNAL_NAME_MODCONTAINER, "EMPTY_PROPERTIES", "Ljava/util/Map;");
                mv.visitInsn(ARETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public Class<?> getCustomResourcePackClass()
            mv = cn.visitMethod(ACC_PUBLIC, "getCustomResourcePackClass", "()Ljava/lang/Class;", null, null);
            {
                // return null;
                mv.visitInsn(ACONST_NULL);
                mv.visitInsn(ARETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public Map getSharedModDescriptor()
            mv = cn.visitMethod(ACC_PUBLIC, "getSharedModDescriptor", "()Ljava/util/Map;", null, null);
            {
                // return null;
                mv.visitInsn(ACONST_NULL);
                mv.visitInsn(ARETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public Disableable canBeDisabled()
            mv = cn.visitMethod(ACC_PUBLIC, "canBeDisabled", "()" + DESCRIPTOR_DISABLEABLE, null, null);
            {
                // return Disableable.NEVER;
                mv.visitMethodInsn(INVOKESTATIC, INTERNAL_NAME_KELETONINSTANCECLASSTRANSFORMER, "disableableNever", "()" + DESCRIPTOR_DISABLEABLE, false);
                mv.visitInsn(ARETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public List getOwnedPackages()
            mv = cn.visitMethod(ACC_PUBLIC, "getOwnedPackages", "()Ljava/util/List;", null, null);
            {
                // return Collections.emptyList();
                returnEmptyList(mv);
            }

            // public boolean shouldLoadInEnvironment()
            mv = cn.visitMethod(ACC_PUBLIC, "shouldLoadInEnvironment", "()Z", null, null);
            {
                // return true;
                mv.visitInsn(ICONST_1);
                mv.visitInsn(IRETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public URL getUpdateUrl()
            mv = cn.visitMethod(ACC_PUBLIC, "getUpdateUrl", "()Ljava/net/URL;", null , null);
            {
                // return null;
                mv.visitInsn(ACONST_NULL);
                mv.visitInsn(ARETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public void setClassVersion(int classVersion)
            mv = cn.visitMethod(ACC_PUBLIC, "setClassVersion", "(I)V", null, null);
            {
                // this.classVersion = classVersion;
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitFieldInsn(PUTFIELD, cn.name, "impl$classVersion", "I");
                mv.visitInsn(RETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // public int getClassVersion()
            mv = cn.visitMethod(ACC_PUBLIC, "getClassVersion", "()I", null, null);
            {
                // return this.classVersion;
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, cn.name, "impl$classVersion", "I");
                mv.visitInsn(IRETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);

        return cw.toByteArray();
    }

    public static VersionRange getStaticVersionRangeHelper()
    {
        return Loader.instance().getMinecraftModContainer().getStaticVersionRange();
    }

    public static ModContainer.Disableable disableableNever()
    {
        return ModContainer.Disableable.NEVER;
    }

    private static void returnEmptyList(MethodVisitor mv)
    {
        mv.visitMethodInsn(INVOKESTATIC, "java/util/Collections", "emptyList", "()Ljava/util/List;", false);
        mv.visitInsn(ARETURN);

        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void returnEmptySet(MethodVisitor mv)
    {
        mv.visitMethodInsn(INVOKESTATIC, "java/util/Collections", "emptySet", "()Ljava/util/Set;", false);
        mv.visitInsn(ARETURN);

        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void returnMetadataField(MethodVisitor mv, String owner, String name, String descriptor)
    {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, owner, "impl$metadata", DESCRIPTOR_MODMETADATA);
        mv.visitFieldInsn(GETFIELD, INTERNAL_NAME_MODMETADATA, name, descriptor);
        mv.visitInsn(ARETURN);

        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static final String INTERNAL_NAME_KELETONINSTANCECLASSTRANSFORMER = Type.getInternalName(KeletonInstanceClassTransformer.class);

    private static final String INTERNAL_NAME_KELETONINSTANCE = Type.getInternalName(KeletonInstance.class);

    private static final String INTERNAL_NAME_MODCONTAINER = Type.getInternalName(ModContainer.class);

    private static final String DESCRIPTOR_MODMETADATA = Type.getDescriptor(ModMetadata.class);

    private static final String INTERNAL_NAME_MODMETADATA = Type.getInternalName(ModMetadata.class);

    private static final String DESCRIPTOR_ARTIFACTVERSION = Type.getDescriptor(ArtifactVersion.class);

    private static final String INTERNAL_NAME_DEFAULTARTIFACTVERSION = Type.getInternalName(DefaultArtifactVersion.class);

    private static final String DESCRIPTOR_MODULE = Type.getDescriptor(Module.class);

    private static final String DESCRIPTOR_METADATACOLLECTION = Type.getDescriptor(MetadataCollection.class);

    private static final String DESCRIPTOR_EVENTBUS = Type.getDescriptor(EventBus.class);

    private static final String DESCRIPTOR_LOADCONTROLLER = Type.getDescriptor(LoadController.class);

    private static final String DESCRIPTOR_VERSIONRANGE = Type.getDescriptor(VersionRange.class);

    private static final String DESCRIPTOR_CERTIFICATE = Type.getDescriptor(Certificate.class);

    private static final String DESCRIPTOR_DISABLEABLE = Type.getDescriptor(ModContainer.Disableable.class);
}
