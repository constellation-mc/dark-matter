package me.melontini.dark_matter.api.glitter;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.glitter.particles.AbstractScreenParticle;
import me.melontini.dark_matter.impl.glitter.ScreenParticleInternals;
import me.melontini.dark_matter.impl.glitter.particles.VanillaParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleEffect;

import java.util.List;
import java.util.function.Supplier;

import static me.melontini.dark_matter.impl.glitter.ScreenParticleInternals.current;


@UtilityClass
@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public final class ScreenParticleHelper {

    public static void addParticle(AbstractScreenParticle particle) {
        ScreenParticleInternals.addScreenParticle(null, particle);
    }

    public static void addParticle(ParticleEffect parameters, double x, double y, double velX, double velY) {
        ScreenParticleInternals.addScreenParticle(null, parameters, x, y, velX, velY);
    }

    public static void addParticle(ParticleEffect parameters, double x, double y, double velX, double velY, double velZ) {
        ScreenParticleInternals.addScreenParticle(null, parameters, x, y, velX, velY, velZ);
    }

    public static void addParticles(AbstractScreenParticle... particle) {
        ScreenParticleInternals.addScreenParticles(null, particle);
    }

    public static void addParticles(List<AbstractScreenParticle> particle) {
        ScreenParticleInternals.addScreenParticles(null, particle);
    }

    public static void addParticles(Supplier<AbstractScreenParticle> particle, int count) {
        ScreenParticleInternals.addScreenParticles(null, particle, count);
    }

    public static void addParticles(ParticleEffect parameters, double x, double y, double deltaX, double deltaY, double speed, int count) {
        ScreenParticleInternals.addScreenParticles(null, parameters, x, y, deltaX, deltaY, speed, count);
    }

    /////////////////////////////

    public static void addScreenParticle(AbstractScreenParticle particle) {
        ScreenParticleInternals.addScreenParticle(current(), particle);
    }

    public static void addScreenParticle(ParticleEffect parameters, double x, double y, double velX, double velY) {
        ScreenParticleInternals.addScreenParticle(current(), parameters, x, y, velX, velY);
    }

    public static void addScreenParticle(ParticleEffect parameters, double x, double y, double velX, double velY, double velZ) {
        ScreenParticleInternals.addScreenParticle(current(), parameters, x, y, velX, velY, velZ);
    }

    public static void addScreenParticles(AbstractScreenParticle... particles) {
        ScreenParticleInternals.addScreenParticles(current(), particles);
    }

    public static void addScreenParticles(List<AbstractScreenParticle> particles) {
        ScreenParticleInternals.addScreenParticles(current(), particles);
    }

    public static void addScreenParticles(Supplier<AbstractScreenParticle> particle, int count) {
        ScreenParticleInternals.addScreenParticles(current(), particle, count);
    }

    public static void addScreenParticles(ParticleEffect parameters, double x, double y, double deltaX, double deltaY, double speed, int count) {
        ScreenParticleInternals.addScreenParticles(current(), parameters, x, y, deltaX, deltaY, speed, count);
    }

    /////////////////////////////

    public static void addScreenParticle(Screen screen, AbstractScreenParticle particle) {
        ScreenParticleInternals.addScreenParticle(screen, particle);
    }

    public static void addScreenParticle(Screen screen, ParticleEffect parameters, double x, double y, double velX, double velY) {
        ScreenParticleInternals.addScreenParticle(screen, parameters, x, y, velX, velY);
    }

    public static void addScreenParticle(Screen screen, ParticleEffect parameters, double x, double y, double velX, double velY, double velZ) {
        ScreenParticleInternals.addScreenParticle(screen, parameters, x, y, velX, velY, velZ);
    }

    public static void addScreenParticles(Screen screen, AbstractScreenParticle... particles) {
        ScreenParticleInternals.addScreenParticles(screen, particles);
    }

    public static void addScreenParticles(Screen screen, List<AbstractScreenParticle> particles) {
        ScreenParticleInternals.addScreenParticles(screen, particles);
    }

    public static void addScreenParticles(Screen screen, Supplier<AbstractScreenParticle> particle, int count) {
        ScreenParticleInternals.addScreenParticles(screen, particle, count);
    }

    public static void addScreenParticles(Screen screen, ParticleEffect parameters, double x, double y, double deltaX, double deltaY, double speed, int count) {
        ScreenParticleInternals.addScreenParticles(screen, parameters, x, y, deltaX, deltaY, speed, count);
    }

    /////////////////////////////

    public static AbstractScreenParticle ofVanilla(Particle particle) {
        return new VanillaParticle(particle);
    }

    public static Supplier<AbstractScreenParticle> ofVanilla(Supplier<Particle> supplier) {
        return () -> new VanillaParticle(supplier.get());
    }
}
