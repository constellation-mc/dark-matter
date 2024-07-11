package me.melontini.dark_matter.impl.mixin;

import lombok.SneakyThrows;
import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.base.util.tuple.Tuple;
import me.melontini.dark_matter.api.mixin.AsmUtil;
import me.melontini.dark_matter.api.mixin.IAsmTransformer;
import me.melontini.dark_matter.api.mixin.IPluginPlugin;
import me.melontini.dark_matter.api.mixin.annotations.AsmTransformers;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.util.Annotations;

import java.util.*;

public class AsmTransformerPlugin implements IPluginPlugin {

    private final Map<Tuple<String, String>, Set<IAsmTransformer>> transformers = Collections.synchronizedMap(new HashMap<>());

    @SneakyThrows
    @Override
    public void beforeApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        ClassNode mixinNode = mixinInfo.getClassNode(ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES | ClassReader.SKIP_CODE);

        AnnotationNode node = Annotations.getVisible(mixinNode, AsmTransformers.class);
        if (node == null) return;

        List<Type> types = AsmUtil.getAnnotationValue(node, "value", null);
        if (types == null) return;

        for (Type type : types) {
            Class<?> cls = Class.forName(type.getClassName());
            if (!IAsmTransformer.class.isAssignableFrom(cls)) continue;

            Set<IAsmTransformer> transformers = this.transformers.computeIfAbsent(Tuple.of(mixinInfo.getClassName(), targetClassName), k -> new LinkedHashSet<>());
            transformers.add((IAsmTransformer) Reflect.setAccessible(cls.getDeclaredConstructor()).newInstance());
            DarkMatterLog.debug("Added transformer {} to mixin {}", type.getClassName(), mixinInfo.getClassName());
        }

        Set<IAsmTransformer> transformers = this.transformers.get(Tuple.of(mixinInfo.getClassName(), targetClassName));
        if (transformers != null) transformers.forEach(transformer -> transformer.beforeApply(targetClass, mixinInfo));
    }

    @Override
    public void afterApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        Set<IAsmTransformer> transformers = this.transformers.get(Tuple.of(mixinInfo.getClassName(), targetClassName));
        if (transformers != null) transformers.forEach(transformer -> transformer.afterApply(targetClass, mixinInfo));
    }
}
