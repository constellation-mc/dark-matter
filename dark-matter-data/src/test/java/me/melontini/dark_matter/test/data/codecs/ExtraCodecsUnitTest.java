package me.melontini.dark_matter.test.data.codecs;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import me.melontini.dark_matter.api.base.util.ColorUtil;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class ExtraCodecsUnitTest {

    @Test
    public void testColorCodec() {
        JsonArray color = new JsonArray();
        color.add(23);
        color.add(45);
        color.add(0);

        int colorResult = parse(ExtraCodecs.COLOR, color);
        MakeSure.isTrue(colorResult == ColorUtil.toColor(23, 45, 0));

        JsonPrimitive value = new JsonPrimitive(345);
        colorResult = parse(ExtraCodecs.COLOR, value);
        MakeSure.isTrue(colorResult == 345);

        JsonElement encode = encode(ExtraCodecs.COLOR, ColorUtil.toColor(23, 45, 0));
        MakeSure.isTrue(Objects.equals(new JsonPrimitive(ColorUtil.toColor(23, 45, 0)), encode));
    }

    @Test
    public void testMapLookupCodec() {
        BiMap<Identifier, Integer> map = ImmutableBiMap.<Identifier, Integer>builder()
                .put(Identifier.of("dark_matter", "one"), 1)
                .put(Identifier.of("dark_matter", "two"), 2)
                .put(Identifier.of("dark_matter", "three"), 3)
                .put(Identifier.of("dark_matter", "four"), 4)
                .build();
        Codec<Integer> codec = ExtraCodecs.mapLookup(Identifier.CODEC, map);

        JsonPrimitive identifier = new JsonPrimitive("dark_matter:two");
        int integer = parse(codec, identifier);
        MakeSure.isTrue(integer == 2);

        JsonElement encode = encode(codec, 3);
        MakeSure.isTrue(Objects.equals(encode.getAsString(), "dark_matter:three"));
    }

    @Test
    public void testEnumCodec() {
        Codec<TestEnum> codec = ExtraCodecs.enumCodec(TestEnum.class);
        JsonPrimitive constant = new JsonPrimitive("first");
        TestEnum testEnum = parse(codec, constant);
        MakeSure.isTrue(testEnum == TestEnum.FIRST);

        JsonElement encode = encode(codec, TestEnum.SECOND);
        MakeSure.isTrue(Objects.equals(encode.getAsString(), "second"));
    }

    private static <T> T parse(Codec<T> codec, JsonElement element) {
        return codec.parse(JsonOps.INSTANCE, element).getOrThrow();
    }

    private static <T> JsonElement encode(Codec<T> codec, T object) {
        return codec.encodeStart(JsonOps.INSTANCE, object).getOrThrow();
    }

    public enum TestEnum {
        FIRST, SECOND, SIXTEENTH
    }
}
