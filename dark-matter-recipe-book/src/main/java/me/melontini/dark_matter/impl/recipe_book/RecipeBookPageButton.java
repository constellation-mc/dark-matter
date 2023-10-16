package me.melontini.dark_matter.impl.recipe_book;

import com.mojang.blaze3d.systems.RenderSystem;
import me.melontini.dark_matter.api.minecraft.util.TextUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RecipeBookPageButton extends ButtonWidget {

    private static final Identifier TEXTURE = new Identifier("dark-matter-recipe-book", "textures/gui/recipe_book_buttons.png");

    private final boolean next;
    private final RecipeBookWidget widget;

    public RecipeBookPageButton(int x, int y, RecipeBookWidget widget, boolean next) {
        super(x, y, 14, 13, next ? TextUtil.literal(">") : TextUtil.literal("<"), button -> {});
        this.widget = widget;
        this.next = next;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        if (this.visible) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            int u = this.active && this.isHovered() ? 28 : 0;
            int v = this.active ? 0 : 13;

            RenderSystem.enableDepthTest();
            this.drawTexture(matrices, this.x, this.y, u + (next ? 14 : 0), v, this.width, this.height);
            if (this.hovered && MinecraftClient.getInstance().currentScreen != null) {
                MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, TextUtil.literal(widget.dm$getPage() + 1 + "/" + widget.dm$getPageCount()), mouseX, mouseY);
            }
        }
    }
}
