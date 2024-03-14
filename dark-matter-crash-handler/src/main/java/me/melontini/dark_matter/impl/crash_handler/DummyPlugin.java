package me.melontini.dark_matter.impl.crash_handler;

import me.melontini.dark_matter.impl.base.DarkMatterLog;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class DummyPlugin implements IMixinConfigPlugin {

    static {
        try {
            Mixins.registerErrorHandlerClass(MixinErrorHandler.class.getName());
        } catch (Throwable e) {
            DarkMatterLog.error("Failed to register mixin error handler!", e);
        }
    }

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() {return null;}
    @Override public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {return false;}
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() {return null;}
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
