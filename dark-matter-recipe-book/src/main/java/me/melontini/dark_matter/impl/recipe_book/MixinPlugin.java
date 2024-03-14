package me.melontini.dark_matter.impl.recipe_book;

import me.melontini.dark_matter.api.mixin.ExtendablePlugin;
import me.melontini.dark_matter.api.mixin.IPluginPlugin;

import java.util.Set;

public class MixinPlugin extends ExtendablePlugin {

    @Override
    protected void collectPlugins(Set<IPluginPlugin> plugins) {
        plugins.add(DefaultPlugins.asmTransformerPlugin());
    }
}
