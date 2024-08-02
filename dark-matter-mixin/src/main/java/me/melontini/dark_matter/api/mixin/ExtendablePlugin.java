package me.melontini.dark_matter.api.mixin;

import java.util.*;
import lombok.Getter;
import me.melontini.dark_matter.impl.mixin.AsmTransformerPlugin;
import me.melontini.dark_matter.impl.mixin.ConstructDummyPlugin;
import me.melontini.dark_matter.impl.mixin.MixinPredicatePlugin;
import me.melontini.dark_matter.impl.mixin.PublicizePlugin;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

public class ExtendablePlugin implements IMixinConfigPlugin {

  private final Set<IPluginPlugin> plugins;

  /**
   * -- GETTER --
   *  The package is set at the start of {@code onPluginLoad}
   * , so it's not available earlier. (collectPlugins, ctx, clinit)
   *
   */
  @Getter
  private String mixinPackage;

  public ExtendablePlugin() {
    Set<IPluginPlugin> plugins = new LinkedHashSet<>();
    plugins.add(DefaultPlugins.mixinPredicatePlugin());
    this.collectPlugins(plugins);

    this.plugins = Collections.unmodifiableSet(plugins);
  }

  @Override
  public final void onLoad(String mixinPackage) {
    this.mixinPackage = mixinPackage;
    this.plugins.forEach(plugin -> plugin.onPluginLoad(this, mixinPackage));
    this.onPluginLoad(mixinPackage);
  }

  @Override
  public String getRefMapperConfig() {
    return null;
  }

  @Override
  public final boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
    try {
      ClassNode node = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName);

      for (IPluginPlugin plugin : this.plugins) {
        if (!plugin.shouldApplyMixin(targetClassName, mixinClassName, node)) return false;
      }

      return this.shouldApplyMixin(targetClassName, mixinClassName, node);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public final void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    this.plugins.forEach(plugin -> plugin.confirmTargets(myTargets, otherTargets));
    this.confirmTargets(myTargets, otherTargets);
  }

  @Override
  public final List<String> getMixins() {
    List<String> mixins = new ArrayList<>();
    this.plugins.forEach(plugin -> plugin.getMixins(mixins));
    this.getMixins(mixins);
    return mixins.isEmpty() ? null : mixins;
  }

  @Override
  public final void preApply(
      String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    this.plugins.forEach(
        plugin -> plugin.beforeApply(targetClassName, targetClass, mixinClassName, mixinInfo));
    this.beforeApply(targetClassName, targetClass, mixinClassName, mixinInfo);
  }

  @Override
  public final void postApply(
      String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    this.plugins.forEach(
        plugin -> plugin.afterApply(targetClassName, targetClass, mixinClassName, mixinInfo));
    this.afterApply(targetClassName, targetClass, mixinClassName, mixinInfo);
  }

  // New Methods.

  @ApiStatus.OverrideOnly
  protected void collectPlugins(Set<IPluginPlugin> plugins) {}

  @ApiStatus.OverrideOnly
  protected void onPluginLoad(String mixinPackage) {}

  @ApiStatus.OverrideOnly
  protected boolean shouldApplyMixin(
      String targetClassName, String mixinClassName, ClassNode mixinNode) {
    return true;
  }

  @ApiStatus.OverrideOnly
  protected void confirmTargets(Set<String> myTargets, Set<String> otherTargets) {}

  @ApiStatus.OverrideOnly
  protected void getMixins(List<String> mixins) {}

  @ApiStatus.OverrideOnly
  protected void beforeApply(
      String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

  @ApiStatus.OverrideOnly
  protected void afterApply(
      String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

  // Utility Methods.

  protected boolean isDev() {
    return FabricLoader.getInstance().isDevelopmentEnvironment();
  }

  protected EnvType getEnv() {
    return FabricLoader.getInstance().getEnvironmentType();
  }

  protected boolean isClient() {
    return this.getEnv() == EnvType.CLIENT;
  }

  protected Optional<Version> getModVersion(String modId) {
    return FabricLoader.getInstance().getModContainer(modId).map(m -> m.getMetadata()
        .getVersion());
  }

  protected VersionPredicate asPredicate(String version) {
    try {
      return VersionPredicate.parse(version);
    } catch (VersionParsingException e) {
      throw new RuntimeException(e);
    }
  }

  protected boolean versionMatches(Version version, VersionPredicate predicate) {
    return predicate.test(version);
  }

  protected MappingResolver getMappingResolver() {
    return FabricLoader.getInstance().getMappingResolver();
  }

  public static final class DefaultPlugins {

    public static IPluginPlugin mixinPredicatePlugin() {
      return new MixinPredicatePlugin();
    }

    public static IPluginPlugin publicizePlugin() {
      return new PublicizePlugin();
    }

    public static IPluginPlugin asmTransformerPlugin() {
      return new AsmTransformerPlugin();
    }

    /**
     * See {@link me.melontini.dark_matter.api.mixin.annotations.ConstructDummy}
     */
    @Deprecated
    @ApiStatus.Experimental
    public static IPluginPlugin constructDummyPlugin() {
      return new ConstructDummyPlugin();
    }
  }
}
