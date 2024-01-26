package me.melontini.dark_matter.api.minecraft.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import me.melontini.dark_matter.impl.minecraft.mixin.data.JsonDataLoaderAccessor;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.Map;

public abstract class JsonCodecDataLoader<T> extends SinglePreparationResourceReloader<Map<Identifier, T>> implements IdentifiableResourceReloadListener {

    private final JsonDataLoader loader;
    private final Identifier fabricId;
    private final Codec<T> codec;

    public JsonCodecDataLoader(String dataType, Identifier fabricId, Codec<T> codec) {
        this.loader = new JsonDataLoader(new Gson(), dataType) {
            @Override
            protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
                throw new IllegalStateException("apply must never be reached!");
            }
        };
        this.fabricId = fabricId;
        this.codec = codec;
    }

    @Override
    public Identifier getFabricId() {
        return this.fabricId;
    }


    @Override
    protected Map<Identifier, T> prepare(ResourceManager manager, Profiler profiler) {
        var data = ((JsonDataLoaderAccessor)this.loader).dark_matter$prepare(manager, profiler);
        Map<Identifier, T> map = new HashMap<>();
        data.forEach((identifier, element) -> map.put(identifier, this.codec.parse(JsonOps.INSTANCE, element).getOrThrow(false, string -> {
            throw new JsonParseException(string);
        })));
        return map;
    }
}
