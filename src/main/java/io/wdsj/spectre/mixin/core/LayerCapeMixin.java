package io.wdsj.spectre.mixin.core;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.wdsj.spectre.config.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Unused, for removal
@Mixin(LayerCape.class)
public class LayerCapeMixin {
    @WrapOperation(
            method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V"
            )
    )
    public void spectre$doRenderLayer(float colorRed, float colorGreen, float colorBlue, float colorAlpha, Operation<Void> original, @Local(argsOnly = true) AbstractClientPlayer renderedPlayer, @Share("isGhostCape") LocalBooleanRef isGhostCape) {
        AbstractClientPlayer thisPlayer = Minecraft.getMinecraft().player;
        if (thisPlayer != null && !thisPlayer.equals(renderedPlayer) && thisPlayer.getDistanceSq(renderedPlayer) < Settings.PlayerSpectreSettings.ghostDistance * Settings.PlayerSpectreSettings.ghostDistance) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.color(colorRed, colorGreen, colorBlue, Settings.PlayerSpectreSettings.playerGhostAlpha);
            GlStateManager.depthMask(false);
            isGhostCape.set(true);
        } else {
            original.call(colorRed, colorGreen, colorBlue, colorAlpha);
        }
    }

    @Inject(
            method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V"
            )
    )
    public void spectre$doRenderLayerFinalize(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci, @Share("isGhostCape") LocalBooleanRef isGhostCape) {
        if (isGhostCape.get()) {
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
        }
    }
}
