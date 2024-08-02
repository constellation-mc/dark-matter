package me.melontini.dark_matter.impl.enums;

import java.util.Set;
import me.melontini.dark_matter.api.mixin.ExtendablePlugin;
import me.melontini.dark_matter.api.mixin.IPluginPlugin;

public class DarkMatterEnumsPlugin extends ExtendablePlugin {

  @Override
  protected void collectPlugins(Set<IPluginPlugin> plugins) {
    plugins.add(DefaultPlugins.asmTransformerPlugin());
    plugins.add(DefaultPlugins.publicizePlugin());
  }
}
