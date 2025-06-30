package io.wdsj.spectre.mixin.core;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import io.wdsj.spectre.GlobalGhostState;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlStateManager.class)
public abstract class GlStateManagerMixin {
    @Inject(method = "color(FFFF)V", at = @At("HEAD"))
    private static void spectre$color(float colorRed, float colorGreen, float colorBlue, float colorAlpha, CallbackInfo ci, @Local(argsOnly = true, ordinal = 3) LocalFloatRef alphaRef) {
        if (GlobalGhostState.isSpectreRendering) {
            alphaRef.set(GlobalGhostState.spectreAlpha);
        }
    }

    @Inject(method = "disableBlend", at = @At("HEAD"), cancellable = true)
    private static void spectre$disableBlend(CallbackInfo ci) {
        if (GlobalGhostState.isSpectreRendering) {
            ci.cancel();
        }
    }
}
