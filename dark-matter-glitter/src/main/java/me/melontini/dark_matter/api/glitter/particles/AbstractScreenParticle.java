package me.melontini.dark_matter.api.glitter.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.ApiStatus;

@Environment(EnvType.CLIENT)
public abstract class AbstractScreenParticle implements Drawable {
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

    @Override
    @ApiStatus.OverrideOnly
    public abstract void render(DrawContext context, int mouseX, int mouseY, float delta);

    @ApiStatus.OverrideOnly
    protected abstract void tick();

    @ApiStatus.OverrideOnly
    protected boolean checkRemoval() {
        return age >= deathAge;
    }

    public void bindToScreen(Screen screen) {
        this.screen = screen;
    }

    @ApiStatus.Internal
    public final void renderInternal(DrawContext context, int mouseX, int mouseY, float delta) {
        if (removed || (screen != null && client.currentScreen != screen)) return;
        render(context, mouseX, mouseY, delta);
    }

    @ApiStatus.Internal
    public final void tickInternal() {
        this.prevX = x;
        this.prevY = y;

        tick();
        age++;
        this.removed = checkRemoval();
        if (screen != null && client.currentScreen != screen) removed = true;
    }
}
