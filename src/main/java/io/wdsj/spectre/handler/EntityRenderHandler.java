package io.wdsj.spectre.handler;

import io.wdsj.spectre.GlobalGhostState;
import io.wdsj.spectre.Spectre;
import io.wdsj.spectre.config.Settings;
import io.wdsj.spectre.duck.IEntityGhostState;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class EntityRenderHandler {
    private static final Set<Class<? extends Entity>> whitelistedEntities = Arrays.stream(Settings.EntitySpectreSettings.ghostEntityWhitelist)
            .map(EntityList::getClassFromName)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(ReferenceOpenHashSet::new));
    private long lastCheckMillis = System.currentTimeMillis();
    private boolean lastDensityCheckResult = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingRenderPre(RenderLivingEvent.Pre<EntityLivingBase> event) {
        if (!Settings.EntitySpectreSettings.enableEntitySpectre) {
            return;
        }
        EntityLivingBase renderedEntity = event.getEntity();
        EntityPlayerSP clientPlayer = Minecraft.getMinecraft().player;
        if (renderedEntity instanceof EntityPlayer) {
            return;
        }

        if (renderedEntity == null || renderedEntity.equals(clientPlayer) || !shouldEntityBeGhosted(renderedEntity)) {
            return;
        }

        RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;
        if (Settings.EntitySpectreSettings.skipTargetEntity && mouseOver != null && mouseOver.typeOfHit == RayTraceResult.Type.ENTITY && mouseOver.entityHit.equals(renderedEntity)) {
            return;
        }

        if (clientPlayer.getDistanceSq(renderedEntity) < Settings.EntitySpectreSettings.ghostDistance * Settings.EntitySpectreSettings.ghostDistance) {
            if (Settings.EntitySpectreSettings.enableDensityCheck) {
                if (System.currentTimeMillis() - lastCheckMillis > 500) {
                    if (!isDensityHighEnough(clientPlayer)) return;
                } else if (!lastDensityCheckResult) {
                    return;
                }
            }
            IEntityGhostState ghostState = (IEntityGhostState) renderedEntity;
            if (ghostState.spectre$isGhost()) return;

            ghostState.spectre$setGhost(true);
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.color(1.0F, 1.0F, 1.0F, Settings.EntitySpectreSettings.entityGhostAlpha);
            GlStateManager.depthMask(false);
            GlobalGhostState.isSpectreRendering = true;
            GlobalGhostState.spectreAlpha = Settings.EntitySpectreSettings.entityGhostAlpha;
        }
    }

    @SubscribeEvent
    public void onLivingRenderPost(RenderLivingEvent.Post<EntityLivingBase> event) {
        EntityLivingBase renderedEntity = event.getEntity();
        IEntityGhostState ghostState = (IEntityGhostState) renderedEntity;

        if (ghostState.spectre$isGhost()) {
            GlobalGhostState.isSpectreRendering = false;
            ghostState.spectre$setGhost(false);
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private boolean isDensityHighEnough(Entity centerEntity) {
        World world = centerEntity.world;
        if (world == null) {
            return false;
        }

        double radius = Settings.EntitySpectreSettings.densityCheckRadius;
        AxisAlignedBB checkArea = new AxisAlignedBB(
                centerEntity.posX - radius,
                centerEntity.posY - radius,
                centerEntity.posZ - radius,
                centerEntity.posX + radius,
                centerEntity.posY + radius,
                centerEntity.posZ + radius
        );
        List<EntityLivingBase> nearbyEntities = world.getEntitiesWithinAABB(EntityLivingBase.class, checkArea,
                entity -> entity != null && !(entity instanceof EntityPlayer)
        );
        lastCheckMillis = System.currentTimeMillis();

        boolean ret = nearbyEntities.size() >= Settings.EntitySpectreSettings.minEntityDensity;
        lastDensityCheckResult = ret;
        return ret;
    }

    public static void rebuildWhitelist() {
        whitelistedEntities.clear();
        whitelistedEntities.addAll(Arrays.stream(Settings.EntitySpectreSettings.ghostEntityWhitelist)
                .map(EntityList::getClassFromName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        Spectre.LOGGER.info("Rebuilt entity list with {} valid entries.", whitelistedEntities.size());
    }

    public static boolean shouldEntityBeGhosted(Entity entity) {
        return Settings.EntitySpectreSettings.invertWhitelist != whitelistedEntities.contains(entity.getClass());
    }
}

