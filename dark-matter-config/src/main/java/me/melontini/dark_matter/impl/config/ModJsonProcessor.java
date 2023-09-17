package me.melontini.dark_matter.impl.config;

import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.config.ConfigManager;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

public class ModJsonProcessor {

    private static final Map<Class<?>, Function<Number, ?>> TYPES = Utilities.consume(new HashMap<>(), map -> {
        map.put(Byte.class, Number::byteValue);
        map.put(Short.class, Number::shortValue);
        map.put(Integer.class, Number::intValue);
        map.put(Long.class, Number::longValue);
        map.put(Float.class, Number::floatValue);
        map.put(Double.class, Number::doubleValue);

        map.put(byte.class, Number::byteValue);
        map.put(short.class, Number::shortValue);
        map.put(int.class, Number::intValue);
        map.put(long.class, Number::longValue);
        map.put(float.class, Number::floatValue);
        map.put(double.class, Number::doubleValue);
    });

    final Map<String, Object> modJson = new LinkedHashMap<>();
    final Map<Field, Set<String>> modBlame = new HashMap<>();
    final String json_key;
    final ConfigManager<?> manager;
    boolean done = false;

    ModJsonProcessor(ConfigManager<?> manager) {
        this.json_key = manager.getMod().getMetadata().getId() + ":config-" + manager.getName();
        this.manager = manager;
    }

    void parseMetadata(ModContainer mod) {
        if (!mod.getMetadata().containsCustomValue(this.json_key)) return;

        CustomValue customValue = mod.getMetadata().getCustomValue(this.json_key);
        if (customValue.getType() != CustomValue.CvType.ARRAY)
            DarkMatterLog.error("{} must be an array. Mod: {} Type: {}", this.json_key, mod.getMetadata().getId(), customValue.getType());
        else {
            CustomValue.CvArray array = customValue.getAsArray();

            for (CustomValue value : array) {
                CustomValue.CvObject object = value.getAsObject();

                if (object.containsKey("mods")) {
                    CustomValue.CvObject mods = object.get("mods").getAsObject();
                    for (Map.Entry<String, CustomValue> mod_entry : mods) {
                        if (!testModVersion(mod_entry.getKey(), mod_entry.getValue().getAsString(), mod.getMetadata().getId()))
                            return;
                    }
                }

                if (object.containsKey("values")) processValues(object.get("values").getAsObject(), mod);
            }
        }
    }

    private void processValues(CustomValue.CvObject values, ModContainer mod) {
        for (Map.Entry<String, CustomValue> feature : values) {
            Field f;
            try {
                f = manager.getField(feature.getKey());
            } catch (NoSuchFieldException e) {
                DarkMatterLog.error("Couldn't find option {}. Mod: {}", feature.getKey(), mod.getMetadata().getId());
                return;
            }

            switch (feature.getValue().getType()) {
                case BOOLEAN -> addModJson(mod, f, feature.getKey(), feature.getValue().getAsBoolean());
                case STRING -> addModJson(mod, f, feature.getKey(), feature.getValue().getAsString());
                case NULL -> addModJson(mod, f, feature.getKey(), null);
                case NUMBER -> addModJson(mod, f, feature.getKey(), TYPES.get(f.getType()).apply(feature.getValue().getAsNumber()));
                default ->
                        DarkMatterLog.error("Unsupported {} type. Mod: {}, Type: {}", this.json_key, mod.getMetadata().getId(), feature.getValue().getType());
            }
        }
    }

    Set<String> blameMods(String feature) throws NoSuchFieldException {
        return modBlame.getOrDefault(manager.getField(feature), Collections.emptySet());
    }

    Set<String> blameMods(Field field) {
        return modBlame.getOrDefault(field, Collections.emptySet());
    }

    String getKey() {
        return this.json_key;
    }

    private void addModJson(ModContainer mod, Field field, String feature, Object value) {
        modJson.put(feature, value);
        modBlame.computeIfAbsent(field, k -> new LinkedHashSet<>()).add(mod.getMetadata().getId());
    }

    static boolean testModVersion(String modId, String predicate, String modBlame) {
        Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer(modId);
        if (mod.isPresent()) {
            try {
                VersionPredicate version = VersionPredicate.parse(predicate);
                return version.test(mod.get().getMetadata().getVersion());
            } catch (VersionParsingException e) {
                DarkMatterLog.error("Couldn't parse version predicate for {} provided by {}", modId, modBlame);
                return false;
            }
        }
        return false;
    }
}
