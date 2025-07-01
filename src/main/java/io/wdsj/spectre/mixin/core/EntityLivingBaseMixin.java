package io.wdsj.spectre.mixin.core;

import io.wdsj.spectre.duck.IEntityGhostState;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin implements IEntityGhostState {
    @Unique
    private boolean spectre$ghost = false;
    @Override
    public boolean spectre$isGhost() {
        return spectre$ghost;
    }
    @Override
    public void spectre$setGhost(boolean ghost) {
        this.spectre$ghost = ghost;
    }
}
