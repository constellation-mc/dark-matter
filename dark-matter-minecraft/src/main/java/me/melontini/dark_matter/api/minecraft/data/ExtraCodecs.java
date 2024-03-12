package me.melontini.dark_matter.api.minecraft.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.ColorUtil;
import net.minecraft.util.collection.WeightedList;

import java.util.List;
import java.util.function.Function;

/**
 * Will move to a different module in 4.0.0
 */
@UtilityClass
public class ExtraCodecs {

    public static final Codec<Integer> COLOR = Codec.either(Codec.INT, Codec.intRange(0, 255).listOf())
            .comapFlatMap(e -> e.map(DataResult::success, integers -> {
                if (integers.size() != 3) return DataResult.error("colors array must contain exactly 3 colors (RGB)");
                return DataResult.success(ColorUtil.toColor(integers.get(0), integers.get(1), integers.get(2)));
            }), Either::left);

    public static <T> Codec<List<T>> list(Codec<T> codec) {
        return Codec.either(codec, codec.listOf()).xmap(e -> e.map(ImmutableList::of, Function.identity()), Either::right);
    }

    public static <T> Codec<WeightedList<T>> weightedList(Codec<T> codec) {
        return Codec.either(codec, WeightedList.createCodec(codec)).xmap(e -> e.map(entry -> {
            WeightedList<T> list = new WeightedList<>();
            list.add(entry, 1);
            return list;
        }, Function.identity()), Either::right);
    }
}
