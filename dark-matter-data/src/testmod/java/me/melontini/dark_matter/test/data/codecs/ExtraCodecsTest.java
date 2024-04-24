package me.melontini.dark_matter.test.data.codecs;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.ModInitializer;

import java.lang.reflect.Type;

public class ExtraCodecsTest implements ModInitializer {
    @Override
    public void onInitialize() {
    }

    private static <T> T parse(Codec<T> codec, JsonElement element) {
        return codec.parse(JsonOps.INSTANCE, element).getOrThrow(false, string -> {
            throw new JsonParseException(string);
        });
    }

    private static <T> JsonElement encode(Codec<T> codec, T object) {
        return codec.encodeStart(JsonOps.INSTANCE, object).getOrThrow(false, string -> {
            throw new JsonParseException(string);
        });
    }

    public static final class GsonContextImpl implements JsonSerializationContext, JsonDeserializationContext {

        private final Gson gson;

        public GsonContextImpl(Gson gson) {
            this.gson = gson;
        }

        @Override
        public JsonElement serialize(Object src) {
            return gson.toJsonTree(src);
        }

        @Override
        public JsonElement serialize(Object src, Type typeOfSrc) {
            return gson.toJsonTree(src, typeOfSrc);
        }

        @Override
        public <R> R deserialize(JsonElement json, Type typeOfT) throws JsonParseException {
            return gson.fromJson(json, typeOfT);
        }
    }
}
