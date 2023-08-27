package me.melontini.dark_matter.impl.minecraft.mixin.debug;

import com.google.common.base.Strings;
import com.llamalad7.mixinextras.sugar.Local;
import me.melontini.dark_matter.impl.minecraft.debug.ValueTrackerImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(GameRenderer.class)
public class MinecraftClientMixin {
    @Unique
    private final TextRenderer dark_matter$text_renderer = MinecraftClient.getInstance().textRenderer;
    @Unique
    private final List<String> DARK_MATTER$VALUES_TO_RENDER = new ArrayList<>();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/toast/ToastManager;draw(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.AFTER), method = "render")
    private void dark_matter$renderValueTrack(float tickDelta, long startTime, boolean tick, CallbackInfo ci, @Local DrawContext context) {
        try {
            if (!DARK_MATTER$VALUES_TO_RENDER.isEmpty()) {
                //MatrixStack stack = new MatrixStack();
                for (int i = 0; i < DARK_MATTER$VALUES_TO_RENDER.size(); ++i) {
                    String string = DARK_MATTER$VALUES_TO_RENDER.get(i);
                    if (!Strings.isNullOrEmpty(string)) {
                        int k = dark_matter$text_renderer.getWidth(string);
                        int m = 2 + 9 * i;
                        context.fill(1, m - 1, 2 + k + 1, m + 9 - 1, -1873784752);
                        context.drawText(dark_matter$text_renderer, string, 2, m, 14737632, false);
                    }
                }
            }
        } catch (Throwable ignored) {}
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void dark_matter$tickValueTrack(CallbackInfo ci) {
        try {
            DARK_MATTER$VALUES_TO_RENDER.clear();

            ValueTrackerImpl.checkTimers();
            ValueTrackerImpl.getView().forEach((id, supplier) -> DARK_MATTER$VALUES_TO_RENDER.add(id + ": " + supplier.get()));
        } catch (Throwable ignored) {}
    }
}
