package net.foxyas.changedaddon.mixins.entity.changedEntity;

import net.foxyas.changedaddon.entity.api.IAlphaAbleEntity;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.foxyas.changedaddon.entity.api.IAlphaAbleEntity.ALPHA_SCALE;
import static net.foxyas.changedaddon.entity.api.IAlphaAbleEntity.IS_ALPHA;

@Mixin(value = LivingEntity.class)
public abstract class LivingEntityChangedEntityMixin extends Entity {
    protected LivingEntityChangedEntityMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "onSyncedDataUpdated", at = @At("TAIL"), cancellable = false)
    private void changedEntityHook(EntityDataAccessor<?> pKey, CallbackInfo ci) {
        LivingEntity self = ChangedAddon$selfMixin();
        if (self instanceof ChangedEntity changedEntity) {
            if (pKey == IS_ALPHA || pKey == ALPHA_SCALE) {
                this.refreshDimensions();
                IAlphaAbleEntity.applyOrRemoveAlphaModifiers(self, entityData.get(IS_ALPHA), entityData.get(ALPHA_SCALE));
                IAbstractChangedEntity.forEitherSafe(changedEntity.maybeGetUnderlying()).map(IAbstractChangedEntity::getTransfurVariantInstance).ifPresent(TransfurVariantInstance::refreshAttributes);
            }
        }
    }


    @Unique
    private LivingEntity ChangedAddon$selfMixin() {
        return (LivingEntity) (Object) this;
    }
}
