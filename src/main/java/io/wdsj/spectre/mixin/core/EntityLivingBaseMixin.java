package io.wdsj.spectre.mixin.core;

import io.wdsj.spectre.duck.IEntityGhostState;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin implements IEntityGhostState {
    @Unique
    private boolean spectre$ghost = false;
    @Override
    public boolean isGhost() {
        return spectre$ghost;
    }
    @Override
    public void setGhost(boolean ghost) {
        this.spectre$ghost = ghost;
    }
}
