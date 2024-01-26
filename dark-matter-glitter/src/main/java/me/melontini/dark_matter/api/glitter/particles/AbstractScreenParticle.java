package me.melontini.dark_matter.api.glitter.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Random;

@Environment(EnvType.CLIENT)
public abstract class AbstractScreenParticle implements Drawable {
    protected static final Random RANDOM = new Random();
    public double x, y, velX, velY;
    public double prevX, prevY;
    public int age = 0, deathAge = 200;
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

    @ApiStatus.Internal
    public final void renderInternal(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (removed || (screen != null && client.currentScreen != screen)) return;
        render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public abstract void render(MatrixStack matrices, int mouseX, int mouseY, float delta);

    @ApiStatus.Internal
    public final void tickInternal() {
        this.prevX = x;
        this.prevY = y;

        tick();
        age++;
        removed = checkRemoval();
        if (screen != null && client.currentScreen != screen) removed = true;
    }

    protected void tick() {
        tickLogic();
    }

    @Deprecated(forRemoval = true)
    protected void tickLogic() {
    }

    protected boolean checkRemoval() {
        return age >= deathAge;
    }

    public void bindToScreen(Screen screen) {
        this.screen = screen;
    }
}
