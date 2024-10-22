package me.melontini.dark_matter.api.data.codecs;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.ColorUtil;
import me.melontini.dark_matter.impl.data.codecs.ExtraCodecsImpl;
import me.melontini.dark_matter.impl.data.codecs.SafeEitherCodec;
import me.melontini.dark_matter.impl.data.codecs.SafeEitherMapCodec;
import me.melontini.dark_matter.impl.data.codecs.SafeOptionalCodec;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class ExtraCodecs {

  public static final Codec<Integer> COLOR = either(
          Codec.INT, Codec.intRange(0, 255).listOf())
      .comapFlatMap(
          e -> e.map(DataResult::success, integers -> {
            if (integers.size() != 3)
              return DataResult.error(() -> "colors array must contain exactly 3 colors (RGB)");
            return DataResult.success(
                ColorUtil.toColor(integers.get(0), integers.get(1), integers.get(2)));
          }),
          Either::left);

  @Contract(value = "_, _ -> new", pure = true)
  public static <F, S> @NotNull Codec<Either<F, S>> either(
      final Codec<F> first, final Codec<S> second) {
    return new SafeEitherCodec<>(first, second);
  }

  @Contract("_, _ -> new")
  public static <F, S> @NotNull MapCodec<Either<F, S>> either(
      final MapCodec<F> first, final MapCodec<S> second) {
    return new SafeEitherMapCodec<>(first, second);
  }

  /**
   * Unlike the vanilla alternative, ({@link Codec#optionalField(String, Codec)}) this codec does not ignore exceptions.
   */
  public static <F> MapCodec<F> optional(
      final String name, final Codec<F> elementCodec, F defaultValue) {
    return optional(name, elementCodec)
        .xmap(
            f -> f.orElse(defaultValue),
            f -> Objects.equals(f, defaultValue) ? Optional.empty() : Optional.of(f));
  }

  /**
   * Unlike the vanilla alternative, ({@link Codec#optionalField(String, Codec)}) this codec does not ignore exceptions.
   */
  @Contract("_, _ -> new")
  public static <F> @NotNull MapCodec<Optional<F>> optional(
      final String name, final Codec<F> elementCodec) {
    return new SafeOptionalCodec<>(name, elementCodec);
  }

  /**
   * A list codec which accepts both lists and singular entries.
   */
  public static <T> Codec<List<T>> list(Codec<T> codec) {
    return either(codec, codec.listOf())
        .xmap(e -> e.map(ImmutableList::of, Function.identity()), Either::right);
  }

  /**
   * A weighted list codec which accepts both lists and singular entries.
   */
  public static <T> Codec<WeightedList<T>> weightedList(Codec<T> codec) {
    return either(codec, WeightedList.createCodec(codec))
        .xmap(
            e -> e.map(
                entry -> {
                  WeightedList<T> list = new WeightedList<>();
                  list.add(entry, 1);
                  return list;
                },
                Function.identity()),
            Either::right);
  }

  public static <K, V> Codec<V> mapLookup(@NotNull Codec<K> keyCodec, @NotNull BiMap<K, V> lookup) {
    return keyCodec.flatXmap(
        key -> Optional.ofNullable(lookup.get(key))
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(() -> "Unknown type: %s".formatted(key))),
        eventType -> Optional.ofNullable(lookup.inverse().get(eventType))
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(() -> "Unknown type: %s".formatted(eventType))));
  }

  @ApiStatus.Experimental
  public static <T extends Enum<T>> Codec<T> enumCodec(Class<T> cls) {
    return Codec.STRING.comapFlatMap(
        string -> {
          try {
            return DataResult.success(Enum.valueOf(cls, string.toUpperCase(Locale.ROOT)));
          } catch (IllegalArgumentException e) {
            return DataResult.error(() -> "No such enum constant %s!".formatted(string));
          }
        },
        t -> t.name().toLowerCase(Locale.ROOT));
  }

  @ApiStatus.Experimental
  public static <T, C extends JsonSerializationContext & JsonDeserializationContext> @NotNull Codec<T> fromJsonSerializer(JsonSerializer<T> serializer, C context) {
    return fromJsonSerializer(serializer, context, context);
  }

  @ApiStatus.Experimental
  public static <T> @NotNull Codec<T> fromJsonSerializer(
      JsonSerializer<T> serializer,
      JsonSerializationContext serializationContext,
      JsonDeserializationContext deserializationContext) {
    Codec<T> codec = Codecs.JSON_ELEMENT.flatXmap(
        element -> {
          if (!element.isJsonObject())
            return DataResult.error(() -> "Not a JsonObject %s".formatted(element));
          return DataResult.success(
              serializer.fromJson(element.getAsJsonObject(), deserializationContext));
        },
        t -> {
          JsonObject object = new JsonObject();
          serializer.toJson(object, t, serializationContext);
          return DataResult.success(object);
        });
    return Codecs.exceptionCatching(codec);
  }

  public static <K, V, C extends JsonSerializationContext & JsonDeserializationContext> @NotNull Codec<V> jsonSerializerDispatch(
          final String typeKey,
          Codec<K> keyCodec,
          final Function<? super V, ? extends K> type,
          final Function<? super K, ? extends JsonSerializer<? extends V>> codec,
          C context) {
    return jsonSerializerDispatch(typeKey, keyCodec, type, codec, context, context);
  }

  public static <K, V> @NotNull Codec<V> jsonSerializerDispatch(
      final String typeKey,
      Codec<K> keyCodec,
      final Function<? super V, ? extends K> type,
      final Function<? super K, ? extends JsonSerializer<? extends V>> codec,
      JsonSerializationContext serializationContext,
      JsonDeserializationContext deserializationContext) {
    return ExtraCodecsImpl.jsonSerializerDispatch(
        typeKey, keyCodec, type, codec, serializationContext, deserializationContext);
  }

  @ApiStatus.Experimental
  public static <T> @NotNull JsonSerializer<T> toJsonSerializer(Codec<T> codec) {
    return ExtraCodecsImpl.toJsonSerializer(codec);
  }
}
