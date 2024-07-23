package me.melontini.dark_matter.impl.recipe_book;

import java.util.Set;
import me.melontini.dark_matter.api.mixin.ExtendablePlugin;
import me.melontini.dark_matter.api.mixin.IPluginPlugin;

public class MixinPlugin extends ExtendablePlugin {

  @Override
  protected void collectPlugins(Set<IPluginPlugin> plugins) {
    plugins.add(DefaultPlugins.asmTransformerPlugin());
  }
}
