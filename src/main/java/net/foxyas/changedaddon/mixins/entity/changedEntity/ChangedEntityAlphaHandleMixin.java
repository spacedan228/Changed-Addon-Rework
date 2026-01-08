package net.foxyas.changedaddon.mixins.entity.changedEntity;

import net.foxyas.changedaddon.entity.api.IAlphaAbleEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChangedEntity.class, remap = false)
public abstract class ChangedEntityAlphaHandleMixin extends Monster implements IAlphaAbleEntity {

    protected boolean alpha;

    protected ChangedEntityAlphaHandleMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void setAlpha(boolean alpha) {
        this.alpha = alpha;
    }

    @Override
    public boolean isAlpha() {
        return alpha;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (isAlpha()) {
            tag.putBoolean("isAlpha", isAlpha());
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("isAlpha")) setAlpha(tag.getBoolean("isAlpha"));
    }

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void makeAlphasBigger(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (!isAlpha()) return;
        EntityDimensions originalValue = cir.getReturnValue();
        cir.setReturnValue(scaleForAlphaDimension(originalValue));
    }
}
