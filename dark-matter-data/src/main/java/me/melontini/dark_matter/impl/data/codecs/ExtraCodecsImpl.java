package me.melontini.dark_matter.impl.data.codecs;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.Utilities;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.dynamic.Codecs;

import java.util.function.Function;

@UtilityClass
public class ExtraCodecsImpl {

    public static <K, V> Codec<V> jsonSerializerDispatch(final String typeKey, Codec<K> keyCodec, final Function<? super V, ? extends K> type, final Function<? super K, ? extends JsonSerializer<? extends V>> codec, JsonSerializationContext serializationContext, JsonDeserializationContext deserializationContext) {
        Codec<V> cc = Codecs.JSON_ELEMENT.flatXmap(element -> {
            if (!element.isJsonObject()) return DataResult.error(() -> "'%s' not a JsonObject".formatted(element));
            JsonObject object = element.getAsJsonObject();
            if (object.get(typeKey) == null) return DataResult.error(() -> "Missing required '%s' field!".formatted(typeKey));

            var keyRes = keyCodec.parse(JsonOps.INSTANCE, object.get(typeKey));
            if (keyRes.error().isPresent()) return keyRes.map(identifier -> null);

            var decoder = codec.apply(keyRes.result().orElseThrow());
            return DataResult.success(decoder.fromJson(object, deserializationContext));
        }, v -> {
            var key = type.apply(v);
            var kr = keyCodec.encodeStart(JsonOps.INSTANCE, key);
            if (kr.error().isPresent()) return kr.map(element -> null);

            JsonObject object = new JsonObject();
            object.add(typeKey, kr.result().orElseThrow());

            var encoder = codec.apply(key);
            encoder.toJson(object, Utilities.cast(v), serializationContext);

            return DataResult.success(object);
        });
        return Codecs.exceptionCatching(cc);
    }

    public static <T> JsonSerializer<T> toJsonSerializer(Codec<T> codec) {
        var mcc = codec instanceof MapCodec.MapCodecCodec<T> glue ? glue.codec() : codec.fieldOf("value");
        return new JsonSerializer<>() {
            @Override
            public void toJson(JsonObject json, T object, JsonSerializationContext context) {
                var s = mcc.encode(object, JsonOps.INSTANCE, JsonOps.INSTANCE.mapBuilder());
                s.build(json).getOrThrow(false, string -> {
                    throw new JsonParseException(string);
                });
            }

            @Override
            public T fromJson(JsonObject json, JsonDeserializationContext context) {
                return mcc.decode(JsonOps.INSTANCE, JsonOps.INSTANCE.getMap(json).getOrThrow(false, string -> {
                    throw new IllegalStateException(string);
                })).getOrThrow(false, string -> {
                    throw new JsonParseException(string);
                });
            }
        };
    }
}
