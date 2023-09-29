package me.melontini.dark_matter.impl.recipe_book.mixin.pages;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import me.melontini.dark_matter.api.base.util.MathStuff;
import me.melontini.dark_matter.api.recipe_book.RecipeBookHelper;
import me.melontini.dark_matter.api.recipe_book.interfaces.PaginatedRecipeBookWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = RecipeBookWidget.class, priority = 999)
public abstract class RecipeBookWidgetMixin implements PaginatedRecipeBookWidget {
    @Shadow @Final protected static Identifier TEXTURE;
    @Shadow protected MinecraftClient client;
    @Shadow private int parentWidth;
    @Shadow private int parentHeight;
    @Shadow private int leftOffset;
    @Shadow @Final private List<RecipeGroupButtonWidget> tabButtons;
    @Shadow public abstract boolean isOpen();
    @Shadow @Final public static int field_32408;
    @Shadow @Final public static int field_32409;

    @Unique
    private int page = 0;
    @Unique
    private int pages;
    @Unique
    private ToggleButtonWidget nextPageButton;
    @Unique
    private ToggleButtonWidget prevPageButton;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeGroupButtonWidget;setToggled(Z)V", shift = At.Shift.BEFORE), method = "reset")
    private void dark_matter$reset(CallbackInfo ci) {
        int a = (this.parentWidth - dm$horizontalOffset()) / 2 - this.leftOffset;
        int s = (this.parentHeight + dm$verticalOffset()) / 2;
        this.nextPageButton = new ToggleButtonWidget(a + 14, s + 2, 12, 17, false);
        this.nextPageButton.setTextureUV(1, 208, 13, 18, TEXTURE);
        this.prevPageButton = new ToggleButtonWidget(a - 35, s + 2, 12, 17, true);
        this.prevPageButton.setTextureUV(1, 208, 13, 18, TEXTURE);
        this.page = 0;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V", shift = At.Shift.BEFORE), method = "render")
    private void dark_matter$render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        dark_matter$renderPageText(matrices);
        this.prevPageButton.render(matrices, mouseX, mouseY, delta);
        this.nextPageButton.render(matrices, mouseX, mouseY, delta);
    }

    @Unique
    private void dark_matter$renderPageText(MatrixStack matrices) {
        if (this.pages > 1) {
            int x = (this.parentWidth - (dm$horizontalOffset() - 12)) / 2 - this.leftOffset - 10;
            int y = (this.parentHeight + (dm$verticalOffset() + 3)) / 2 + 5;

            String string = this.page + 1 + "/" + this.pages;
            int textLength = this.client.textRenderer.getWidth(string);
            this.client.textRenderer.draw(matrices, string, (x - textLength / 2F), y, -1);
        }
    }

    @Unique
    @Override
    public void dm$updatePages() {
        for (RecipeGroupButtonWidget widget : this.tabButtons) {
            widget.visible = widget.dm$getPage() == this.page;
        }
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
    private void dark_matter$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.client.player != null) if (this.isOpen() && !this.client.player.isSpectator()) {
            if (this.nextPageButton.mouseClicked(mouseX, mouseY, button)) {
                dm$setPage(this.page + 1);
                cir.setReturnValue(true);
            } else if (this.prevPageButton.mouseClicked(mouseX, mouseY, button)) {
                dm$setPage(this.page - 1);
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    @Override
    public void dm$updatePageSwitchButtons() {
        if (this.nextPageButton != null) this.nextPageButton.visible = this.pages > 1 && this.page < (this.pages - 1);
        if (this.prevPageButton != null) this.prevPageButton.visible = this.pages > 1 && this.page != 0;
    }

    @ModifyExpressionValue(at = @At(value = "FIELD", target = "Lnet/minecraft/client/recipebook/RecipeBookGroup;CRAFTING_SEARCH:Lnet/minecraft/client/recipebook/RecipeBookGroup;"), method = "refreshTabButtons", require = 0)
    private RecipeBookGroup dark_matter$refresh$correctGroup(RecipeBookGroup group, @Local RecipeGroupButtonWidget widget) {
        return RecipeBookHelper.isSearchGroup(widget.getCategory()) ? widget.getCategory() : group;
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeGroupButtonWidget;setPos(II)V"), method = "refreshTabButtons", index = 1)
    private int dark_matter$refresh$setPos(int y, @Local RecipeGroupButtonWidget widget, @Local(ordinal = 1) int j, @Share("index") LocalIntRef index) {
        y = j + widget.getHeight() * index.get();
        index.set(index.get() + 1);
        return y;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeGroupButtonWidget;setPos(II)V", shift = At.Shift.AFTER), method = "refreshTabButtons")
    private void dark_matter$refresh$setPos(CallbackInfo ci, @Local RecipeGroupButtonWidget widget, @Share("index") LocalIntRef index, @Share("wc") LocalIntRef wc) {
        widget.dm$setPage((int) Math.floor(wc.get() / 6f));
        if (index.get() == 6) index.set(0);
        wc.set(wc.get() + 1);
    }

    @Inject(at = @At("TAIL"), method = "refreshTabButtons")
    private void dark_matter$refresh$tail(CallbackInfo ci, @Share("wc") LocalIntRef wc) {
        this.pages = MathStuff.fastCeil(wc.get() / 6f);
        this.dm$updatePages();
        this.dm$updatePageSwitchButtons();
    }

    @Unique
    @Override
    public int dm$getPage() {
        return this.page;
    }

    @Unique
    @Override
    public void dm$setPage(int page) {
        if (page < 0) page = 0;
        if (page > pages - 1) page = pages - 1;

        this.page = page;
        dm$updatePages();
        dm$updatePageSwitchButtons();
    }

    @Unique
    @Override
    public int dm$getPageCount() {
        return this.pages;
    }

    @Unique
    private static int dm$horizontalOffset() {
        return field_32408;
    }

    @Unique
    private static int dm$verticalOffset() {
        return field_32409;
    }
}
