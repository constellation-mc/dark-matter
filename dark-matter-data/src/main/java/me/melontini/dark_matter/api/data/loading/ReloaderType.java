package me.melontini.dark_matter.api.data.loading;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.tag.TagManagerLoader;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.function.FunctionLoader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
public final class ReloaderType<T extends ResourceReloader> {

  public static final ReloaderType<TagManagerLoader> TAGS = create(ResourceReloadListenerKeys.TAGS);
  public static final ReloaderType<RecipeManager> RECIPES =
      create(ResourceReloadListenerKeys.RECIPES);
  public static final ReloaderType<ServerAdvancementLoader> ADVANCEMENTS =
      create(ResourceReloadListenerKeys.ADVANCEMENTS);
  public static final ReloaderType<FunctionLoader> FUNCTIONS =
      create(ResourceReloadListenerKeys.FUNCTIONS);

  private final Identifier identifier;

  @Contract("_ -> new")
  public static <T extends ResourceReloader> @NotNull ReloaderType<T> create(
      Identifier identifier) {
    return new ReloaderType<>(identifier);
  }
}
