package me.melontini.dark_matter.test.data.codecs;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.ModInitializer;

public class ExtraCodecsTest implements ModInitializer {
  @Override
  public void onInitialize() {}

  private static <T> T parse(Codec<T> codec, JsonElement element) {
    return codec.parse(JsonOps.INSTANCE, element).getOrThrow();
  }

  private static <T> JsonElement encode(Codec<T> codec, T object) {
    return codec.encodeStart(JsonOps.INSTANCE, object).getOrThrow();
  }
}
