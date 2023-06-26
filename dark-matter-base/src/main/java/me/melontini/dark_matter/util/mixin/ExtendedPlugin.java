package me.melontini.dark_matter.util.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.*;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.*;
import java.util.function.BiPredicate;

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
            ClassNode node = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName);
            return processAnnotations(node.visibleAnnotations, mixinClassName) &&
                    processAnnotations(node.invisibleAnnotations, mixinClassName);
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
        Map<String, Object> values = new HashMap<>();

        if (node.values == null) return values;

        for (int i = 0; i < node.values.size(); i += 2) {
            String name = (String) node.values.get(i);
            Object value = mapObjectFromAnnotation(node.values.get(i + 1));
            if (name != null && value != null) values.putIfAbsent(name, value);
        }

        return values;
    }

    public static Object mapObjectFromAnnotation(Object value) {
        return mapObjectFromAnnotation(value, true, false);
    }

    public static Object mapObjectFromAnnotation(Object value, boolean loadEnums, boolean loadClasses) {
        if (value instanceof List<?> list) {
            List<Object> process = new ArrayList<>(list.size());
            for (Object o : list) {
                process.add(mapObjectFromAnnotation(o));
            }
            return process;
        } else if (value instanceof AnnotationNode node) {
            return mapAnnotationNode(node);
        } else if (value instanceof Type type && loadClasses) {
            try {
                return Class.forName(type.getClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else if (value instanceof String[] enum0 && loadEnums) {
            try {
                Class<?> cls = Class.forName(enum0[0].replace("/", ".").substring(1, enum0[0].length() - 1));
                if (Enum.class.isAssignableFrom(cls)) {
                    value = Enum.valueOf((Class<? extends Enum>) cls, enum0[1]);
                    return value;
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            return value;
        }
        return value;
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
        if (targetClass.visibleAnnotations != null && !targetClass.visibleAnnotations.isEmpty()) {//strip our annotation from the class
            targetClass.visibleAnnotations.removeIf(node -> SHOULD_APPLY_DESC.equals(node.desc));
        }
    }
}
