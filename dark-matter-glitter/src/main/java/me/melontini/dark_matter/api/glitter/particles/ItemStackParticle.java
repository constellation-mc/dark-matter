package me.melontini.dark_matter.api.glitter.particles;

import me.melontini.dark_matter.api.base.util.MathUtil;
import me.melontini.dark_matter.api.minecraft.client.util.DrawUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class ItemStackParticle extends AbstractScreenParticle {

    public final ItemStack stack;

    public ItemStackParticle(double x, double y, double velX, double velY, ItemStack stack) {
        super(x, y, velX, velY);
        this.stack = stack;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        float x = (float) MathHelper.lerp(delta, prevX, this.x);
        float y = (float) MathHelper.lerp(delta, prevY, this.y);
        matrices.push();
        matrices.translate(x, y, 500);
        float angle = (float) Math.toDegrees(Math.atan2(velY, velX) * 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(angle));
        BakedModel model = client.getItemRenderer().getModel(stack, null, null, 0);
        DrawUtil.renderGuiItemModelCustomMatrix(matrices, stack, -8, -8, model);
        matrices.pop();
    }

    @Override
    protected void tick() {
        x += velX *= 0.99;
        y += velY * 0.99;
        velY += MathUtil.nextDouble(RANDOM, 0.4, 0.9);
    }

    @Override
    protected boolean checkRemoval() {
        int w = client.getWindow().getScaledWidth();
        int h = client.getWindow().getScaledWidth();
        return ((x > w + 80) || (x < -80)) || ((y > h + 80) || (y < -80));
    }
}
