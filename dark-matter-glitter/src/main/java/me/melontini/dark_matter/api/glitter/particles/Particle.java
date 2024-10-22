package me.melontini.dark_matter.api.glitter.particles;

import static me.melontini.dark_matter.api.minecraft.client.util.DrawUtil.fillGradient;

import me.melontini.dark_matter.api.base.util.MathUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class Particle extends AbstractScreenParticle {
  public final int color;
  public double wind = 0.05;

  public Particle(double x, double y, double velX, double velY, int color) {
    super(x, y, velX, velY);
    this.color = color;
    this.deathAge += MathUtil.threadRandom().nextInt(120);
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    MatrixStack matrices = context.getMatrices();
    matrices.push();
    float x = (float) MathHelper.lerp(delta, prevX, this.x);
    float y = (float) MathHelper.lerp(delta, prevY, this.y);
    fillGradient(matrices, x, y, x + 3, y + 3, 500, color, color);
    matrices.pop();
  }

  @Override
  protected void tick() {
    x += velX;
    y += velY;
    velX += wind * MathUtil.nextDouble(-0.5, 1);
  }
}
