package me.melontini.dark_matter.minecraft.mixin.debug;

import com.google.common.base.Strings;
import me.melontini.dark_matter.minecraft.debug.ValueTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Mixin(GameRenderer.class)
public class MinecraftClientMixin {
    private final List<String> DARK_MATTER$VALUES_TO_RENDER = new ArrayList<>();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/toast/ToastManager;draw(Lnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.AFTER), method = "render")
    private void dark_matter$renderValueTrack(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        try {
            if (!DARK_MATTER$VALUES_TO_RENDER.isEmpty()) {
                MatrixStack stack = new MatrixStack();
                for (int i = 0; i < DARK_MATTER$VALUES_TO_RENDER.size(); ++i) {
                    String string = DARK_MATTER$VALUES_TO_RENDER.get(i);
                    if (!Strings.isNullOrEmpty(string)) {
                        int k = MinecraftClient.getInstance().textRenderer.getWidth(string);
                        int m = 2 + 9 * i;
                        DrawableHelper.fill(stack, 1, m - 1, 2 + k + 1, m + 9 - 1, -1873784752);
                        MinecraftClient.getInstance().textRenderer.draw(stack, string, 2.0F, (float) m, 14737632);
                    }
                }
            }
        } catch (Throwable ignored) {}
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void dark_matter$tickValueTrack(CallbackInfo ci) {
        try {
            DARK_MATTER$VALUES_TO_RENDER.clear();

            ValueTracker.MANUAL_TRACK.forEach((s, o) -> DARK_MATTER$VALUES_TO_RENDER.add(s + ": " + (o != null ? o.toString() : "null")));

            for (Field field : ValueTracker.AUTO_TRACK_STATIC) {
                try {
                    DARK_MATTER$VALUES_TO_RENDER.add(field.getDeclaringClass().getName() + "." + field.getName() + ": " + field.get(null));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            ValueTracker.AUTO_TRACK.forEach((field, o) -> {
                try {
                    for (Object o1 : o) {
                        DARK_MATTER$VALUES_TO_RENDER.add(o1.toString() + "." + field.getName() + ": " + field.get(o1));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Throwable ignored) {}
    }
}
