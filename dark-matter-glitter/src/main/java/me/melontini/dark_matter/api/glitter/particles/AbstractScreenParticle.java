package me.melontini.dark_matter.api.glitter.particles;

import me.melontini.dark_matter.api.base.util.MathStuff;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Random;

@Environment(EnvType.CLIENT)
public abstract class AbstractScreenParticle implements Drawable {
    protected static final Random RANDOM = new Random();
    public double wind = 0.05;
    public double x, y, velX, velY;
    public double prevX, prevY;
    public int age = 0;
    public int deathAge = 200;
    protected MinecraftClient client;
    public boolean removed = false;
    protected Screen screen;

    public AbstractScreenParticle(double x, double y, double velX, double velY) {
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
        this.prevX = x - velX;
        this.prevY = y - velY;
        this.client = MinecraftClient.getInstance();
    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.render(context.getMatrices(), mouseX, mouseY, delta);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

    }

    public final void tick() {
        this.prevX = x;
        this.prevY = y;

        tickLogic();
        age++;
        removed = checkRemoval();
        if (screen != null && client.currentScreen != screen) removed = true;
    }

    protected void tickLogic() {
        x += velX * 0.99;
        y += velY * 0.99;
        velX += wind * MathStuff.nextDouble(RANDOM, -0.5, 1);
    }

    protected boolean checkRemoval() {
        return age >= deathAge;
    }

    public void bindToScreen(Screen screen) {
        this.screen = screen;
    }
}
