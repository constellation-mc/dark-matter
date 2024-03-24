package me.melontini.dark_matter.impl.recipe_book;

import com.mojang.blaze3d.systems.RenderSystem;
import me.melontini.dark_matter.api.minecraft.util.TextUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RecipeBookPageButton extends ButtonWidget {

    private static final Identifier TEXTURE = new Identifier("dark-matter-recipe-book", "textures/gui/recipe_book_buttons.png");

    private final boolean next;
    private final RecipeBookWidget widget;

    public RecipeBookPageButton(int x, int y, RecipeBookWidget widget, boolean next) {
        super(x, y, 14, 13, next ? TextUtil.literal(">") : Text.literal("<"), button -> {}, DEFAULT_NARRATION_SUPPLIER);
        this.widget = widget;
        this.next = next;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

        if (this.visible) {
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderTexture(0, TEXTURE);
            int u = this.active && this.isHovered() ? 28 : 0;
            int v = this.active ? 0 : 13;

            RenderSystem.enableDepthTest();
            context.drawTexture(TEXTURE, this.getX(), this.getY(), 0, u + (next ? 14 : 0), v, this.width, this.height, 256, 256);
            if (this.hovered && MinecraftClient.getInstance().currentScreen != null) {
                context.drawTooltip(MinecraftClient.getInstance().textRenderer, TextUtil.literal(widget.dm$getPage() + 1 + "/" + widget.dm$getPageCount()), mouseX, mouseY);
            }
        }
    }
}
