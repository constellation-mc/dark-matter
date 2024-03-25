package me.melontini.dark_matter.api.data.codecs;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.ColorUtil;
import me.melontini.dark_matter.impl.data.codecs.SafeOptionalCodec;
import net.minecraft.util.collection.WeightedList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@UtilityClass
public class ExtraCodecs {

    public static final Codec<Integer> COLOR = Codec.either(Codec.INT, Codec.intRange(0, 255).listOf())
            .comapFlatMap(e -> e.map(DataResult::success, integers -> {
                if (integers.size() != 3) return DataResult.error(() -> "colors array must contain exactly 3 colors (RGB)");
                return DataResult.success(ColorUtil.toColor(integers.get(0), integers.get(1), integers.get(2)));
            }), Either::left);

    /**
     * Unlike the vanilla alternative, ({@link Codec#optionalField(String, Codec)}) this codec does not ignore exceptions.
     */
    public static <F> MapCodec<F> optional(final String name, final Codec<F> elementCodec, F defaultValue) {
        return optional(name, elementCodec).xmap(f -> f.orElse(defaultValue), f -> Objects.equals(f, defaultValue) ? Optional.empty() : Optional.of(f));
    }

    /**
     * Unlike the vanilla alternative, ({@link Codec#optionalField(String, Codec)}) this codec does not ignore exceptions.
     */
    @Contract("_, _ -> new")
    public static <F> @NotNull MapCodec<Optional<F>> optional(final String name, final Codec<F> elementCodec) {
        return new SafeOptionalCodec<>(name, elementCodec);
    }

    /**
     * A list codec which accepts both lists and singular entries.
     */
    public static <T> Codec<List<T>> list(Codec<T> codec) {
        return Codec.either(codec, codec.listOf()).xmap(e -> e.map(ImmutableList::of, Function.identity()), Either::right);
    }

    /**
     * A weighted list codec which accepts both lists and singular entries.
     */
    public static <T> Codec<WeightedList<T>> weightedList(Codec<T> codec) {
        return Codec.either(codec, WeightedList.createCodec(codec)).xmap(e -> e.map(entry -> {
            WeightedList<T> list = new WeightedList<>();
            list.add(entry, 1);
            return list;
        }, Function.identity()), Either::right);
    }
}
