package me.melontini.dark_matter.impl.minecraft.mixin.debug;

import com.google.common.base.Strings;
import me.melontini.dark_matter.impl.minecraft.debug.ValueTrackerImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    @Final
    public TextRenderer textRenderer;
    @Unique
    private final List<String> DARK_MATTER$VALUES_TO_RENDER = new ArrayList<>();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/toast/ToastManager;draw(Lnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.AFTER), method = "render")
    private void dark_matter$renderValueTrack(boolean tick, CallbackInfo ci) {
        if (!DARK_MATTER$VALUES_TO_RENDER.isEmpty()) {
            MatrixStack stack = new MatrixStack();
            for (int i = 0; i < DARK_MATTER$VALUES_TO_RENDER.size(); ++i) {
                String string = DARK_MATTER$VALUES_TO_RENDER.get(i);
                if (!Strings.isNullOrEmpty(string)) {
                    int k = textRenderer.getWidth(string);
                    int m = 2 + 9 * i;
                    DrawableHelper.fill(stack, 1, m - 1, 2 + k + 1, m + 9 - 1, -1873784752);
                    textRenderer.draw(stack, string, 2.0F, (float) m, 14737632);
                }
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void dark_matter$tickValueTrack(CallbackInfo ci) {
        DARK_MATTER$VALUES_TO_RENDER.clear();

        ValueTrackerImpl.checkTimers();
        ValueTrackerImpl.getView().forEach((id, supplier) -> DARK_MATTER$VALUES_TO_RENDER.add(id + ": " + supplier.get()));
    }

    @Inject(method = "startIntegratedServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/LevelLoadingScreen;tick()V"))
    private void dark_matter$tickValueTrackIntegratedServer(CallbackInfo ci) {
        DARK_MATTER$VALUES_TO_RENDER.clear();

        ValueTrackerImpl.checkTimers();
        ValueTrackerImpl.getView().forEach((id, supplier) -> DARK_MATTER$VALUES_TO_RENDER.add(id + ": " + supplier.get()));
    }
}
