package me.melontini.dark_matter.api.minecraft.client.util;

import me.melontini.dark_matter.impl.minecraft.client.util.DrawInternals;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

import java.util.List;
import java.util.Optional;

/**
 * This class provides a set of utility methods for drawing on screen.
 *
 * <p>It mirrors some of the methods from the Screen and DrawableHelper classes
 * to allow using them without creating an instance of a screen. Most int values
 * in those methods have been replaced by floats to allow for more flexibility.</p>
 */
@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class DrawUtil {
    public static final Screen FAKE_SCREEN = DrawInternals.FAKE_SCREEN;

    public static void renderTooltip(MatrixStack matrices, ItemStack stack, float x, float y) {
        DrawInternals.renderTooltip(matrices, stack, x, y);
    }

    public static void renderTooltip(MatrixStack matrices, Text text, float x, float y) {
        DrawInternals.renderTooltip(matrices, text, x, y);
    }

    public static void renderTooltip(MatrixStack matrices, List<Text> lines, float x, float y) {
        DrawInternals.renderTooltip(matrices, lines, x, y);
    }

    public static void renderTooltip(MatrixStack matrices, List<Text> lines, Optional<TooltipData> data, float x, float y) {
        DrawInternals.renderTooltip(matrices, lines, data, x, y);
    }

    public static void renderOrderedTooltip(MatrixStack matrices, List<? extends OrderedText> lines, float x, float y) {
        DrawInternals.renderOrderedTooltip(matrices, lines, x, y);
    }

    public static void renderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, float x, float y) {
        DrawInternals.renderTooltipFromComponents(matrices, components, x, y);
    }

    public static void fillGradient(MatrixStack matrices, float startX, float startY, float endX, float endY, float z, int colorStart, int colorEnd) {
        DrawInternals.fillGradient(matrices, startX, startY, endX, endY, z, colorStart, colorEnd);
    }

    public static void fillGradient(Matrix4f matrix, BufferBuilder builder, float startX, float startY, float endX, float endY, float z, int colorStart, int colorEnd) {
        DrawInternals.fillGradient(matrix, builder, startX, startY, endX, endY, z, colorStart, colorEnd);
    }

    public static void fillGradientHorizontal(MatrixStack matrices, float startX, float startY, float endX, float endY, int colorStart, int colorEnd, float z) {
        DrawInternals.fillGradientHorizontal(matrices, startX, startY, endX, endY, colorStart, colorEnd, z);
    }

    public static void fillGradientHorizontal(Matrix4f matrix, BufferBuilder builder, float startX, float startY, float endX, float endY, float z, int colorStart, int colorEnd) {
        DrawInternals.fillGradientHorizontal(matrix, builder, startX, startY, endX, endY, z, colorStart, colorEnd);
    }

    public static void drawTexture(MatrixStack matrices, float x, float y, float z, float u, float v, float width, float height) {
        DrawInternals.drawTexture(matrices, x, y, z, u, v, width, height);
    }

    public static void drawTexture(MatrixStack matrices, float x, float y, float z, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        DrawInternals.drawTexture(matrices, x, y, z, u, v, width, height, textureWidth, textureHeight);
    }

    public static void drawTexture(MatrixStack matrices, float x, float y, float width, float height, float u, float v, float regionWidth, float regionHeight, float textureWidth, float textureHeight) {
        DrawInternals.drawTexture(matrices, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    public static void drawTexture(MatrixStack matrices, float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        DrawInternals.drawTexture(matrices, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    public static void drawTexture(MatrixStack matrices, float x0, float x1, float y0, float y1, float z, float regionWidth, float regionHeight, float u, float v, float textureWidth, float textureHeight) {
        DrawInternals.drawTexture(matrices, x0, x1, y0, y1, z, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
    }

    public static void drawTexturedQuad(Matrix4f matrix, float x0, float x1, float y0, float y1, float z, float u0, float u1, float v0, float v1) {
        DrawInternals.drawTexturedQuad(matrix, x0, x1, y0, y1, z, u0, u1, v0, v1);
    }

    public static void renderGuiItemModelCustomMatrixNoTransform(MatrixStack matrixStack, ItemStack stack, BakedModel model) {
        DrawInternals.renderGuiItemModelCustomMatrixNoTransform(matrixStack, stack, model);
    }

    public static void renderGuiItemModelCustomMatrix(MatrixStack matrixStack, ItemStack stack, float x, float y, BakedModel model) {
        DrawInternals.renderGuiItemModelCustomMatrix(matrixStack, stack, x, y, model);
    }
}
