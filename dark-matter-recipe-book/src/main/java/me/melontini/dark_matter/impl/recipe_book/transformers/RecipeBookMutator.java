package me.melontini.dark_matter.impl.recipe_book.transformers;

import me.melontini.dark_matter.api.mixin.IAsmTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class RecipeBookMutator implements IAsmTransformer {

    private static final String IMMUTABLE_LIST = "com/google/common/collect/ImmutableList";
    private static final String IMMUTABLE_MAP = "com/google/common/collect/ImmutableMap";
    private static final String LISTS = "com/google/common/collect/Lists"; //newArrayList(Ljava/lang/Iterable;)Ljava/util/ArrayList;
    private static final String MAPS = "com/google/common/collect/Maps"; //newHashMap(Ljava/util/Map;)Ljava/util/HashMap

    @Override
    public void beforeApply(ClassNode targetClass, IMixinInfo mixinInfo) {
        for (MethodNode method : targetClass.methods) {
            if ("<clinit>".equals(method.name)) {
                for (AbstractInsnNode instruction : method.instructions) {
                    if (instruction instanceof MethodInsnNode mins) {
                        if ("of".equals(mins.name)) {
                            if (IMMUTABLE_LIST.equals(mins.owner)) {
                                method.instructions.insert(mins, new MethodInsnNode(Opcodes.INVOKESTATIC, LISTS, "newArrayList", "(Ljava/lang/Iterable;)Ljava/util/ArrayList;", false));
                                method.visitMaxs(method.maxStack + 1, method.maxLocals + 1);
                            } else if (IMMUTABLE_MAP.equals(mins.owner)) {
                                method.instructions.insert(mins, new MethodInsnNode(Opcodes.INVOKESTATIC, MAPS, "newHashMap", "(Ljava/util/Map;)Ljava/util/HashMap;", false));
                                method.visitMaxs(method.maxStack + 1, method.maxLocals + 1);
                            }
                        }
                    }
                }
            }
        }
    }
}
