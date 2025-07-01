package io.wdsj.spectre.mixin.core;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wdsj.spectre.duck.IEntityGhostState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RenderLivingBase.class, priority = 1001)
public abstract class RenderLivingBaseMixin {
    @WrapOperation(
            method = "renderLayers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/layers/LayerRenderer;doRenderLayer(Lnet/minecraft/entity/EntityLivingBase;FFFFFFF)V"
            )
    )
    public void spectre$renderLayers(LayerRenderer<?> instance, EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, Operation<Void> original) {
        IEntityGhostState ghostState = (IEntityGhostState) entitylivingbaseIn;
        if (ghostState.spectre$isGhost()) {
            try {
                GlStateManager.disableCull();
                original.call(instance, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            } finally {
                GlStateManager.enableCull();
            }
        } else {
            original.call(instance, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Inject(
            method = "setBrightness",
            at = @At("HEAD"),
            cancellable = true
    )
    public void spectre$setBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks, boolean combineTextures, CallbackInfoReturnable<Boolean> cir) {
        if (((IEntityGhostState) entitylivingbaseIn).spectre$isGhost()) {
            cir.setReturnValue(false);
        }
    }
}
