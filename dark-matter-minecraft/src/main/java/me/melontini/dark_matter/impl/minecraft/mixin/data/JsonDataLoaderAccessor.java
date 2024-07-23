package me.melontini.dark_matter.impl.minecraft.mixin.data;

import com.google.gson.JsonElement;
import java.util.Map;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(JsonDataLoader.class)
public interface JsonDataLoaderAccessor {

  @Invoker("prepare")
  Map<Identifier, JsonElement> dark_matter$prepare(
      ResourceManager resourceManager, Profiler profiler);
}
