package me.melontini.dark_matter.impl.base.util.mixin;

import me.melontini.dark_matter.api.base.util.mixin.AsmUtil;
import me.melontini.dark_matter.api.base.util.mixin.IPluginPlugin;
import me.melontini.dark_matter.api.base.util.mixin.annotations.MixinPredicate;
import me.melontini.dark_matter.api.base.util.mixin.annotations.Mod;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@ApiStatus.Internal
public class MixinPredicatePlugin implements IPluginPlugin {

    private static final String PREDICATE_DESC = "L" + MixinPredicate.class.getName().replace(".", "/") + ";";

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName, ClassNode mixinNode, List<AnnotationNode> mergedAnnotations) {
        if (mergedAnnotations.isEmpty()) return true;

        Optional<AnnotationNode> optional = mergedAnnotations.stream().filter(node -> node.desc.equals(PREDICATE_DESC)).findFirst();

        AtomicBoolean apply = new AtomicBoolean(true);
        optional.ifPresent(node -> process(apply, node));

        return apply.get();
    }

    @Override
    public void afterApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (targetClass.visibleAnnotations != null && !targetClass.visibleAnnotations.isEmpty()) {
            targetClass.visibleAnnotations.removeIf(node -> PREDICATE_DESC.equals(node.desc));
        }
    }

    private static void process(AtomicBoolean bool, AnnotationNode node) {
        Map<String, Object> values = AsmUtil.mapAnnotationNode(node);

        if (values.isEmpty()) return;

        if (!bool.get()) return;
        bool.set(checkMods(values));
    }

    private static boolean checkMods(@NotNull Map<String, Object> values) {
        List<Map<String, Object>> array = (List<Map<String, Object>>) values.getOrDefault("mods", AsmUtil.emptyAnnotationList());
        if (array.isEmpty()) return true;

        for (Map<String, Object> map : array) {
            try {
                String id = (String) map.get("value");
                Mod.State state = (Mod.State) map.getOrDefault("state", Mod.State.LOADED);
                String version = (String) map.getOrDefault("version", "*");
                VersionPredicate predicate = VersionPredicate.parse(version);

                Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(id);

                return switch (state) {
                    case LOADED -> {
                        boolean b = container.filter(modContainer -> predicate.test(modContainer.getMetadata().getVersion())).isPresent();
                        DarkMatterLog.debug("Checking mod {}. {} ({}): {}", id, state, version, b);
                        yield b;
                    }
                    case NOT_LOADED -> {
                        boolean b = container.map(modContainer -> !predicate.test(modContainer.getMetadata().getVersion())).orElse(true);
                        DarkMatterLog.debug("Checking mod {}. {} ({}): {}", id, state, version, b);
                        yield  b;
                    }
                };
            } catch (Exception e) {
                DarkMatterLog.error("Error while checking mods", e);
                return true;
            }
        }

        return true;
    }

}
