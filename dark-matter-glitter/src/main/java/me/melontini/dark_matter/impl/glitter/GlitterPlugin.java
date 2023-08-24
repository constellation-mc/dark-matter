package me.melontini.dark_matter.impl.glitter;

import me.melontini.dark_matter.api.base.util.mixin.ExtendablePlugin;
import me.melontini.dark_matter.api.base.util.mixin.IPluginPlugin;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.Set;

public class GlitterPlugin extends ExtendablePlugin {

    @Override
    protected void collectPlugins(Set<IPluginPlugin> plugins) {
        plugins.clear();
    }

    @Override
    protected void beforeApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (!mixinClassName.endsWith("VanillaParticleMixin") || !isConnector()) return;

        MethodNode getParticleId = targetClass.methods.stream().filter(m -> m.name.equals("getParticleId")).findFirst().orElseThrow();
        MethodNode createScreenParticle = targetClass.methods.stream().filter(m -> m.name.equals("createScreenParticle")).findFirst().orElseThrow();

        boolean modified = false;
        for (AbstractInsnNode instruction : createScreenParticle.instructions) {
            if (instruction instanceof FieldInsnNode f) {
                if ("Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;".equals(f.desc)) {
                    f.desc = "Ljava/util/Map;";
                    modified = true;
                    continue;
                }
            }

            if (instruction instanceof MethodInsnNode m) {
                if ("it/unimi/dsi/fastutil/ints/Int2ObjectMap".equals(m.owner) && "get".equals(m.name)) {
                    m.owner = "java/util/Map";
                    m.desc = "(Ljava/lang/Object;)Ljava/lang/Object;";

                    MethodInsnNode m2 = new MethodInsnNode(Opcodes.INVOKESTATIC, targetClassName.replace('.', '/'), getParticleId.name, getParticleId.desc, false);
                    createScreenParticle.instructions.insertBefore(m, m2);
                    modified = true;
                }
            }
        }
        if (modified) DarkMatterLog.info("Ran fixup for VanillaParticle!");
    }

    private static boolean isConnector() {
        if (FabricLoader.getInstance().isModLoaded("connectormod")) {
            try {
                Class.forName("dev.su5ed.sinytra.connector.mod.ConnectorLoader");
                return true;
            } catch (ClassNotFoundException ignored) {}
        }
        return false;
    }

}
