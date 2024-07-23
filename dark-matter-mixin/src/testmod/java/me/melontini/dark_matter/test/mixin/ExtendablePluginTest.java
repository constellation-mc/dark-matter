package me.melontini.dark_matter.test.mixin;

import java.util.Set;
import lombok.SneakyThrows;
import me.melontini.dark_matter.api.mixin.ExtendablePlugin;
import me.melontini.dark_matter.api.mixin.IPluginPlugin;
import net.fabricmc.loader.api.Version;
import org.assertj.core.api.Assertions;

public class ExtendablePluginTest extends ExtendablePlugin {

  @Override
  protected void collectPlugins(Set<IPluginPlugin> plugins) {
    plugins.add(DefaultPlugins.publicizePlugin());
    plugins.add(DefaultPlugins.asmTransformerPlugin());
  }

  @SneakyThrows
  @Override
  protected void onPluginLoad(String mixinPackage) {
    Assertions.assertThat(Version.parse("1.3.5"))
        .matches(version -> versionMatches(version, asPredicate(">=1.3.5")));
    Assertions.assertThat(Version.parse("1.3.5"))
        .matches(version -> !versionMatches(version, asPredicate(">1.3.5")));

    Assertions.assertThat(getModVersion("dark-matter-base"))
        .isPresent()
        .get()
        .matches(version -> versionMatches(version, asPredicate(">1.0.0")));
  }
}
