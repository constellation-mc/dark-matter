package me.melontini.dark_matter.api.base.util.mixin;

import me.melontini.dark_matter.impl.base.util.mixin.PublicizePlugin;
import me.melontini.dark_matter.impl.base.util.mixin.ShouldApplyPlugin;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

//TODO support custom processors. (plugin-plugins?)
@ApiStatus.Experimental
public class ExtendablePlugin implements IMixinConfigPlugin {

    private final Set<IPluginPlugin> plugins = new HashSet<>();

    @Override
    public final void onLoad(String mixinPackage) {
        this.plugins.add(DefaultPlugins.shouldApplyPlugin());
        this.collectPlugins(this.plugins);

        this.plugins.forEach(plugin -> plugin.onPluginLoad(mixinPackage));
        this.onPluginLoad(mixinPackage);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public final boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        AtomicBoolean apply = new AtomicBoolean(true);
        try {
            ClassNode node = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName);
            List<AnnotationNode> annotationNodes = new ArrayList<>();

            if (node.invisibleAnnotations != null) annotationNodes.addAll(node.invisibleAnnotations);
            if (node.visibleAnnotations != null) annotationNodes.addAll(node.visibleAnnotations);

            for (IPluginPlugin plugin : this.plugins) {
                apply.set(plugin.shouldApplyMixin(targetClassName, mixinClassName, node, annotationNodes));
                if (!apply.get()) break;
            }

            if (apply.get()) apply.set(this.shouldApplyMixin(targetClassName, mixinClassName, node, annotationNodes));
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return apply.get();
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
    public final void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        this.plugins.forEach(plugin -> plugin.beforeApply(targetClassName, targetClass, mixinClassName, mixinInfo));
        this.beforeApply(targetClassName, targetClass, mixinClassName, mixinInfo);
    }

    @Override
    public final void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        this.plugins.forEach(plugin -> plugin.afterApply(targetClassName, targetClass, mixinClassName, mixinInfo));
        this.afterApply(targetClassName, targetClass, mixinClassName, mixinInfo);
    }

    //New Methods.

    protected void collectPlugins(Set<IPluginPlugin> plugins) {

    }

    protected void onPluginLoad(String mixinPackage) {

    }

    protected boolean shouldApplyMixin(String targetClassName, String mixinClassName, ClassNode mixinNode, List<AnnotationNode> mergedAnnotations) {
        return true;
    }

    protected void confirmTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    protected void getMixins(List<String> mixins) {

    }

    protected void beforeApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    protected void afterApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    //Utility Methods.

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
        return FabricLoader.getInstance().getModContainer(modId).map(m -> m.getMetadata().getVersion());
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

        public static IPluginPlugin shouldApplyPlugin() {
            return new ShouldApplyPlugin();
        }

        public static IPluginPlugin publicizePlugin() {
            return new PublicizePlugin();
        }

    }

}
