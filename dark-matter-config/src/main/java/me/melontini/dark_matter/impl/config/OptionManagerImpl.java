package me.melontini.dark_matter.impl.config;

import me.melontini.dark_matter.api.base.util.classes.Tuple;
import me.melontini.dark_matter.api.config.ConfigManager;
import me.melontini.dark_matter.api.config.OptionProcessorRegistry;
import me.melontini.dark_matter.api.config.interfaces.OptionManager;
import me.melontini.dark_matter.api.config.interfaces.Processor;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Field;
import java.util.*;

public class OptionManagerImpl<T> implements OptionManager<T>, OptionProcessorRegistry<T> {

    private final Map<String, OptionProcessorEntry<T>> optionProcessors = new LinkedHashMap<>();
    private final ConfigManager<T> manager;

    final Map<Field, Set<String>> modifiedFields = new HashMap<>();
    final Map<Field, String> fieldToOption = new HashMap<>();

    final ModJsonProcessor modJsonProcessor;

    OptionManagerImpl(ConfigManager<T> manager) {
        this.manager = manager;
        this.modJsonProcessor = new ModJsonProcessor(manager);

        register(manager.getMod().getMetadata().getId() + ":mod_json", manager1 -> {
            if (!this.modJsonProcessor.done) {
                FabricLoader.getInstance().getAllMods().stream()
                        .filter(container -> container.getMetadata().containsCustomValue(this.modJsonProcessor.getKey()))
                        .forEach(this.modJsonProcessor::parseMetadata);
                this.modJsonProcessor.done = true;
            }
            return this.modJsonProcessor.modJson;
        });
    }

    @Override
    public void processFeatures() {
        optionProcessors.forEach((s, entry) -> {
            var config = entry.processor().process(this.manager);
            if (config != null && !config.isEmpty()) {
                configure(s, config);
            }
        });
    }

    private void configure(String id, Map<String, Object> config) {
        validateId(id);

        config.forEach((s, o) -> {
            try {
                manager.set(s, o);
                Field f = manager.getField(s);
                modifiedFields.computeIfAbsent(f, field -> new HashSet<>()).add(s);
                fieldToOption.put(f, s);
            } catch (NoSuchFieldException e) {
                DarkMatterLog.error("Option %s does not exist (%s)".formatted(s, id), e);
            }
        });
    }

    static void validateId(String id) {
        String[] split = id.split(":");
        if (split.length != 2) throw new IllegalArgumentException("Invalid id: " + id);
    }

    @Override
    public boolean isModified(Field f) {
        return modifiedFields.containsKey(f);
    }

    @Override
    public boolean isModified(String option) throws NoSuchFieldException {
        return isModified(manager.getField(option));
    }

    @Override
    public Tuple<String, Set<String>> blameProcessors(Field f) {
        return Tuple.of(fieldToOption.get(f), modifiedFields.getOrDefault(f, Collections.emptySet()));
    }

    @Override
    public Set<String> blameProcessors(String option) throws NoSuchFieldException {
        return modifiedFields.getOrDefault(manager.getField(option), Collections.emptySet());
    }

    @Override
    public Tuple<String, Set<String>> blameMods(Field f) {
        return Tuple.of(fieldToOption.get(f), modJsonProcessor.blameMods(f));
    }

    @Override
    public Set<String> blameMods(String option) throws NoSuchFieldException {
        return modJsonProcessor.blameMods(option);
    }


    @Override
    public void register(String id, Processor<T> processor) {
        validateId(id);
        var last = optionProcessors.put(id, new OptionProcessorEntry<>(id, processor));
        if (last != null) throw new IllegalStateException("Tried to register an option processor with the same id (%s) twice!".formatted(id));
    }

    private record OptionProcessorEntry<T>(String id, Processor<T> processor) {

    }
}
