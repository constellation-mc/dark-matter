package me.melontini.dark_matter.api.data.loading;

import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.loot.LootManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.tag.TagManagerLoader;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.function.FunctionLoader;
import net.minecraft.util.Identifier;

public record ReloaderType<T extends ResourceReloader>(Identifier identifier) {

    public static final ReloaderType<TagManagerLoader> TAGS = create(ResourceReloadListenerKeys.TAGS);
    public static final ReloaderType<RecipeManager> RECIPES = create(ResourceReloadListenerKeys.RECIPES);
    public static final ReloaderType<ServerAdvancementLoader> ADVANCEMENTS = create(ResourceReloadListenerKeys.ADVANCEMENTS);
    public static final ReloaderType<FunctionLoader> FUNCTIONS = create(ResourceReloadListenerKeys.FUNCTIONS);
    public static final ReloaderType<LootManager> LOOT_TABLES = create(ResourceReloadListenerKeys.LOOT_TABLES);

    public static <T extends ResourceReloader> ReloaderType<T> create(Identifier identifier) {
        return new ReloaderType<>(identifier);
    }
}
