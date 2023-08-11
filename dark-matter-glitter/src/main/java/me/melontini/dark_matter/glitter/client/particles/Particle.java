package me.melontini.dark_matter.glitter.client.particles;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import static me.melontini.dark_matter.api.minecraft.client.util.DrawUtil.fillGradient;

public class Particle extends AbstractScreenParticle {
    public final int color;

    public Particle(double x, double y, double velX, double velY, int color) {
        super(x, y, velX, velY);
        this.color = color;
        this.deathAge += RANDOM.nextInt(120);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        matrices.push();
        float x = (float) MathHelper.lerp(delta, prevX, this.x);
        float y = (float) MathHelper.lerp(delta, prevY, this.y);
        fillGradient(matrices, x, y, x + 3, y + 3, 500, color, color);
        matrices.pop();
    }
}
