package me.melontini.dark_matter.test.mixin;

import java.util.Set;
import lombok.SneakyThrows;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.mixin.ExtendablePlugin;
import me.melontini.dark_matter.api.mixin.IPluginPlugin;
import net.fabricmc.loader.api.Version;

public class ExtendablePluginTest extends ExtendablePlugin {

  @Override
  protected void collectPlugins(Set<IPluginPlugin> plugins) {
    plugins.add(DefaultPlugins.publicizePlugin());
    plugins.add(DefaultPlugins.asmTransformerPlugin());
  }

  @SneakyThrows
  @Override
  protected void onPluginLoad(String mixinPackage) {
    MakeSure.isTrue(versionMatches(Version.parse("1.3.5"), asPredicate(">=1.3.5")));
    MakeSure.isTrue(!versionMatches(Version.parse("1.3.5"), asPredicate(">1.3.5")));

    MakeSure.isTrue(
        versionMatches(getModVersion("dark-matter-base").orElseThrow(), asPredicate(">1.0.0")));
  }
}
