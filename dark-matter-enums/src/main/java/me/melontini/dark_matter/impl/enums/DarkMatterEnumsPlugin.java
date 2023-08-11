package me.melontini.dark_matter.impl.enums;

import me.melontini.dark_matter.util.mixin.ExtendedPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.lang.reflect.Modifier;
import java.util.Objects;

@ApiStatus.Internal
public class DarkMatterEnumsPlugin extends ExtendedPlugin {
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        super.preApply(targetClassName, targetClass, mixinClassName, mixinInfo);

        if (mixinClassName.contains("enhanced_enums")) {
            if (mixinClassName.contains("enhanced_enums.EnchantmentTargetMixin")) {
                hackEnchantmentTarget(targetClass);//hacking the enum by implementing isAcceptableItem and removing "abstract".
                //Will this break something? Probably yes. But this was a fun experiment nonetheless.
            }
        }
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        super.postApply(targetClassName, targetClass, mixinClassName, mixinInfo);

        if (mixinClassName.contains("enhanced_enums")) {
            for (MethodNode method : targetClass.methods) {
                if (Objects.equals(method.name, "dark_matter$extendEnum")) {
                    method.access = (method.access & ~Opcodes.ACC_PRIVATE) | Opcodes.ACC_PUBLIC;
                    //bitwise the private away in case ReflectionUtil.setAccessible() breaks.
                }
            }
        }
    }

    private static void hackEnchantmentTarget(ClassNode targetClass) {
        //final String resolvedEnchantmentTarget = MAPPING_RESOLVER.mapClassName("intermediary", "net.minecraft.class_1886");
        final String resolvedItem = MAPPING_RESOLVER.mapClassName("intermediary", "net.minecraft.class_1792");
        final String resolvedIsAcceptableItem = MAPPING_RESOLVER.mapMethodName("intermediary", "net.minecraft.class_1886", "method_8177", "(Lnet/minecraft/class_1792;)Z");

        if (Modifier.isAbstract(targetClass.access)) {
            targetClass.access = targetClass.access & ~Opcodes.ACC_ABSTRACT;
        }

        for (MethodNode method : targetClass.methods) {
            if (method.name.equals(resolvedIsAcceptableItem)) {
                if (Modifier.isAbstract(method.access)) {
                    method.access = method.access & ~Opcodes.ACC_ABSTRACT;

                    if (method.instructions == null) method.instructions = new InsnList();
                    method.instructions.clear();

                    Label l0 = new Label();
                    Label l1 = new Label();

                    method.visitLabel(l0);
                    method.visitInsn(Opcodes.ICONST_0);
                    method.visitInsn(Opcodes.IRETURN);

                    method.visitLabel(l1);
                    method.visitLocalVariable("this", "L" + targetClass.name.replace(".", "/") + ";", null, l0, l1, 0);
                    method.visitLocalVariable(MAPPING_RESOLVER.getCurrentRuntimeNamespace().equals("intermediary") ? "$$0" : "item", "L" + resolvedItem.replace(".", "/") + ";", null, l0, l1, 1);
                    method.visitMaxs(2, 2);
                    method.visitEnd();

                    break;
                }
            }
        }
    }
}
