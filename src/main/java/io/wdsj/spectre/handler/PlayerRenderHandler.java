package io.wdsj.spectre.handler;

import io.wdsj.spectre.GlobalGhostState;
import io.wdsj.spectre.Spectre;
import io.wdsj.spectre.config.Settings;
import io.wdsj.spectre.duck.IEntityGhostState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerRenderHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRenderPre(RenderPlayerEvent.Pre event) {
        EntityPlayer renderedPlayer = event.getEntityPlayer();
        EntityPlayerSP clientPlayer = Minecraft.getMinecraft().player;

        if (renderedPlayer == null || renderedPlayer.equals(clientPlayer)) {
            return;
        }

        if (Settings.PlayerSpectreSettings.skipRidingPlayer && renderedPlayer.isRiding()) {
            return;
        }

        if (clientPlayer.getDistanceSq(renderedPlayer) < Settings.PlayerSpectreSettings.ghostDistance * Settings.PlayerSpectreSettings.ghostDistance) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.color(1.0F, 1.0F, 1.0F, Settings.PlayerSpectreSettings.playerGhostAlpha);
            GlStateManager.depthMask(false);
            ((IEntityGhostState) renderedPlayer).setGhost(true);
            GlobalGhostState.isSpectreRendering = true;
            GlobalGhostState.spectreAlpha = Settings.PlayerSpectreSettings.playerGhostAlpha;
        }
    }

    @SubscribeEvent
    public void onPlayerRenderPost(RenderPlayerEvent.Post event) {
        EntityPlayer renderedPlayer = event.getEntityPlayer();
        EntityPlayerSP clientPlayer = Minecraft.getMinecraft().player;

        if (renderedPlayer == null || renderedPlayer.equals(clientPlayer)) {
            return;
        }

        if (((IEntityGhostState) renderedPlayer).isGhost()) {
            GlobalGhostState.isSpectreRendering = false;
            ((IEntityGhostState) renderedPlayer).setGhost(false);
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }
}
