package me.melontini.dark_matter.test.data.codecs;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import me.melontini.dark_matter.api.base.util.ColorUtil;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.fabricmc.api.ModInitializer;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializableType;

import java.lang.reflect.Type;

public class ExtraCodecsTest implements ModInitializer {
    @Override
    public void onInitialize() {
        testColorCodec();
        testMapLookupCodec();
        testEnumCodec();
        testDispatchSerializer();
    }

    private static void testColorCodec() {
        JsonArray color = new JsonArray();
        color.add(23);
        color.add(45);
        color.add(0);

        int colorResult = parse(ExtraCodecs.COLOR, color);
        MakeSure.isTrue(colorResult == ColorUtil.toColor(23, 45, 0));

        JsonPrimitive value = new JsonPrimitive(345);
        colorResult = parse(ExtraCodecs.COLOR, value);
        MakeSure.isTrue(colorResult == 345);
    }

    private static void testMapLookupCodec() {
        BiMap<Identifier, Integer> map = ImmutableBiMap.<Identifier, Integer>builder()
                .put(new Identifier("dark_matter", "one"), 1)
                .put(new Identifier("dark_matter", "two"), 2)
                .put(new Identifier("dark_matter", "three"), 3)
                .put(new Identifier("dark_matter", "four"), 4)
                .build();
        Codec<Integer> codec = ExtraCodecs.mapLookup(Identifier.CODEC, map);

        JsonPrimitive identifier = new JsonPrimitive("dark_matter:two");
        int integer = parse(codec, identifier);
        MakeSure.isTrue(integer == 2);
    }

    private static void testEnumCodec() {
        Codec<TestEnum> codec = ExtraCodecs.enumCodec(TestEnum.class);
        JsonPrimitive constant = new JsonPrimitive("first");
        TestEnum testEnum = parse(codec, constant);
        MakeSure.isTrue(testEnum == TestEnum.FIRST);
    }

    private static void testDispatchSerializer() {
        Codec<LootCondition> codec = ExtraCodecs.jsonSerializerDispatch("condition", Registries.LOOT_CONDITION_TYPE.getCodec(), LootCondition::getType, JsonSerializableType::getJsonSerializer, new GsonContextImpl(LootGsons.getConditionGsonBuilder().create()));
        JsonElement element = JsonParser.parseString("""
                {
                        "condition": "minecraft:random_chance",
                        "chance": 0.5
                      }""");
        LootCondition condition = parse(codec, element);
        MakeSure.isTrue(condition instanceof RandomChanceLootCondition);
    }

    private static <T> T parse(Codec<T> codec, JsonElement element) {
        return codec.parse(JsonOps.INSTANCE, element).getOrThrow(false, string -> {
            throw new JsonParseException(string);
        });
    }

    public enum TestEnum {
        FIRST, SECOND, SIXTEENTH
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
