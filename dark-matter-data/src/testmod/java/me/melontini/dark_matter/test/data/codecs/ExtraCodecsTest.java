package me.melontini.dark_matter.test.data.codecs;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.lang.reflect.Type;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.fabricmc.api.ModInitializer;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.registry.Registries;
import net.minecraft.util.JsonSerializableType;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;

public class ExtraCodecsTest implements ModInitializer {
  @Override
  public void onInitialize() {
    testDispatchSerializer();
  }

  private static void testDispatchSerializer() {
    Codec<LootCondition> codec = ExtraCodecs.jsonSerializerDispatch(
        "condition",
        Registries.LOOT_CONDITION_TYPE.getCodec(),
        LootCondition::getType,
        JsonSerializableType::getJsonSerializer,
        new GsonContextImpl(LootGsons.getConditionGsonBuilder().create()));
    JsonElement element = JsonParser.parseString(
        """
                      {
                        "condition": "minecraft:random_chance",
                        "chance": 0.5
                      }""");
    LootCondition condition = parse(codec, element);
    Assertions.assertThat(parse(codec, element))
        .isNotNull()
        .isInstanceOf(RandomChanceLootCondition.class)
        .asInstanceOf(InstanceOfAssertFactories.type(LootCondition.class))
        .extracting(lc -> encode(codec, condition))
        .isEqualTo(element);
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

  public static final class GsonContextImpl
      implements JsonSerializationContext, JsonDeserializationContext {

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
