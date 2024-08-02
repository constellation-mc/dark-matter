package me.melontini.dark_matter.impl.item_group.mixin.animated_groups;

import com.llamalad7.mixinextras.sugar.Local;
import me.melontini.dark_matter.impl.item_group.ItemGroupExtensions;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin
    extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {

  public CreativeInventoryScreenMixin(
      CreativeInventoryScreen.CreativeScreenHandler screenHandler,
      PlayerInventory playerInventory,
      Text text) {
    super(screenHandler, playerInventory, text);
  }

  @Inject(
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/item/ItemGroup;getIcon()Lnet/minecraft/item/ItemStack;",
              shift = At.Shift.BEFORE),
      method = "renderTabIcon",
      cancellable = true)
  private void dark_matter$drawGroupIcon(
      DrawContext context,
      ItemGroup group,
      CallbackInfo ci,
      @Local(index = 3) boolean bl,
      @Local(index = 4) boolean bl2,
      @Local(index = 6) int j,
      @Local(index = 7) int k) {
    if (((ItemGroupExtensions) group).dm$getIconAnimation() != null) {
      ((ItemGroupExtensions) group)
          .dm$getIconAnimation()
          .animateIcon(group, context, j, k, bl, bl2);
      ci.cancel();
    }
  }
}
