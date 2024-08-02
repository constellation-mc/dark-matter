package me.melontini.dark_matter.test.data.codecs;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import me.melontini.dark_matter.api.base.util.ColorUtil;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.util.Identifier;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ExtraCodecsUnitTest {

  @ValueSource(
      strings = {
        "[1, 34, 5]",
        "[67, 98, 10]",
        "[255, 255, 255]",
        "[0, 0, 0]",
      })
  @ParameterizedTest
  public void testColorCodecArrays(String value) {
    JsonArray array = JsonParser.parseString(value).getAsJsonArray();
    int intended = ColorUtil.toColor(
        array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt());

    int color = parse(ExtraCodecs.COLOR, array);
    Assertions.assertThat(color).isEqualTo(intended);

    Assertions.assertThat(encode(ExtraCodecs.COLOR, color)).isEqualTo(new JsonPrimitive(intended));
  }

  @ValueSource(
      strings = {
        "[1, 34, -5]",
        "[45678, 98, 10]",
        "[255, 255, 256]",
        "[0, -1, 0]",
      })
  @ParameterizedTest
  public void testColorCodecArraysInvalid(String value) {
    JsonArray array = JsonParser.parseString(value).getAsJsonArray();
    Assertions.assertThatThrownBy(() -> parse(ExtraCodecs.COLOR, array));
  }

  @Test
  public void testMapLookupCodec() {
    BiMap<Identifier, Integer> map = ImmutableBiMap.<Identifier, Integer>builder()
        .put(new Identifier("dark_matter", "one"), 1)
        .put(new Identifier("dark_matter", "two"), 2)
        .put(new Identifier("dark_matter", "three"), 3)
        .put(new Identifier("dark_matter", "four"), 4)
        .build();
    Codec<Integer> codec = ExtraCodecs.mapLookup(Identifier.CODEC, map);

    Assertions.assertThat(parse(codec, new JsonPrimitive("dark_matter:two"))).isEqualTo(2);

    Assertions.assertThat(encode(codec, 3))
        .extracting(JsonElement::getAsString, Assertions.as(InstanceOfAssertFactories.STRING))
        .isEqualTo("dark_matter:three");
  }

  @Test
  public void testEnumCodec() {
    Codec<TestEnum> codec = ExtraCodecs.enumCodec(TestEnum.class);
    Assertions.assertThat(parse(codec, new JsonPrimitive("first"))).isEqualTo(TestEnum.FIRST);

    Assertions.assertThat(encode(codec, TestEnum.SECOND))
        .extracting(JsonElement::getAsString, Assertions.as(InstanceOfAssertFactories.STRING))
        .isEqualTo("second");
  }

  private static <T> T parse(Codec<T> codec, JsonElement element) {
    return codec.parse(JsonOps.INSTANCE, element).getOrThrow();
  }

  private static <T> JsonElement encode(Codec<T> codec, T object) {
    return codec.encodeStart(JsonOps.INSTANCE, object).getOrThrow();
  }

  public enum TestEnum {
    FIRST,
    SECOND,
    SIXTEENTH
  }
}
