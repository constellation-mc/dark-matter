package me.melontini.dark_matter.impl.base.util.mixin;

import me.melontini.dark_matter.api.base.util.mixin.IPluginPlugin;
import me.melontini.dark_matter.api.base.util.mixin.Publicize;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicInteger;

@ApiStatus.Internal
public class PublicizePlugin implements IPluginPlugin {

    private static final String PUBLICIZE_DESC = "L" + Publicize.class.getName().replace(".", "/") + ";";

    @Override //Note:
    public void afterApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (targetClass.fields != null && !targetClass.fields.isEmpty()) {
            for (FieldNode fieldNode : targetClass.fields) {
                if (fieldNode.visibleAnnotations != null && !fieldNode.visibleAnnotations.isEmpty()) {
                    for (AnnotationNode annotationNode : fieldNode.visibleAnnotations) {
                        if (PUBLICIZE_DESC.equals(annotationNode.desc)) {
                            publicize(fieldNode);
                            fieldNode.visibleAnnotations.removeIf(node -> PUBLICIZE_DESC.equals(node.desc));
                            break;
                        }
                    }
                }
            }
        }

        if (targetClass.methods != null && !targetClass.methods.isEmpty()) {
            for (MethodNode methodNode : targetClass.methods) {
                if (methodNode.visibleAnnotations != null && !methodNode.visibleAnnotations.isEmpty()) {
                    for (AnnotationNode annotationNode : methodNode.visibleAnnotations) {
                        if (PUBLICIZE_DESC.equals(annotationNode.desc)) {
                            publicize(methodNode);
                            methodNode.visibleAnnotations.removeIf(node -> PUBLICIZE_DESC.equals(node.desc));
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void publicize(MethodNode methodNode) {
        AtomicInteger integer = new AtomicInteger(methodNode.access);
        publicize(integer);
        if (integer.get() != methodNode.access) {
            DarkMatterLog.debug("Publicized method: " + methodNode.name + methodNode.desc);
            methodNode.access = integer.get();
        }
    }

    private static void publicize(FieldNode fieldNode) {
        AtomicInteger integer = new AtomicInteger(fieldNode.access);
        publicize(integer);
        if (integer.get() != fieldNode.access) {
            DarkMatterLog.debug("Publicized field: " + fieldNode.name + fieldNode.desc);
            fieldNode.access = integer.get();
        }
    }

    private static void publicize(AtomicInteger access) {
        if (Modifier.isPrivate(access.get())) {
            access.set((access.get() & ~Opcodes.ACC_PRIVATE) | Opcodes.ACC_PUBLIC);
        }

        if (Modifier.isProtected(access.get())) {
            access.set((access.get() & ~Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC);
        }
    }

}
