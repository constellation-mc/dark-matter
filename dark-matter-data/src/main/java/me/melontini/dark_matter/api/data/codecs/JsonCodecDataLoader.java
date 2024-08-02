package me.melontini.dark_matter.api.data.codecs;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.BiConsumer;

public abstract class JsonCodecDataLoader<T> extends JsonDataLoader
    implements IdentifiableResourceReloadListener {

  @Contract("_, _, _ -> new")
  public static <T> @NotNull JsonCodecDataLoader<T> simple(
      Identifier identifier, Codec<T> codec, BiConsumer<Identifier, T> consumer) {
    return new JsonCodecDataLoader<T>(identifier, codec) {
      @Override
      protected void apply(Map<Identifier, T> parsed, ResourceManager manager) {
        parsed.forEach(consumer);
      }
    };
  }

  private final Identifier identifier;
  private final Codec<T> codec;

  public JsonCodecDataLoader(Identifier identifier, Codec<T> codec) {
    super(new Gson(), identifier.toString().replace(':', '/'));
    this.identifier = identifier;
    this.codec = codec;
  }

  @Override
  public final Identifier getFabricId() {
    return this.identifier;
  }

  @Override
  protected void apply(
      Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
    this.apply(
        Maps.transformValues(
            prepared, input -> this.codec.parse(JsonOps.INSTANCE, input).getOrThrow()),
        manager);
  }

  protected abstract void apply(Map<Identifier, T> parsed, ResourceManager manager);
}
