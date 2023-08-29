package me.melontini.dark_matter.impl.glitter.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.melontini.dark_matter.api.glitter.particles.AbstractScreenParticle;
import me.melontini.dark_matter.api.mirage.Mirage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

/**
 * Render vanilla particle types on screen!
 * <p>
 * Inspired by the removed {@code gesundheit} module of <a href="https://git.sleeping.town/unascribed-mods/Lib39">Lib39</a>
 */
@Environment(EnvType.CLIENT)
public class VanillaParticle extends AbstractScreenParticle {

    private static final Camera CAMERA = new Camera();
    private final Particle particle;

    public VanillaParticle(ParticleEffect parameters, double x, double y, double velX, double velY, double velZ) {
        super(0, 0, 0, 0);

        this.particle = createScreenParticle(parameters, x, y, velX, velY, velZ);
        if (this.particle != null) {
            this.particle.collidesWithWorld = false;
        } else {
            this.removed = true;
        }
    }

    public VanillaParticle(ParticleEffect parameters, double x, double y, double velX, double velY) {
        this(parameters, x, y, velX, velY, 0);
    }

    public VanillaParticle(Particle particle) {
        super(0, 0, 0, 0);

        this.particle = particle;
        if (this.particle != null) {
            this.particle.collidesWithWorld = false;
        } else {
            this.removed = true;
        }
    }

    @Override
    protected void tick() {
        particle.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();

        matrices.push();
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(0, 0, 500);
        matrixStack.scale(24, 24, 1);
        matrixStack.translate(0, client.getWindow().getScaledHeight() / 24f, 0);
        matrixStack.scale(1, -1, 1);
        matrixStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
        RenderSystem.applyModelViewMatrix();

        Mirage.ALWAYS_BRIGHT_LTM.enable();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        RenderSystem.setShader(GameRenderer::getParticleProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        particle.getType().begin(bufferBuilder, client.getTextureManager());

        try {
            particle.buildGeometry(bufferBuilder, CAMERA, client.getTickDelta());
        } catch (Throwable var17) {
            CrashReport crashReport = CrashReport.create(var17, "[Dark Matter Glitter] Rendering Particle On Screen");
            CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered on screen");
            crashReportSection.add("Particle", particle::toString);
            crashReportSection.add("Particle Type", particle.getType()::toString);
            throw new CrashException(crashReport);
        }

        particle.getType().draw(tessellator);

        Mirage.ALWAYS_BRIGHT_LTM.disable();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        matrices.pop();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    @Override
    protected boolean checkRemoval() {
        return !particle.isAlive();
    }

    public static <T extends ParticleEffect> Particle createScreenParticle(T parameters, double x, double y, double velocityX, double velocityY, double velocityZ) {
        return Optional.ofNullable(getFactory(parameters))
                .map(factory -> factory.createParticle(parameters, Mirage.FAKE_WORLD, x / 24, (MinecraftClient.getInstance().getWindow().getScaledHeight() - y) / 24, 0, velocityX, velocityY, velocityZ))
                .orElseThrow(() -> new NullPointerException("Particle type has no factory!"));
    }

    private static <T extends ParticleEffect> ParticleFactory<T> getFactory(T particleType) {
        if (!typeChangedByForge) {
            return (ParticleFactory<T>) MinecraftClient.getInstance().particleManager.factories.get(Registries.PARTICLE_TYPE.getRawId(particleType.getType()));
        } else {
            try {
                return ((Map<Identifier, ParticleFactory<T>>) factoriesField.get(MinecraftClient.getInstance().particleManager)).get(Registries.PARTICLE_TYPE.getId(particleType.getType()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static boolean typeChangedByForge = false;
    private static Field factoriesField = null;

    static {
        MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();
        String forge = resolver.mapFieldName("intermediary", "net.minecraft.class_702", "field_3835", "Ljava/util/Map;");
        Field field = null;
        for (Field field1 : MinecraftClient.getInstance().particleManager.getClass().getFields()) {
            if (field1.getName().equals(forge)) {
                field = field1;
                break;
            }
        }

        if (field != null && field.getType() != Int2ObjectMap.class) {
            typeChangedByForge = true;
            factoriesField = field;
            factoriesField.setAccessible(true);
        }
    }
}
