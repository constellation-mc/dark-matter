package me.melontini.dark_matter.test.recipe_book.mixin;

import me.melontini.dark_matter.test.recipe_book.InventoryScreenAccessor;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin implements InventoryScreenAccessor {

    @Unique private TexturedButtonWidget recipeBookButton;

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    private Element steal(Element par1) {
        if (par1 instanceof TexturedButtonWidget widget) {
            this.recipeBookButton = widget;
            return widget;
        }
        return par1;
    }

    @Override
    public void dark_matter$pressRecipeBookButton() {
        this.recipeBookButton.onPress();
    }
}
