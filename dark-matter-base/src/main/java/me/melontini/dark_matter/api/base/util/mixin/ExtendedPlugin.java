package me.melontini.dark_matter.api.base.util.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.*;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.*;
import java.util.function.BiPredicate;

@ApiStatus.Obsolete
public class ExtendedPlugin implements IMixinConfigPlugin {

    protected static final MappingResolver MAPPING_RESOLVER = FabricLoader.getInstance().getMappingResolver();
    protected static final BiPredicate<String, Mod.Mode> MOD_PREDICATE = (s, mode) -> switch (mode) {
        case LOADED -> FabricLoader.getInstance().isModLoaded(s);
        case NOT_LOADED -> !FabricLoader.getInstance().isModLoaded(s);
    };

    protected static final EnvType ENV_TYPE = FabricLoader.getInstance().getEnvironmentType();
    protected static final Version MC_VERSION = parseMCVersion();
    private static final List<Map<String, Object>> EMPTY_ANN_ARRAY = Collections.unmodifiableList(new ArrayList<>());
    private static final String SHOULD_APPLY_DESC = "L" + MixinShouldApply.class.getName().replace(".", "/") + ";";
    private final IPluginPlugin shouldApplyPlugin = ExtendablePlugin.DefaultPlugins.shouldApplyPlugin();

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        try {
            List<AnnotationNode> annotationNodes = new ArrayList<>();
            ClassNode node = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName);

            if (node.visibleAnnotations != null) annotationNodes.addAll(node.visibleAnnotations);
            if (node.invisibleAnnotations != null) annotationNodes.addAll(node.invisibleAnnotations);

            return shouldApplyPlugin.shouldApplyMixin(targetClassName, mixinClassName, node, annotationNodes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected final boolean processAnnotations(List<AnnotationNode> nodes, String mixinClassName) {
        if (nodes == null || nodes.isEmpty()) return true;

        boolean load = true;
        for (AnnotationNode node : nodes) {
            if (SHOULD_APPLY_DESC.equals(node.desc)) {
                Map<String, Object> values = mapAnnotationNode(node);

                if (values.isEmpty()) continue;

                if (!load) break;
                load = checkMods(values);

                if (!load) break;
                load = checkMCVersion(values);
            }
        }
        return load;
    }

    public static boolean checkMods(Map<String, Object> values) {
        List<Map<String, Object>> array = (List<Map<String, Object>>) values.getOrDefault("mods", EMPTY_ANN_ARRAY);
        if (array.isEmpty()) return true;

        for (int i = 0; i < array.size(); i += 2) {
            String name = (String) array.get(i).get("value");
            Mod.Mode mode = (Mod.Mode) array.get(i).getOrDefault("mode", Mod.Mode.LOADED);

            if (!MOD_PREDICATE.test(name, mode)) {
                return false;
            }
        }

        return true;
    }

    public static boolean checkMCVersion(Map<String, Object> values) {
        String version = (String) values.getOrDefault("mcVersion", "");

        if (version.isEmpty()) return true;

        try {
            var predicate = VersionPredicate.parse(version);
            return predicate.test(MC_VERSION);
        } catch (VersionParsingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> mapAnnotationNode(AnnotationNode node) {
        return AsmUtil.mapAnnotationNode(node);
    }

    public static Object mapObjectFromAnnotation(Object value) {
        return mapObjectFromAnnotation(value, true, false);
    }

    public static Object mapObjectFromAnnotation(Object value, boolean loadEnums, boolean loadClasses) {
        return AsmUtil.mapObjectFromAnnotation(value, loadEnums, loadClasses);
    }

    public static Version parseMCVersion() {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer("minecraft");

        if (container.isPresent()) {
            Version version = container.get().getMetadata().getVersion();
            if (version instanceof SemanticVersion) {
                return version;
            }
        }
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        this.shouldApplyPlugin.afterApply(targetClassName, targetClass, mixinClassName, mixinInfo);
    }
}
