package me.melontini.dark_matter.api.mixin;

import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public interface IPluginPlugin {

  default void onPluginLoad(ExtendablePlugin owner, String mixinPackage) {}

  default boolean shouldApplyMixin(
      String targetClassName, String mixinClassName, ClassNode mixinNode) {
    return true;
  }

  default void confirmTargets(Set<String> myTargets, Set<String> otherTargets) {}

  default void getMixins(List<String> mixins) {}

  default void beforeApply(
      String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

  default void afterApply(
      String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
