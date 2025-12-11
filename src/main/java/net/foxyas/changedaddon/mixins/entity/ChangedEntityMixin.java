package net.foxyas.changedaddon.mixins.entity;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.foxyas.changedaddon.entity.api.ChangedEntityExtension;
import net.foxyas.changedaddon.entity.api.IGrabberEntity;
import net.foxyas.changedaddon.init.ChangedAddonGameRules;
import net.foxyas.changedaddon.init.ChangedAddonMobEffects;
import net.foxyas.changedaddon.item.armor.DarkLatexCoatItem;
import net.foxyas.changedaddon.item.armor.HazardBodySuit;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.AbstractDarkLatexWolf;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedAccessorySlots;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(value = ChangedEntity.class, remap = false)
public abstract class ChangedEntityMixin extends Monster implements ChangedEntityExtension {

    @Shadow
    public abstract LivingEntity maybeGetUnderlying();

    @Shadow public abstract float computeHealthRatio();

    @Unique
    protected boolean pacified = false;

    protected ChangedEntityMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return !isPacified();
    }

    @Override
    public boolean isPacified() {
        return pacified;
    }

    @Override
    public void setPacified(boolean pacified) {
        this.pacified = pacified;
    }

    @Override
    public boolean isNeutralTo(@NotNull LivingEntity target) {
        if (hasEffect(ChangedAddonMobEffects.PACIFIED.get())) return true;
        if (this.isPacified()) return true;
        return false;
    }

    @Inject(at = @At("HEAD"), method = "targetSelectorTest", cancellable = true)
    private void onTargetSelectorTest(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (isNeutralTo(livingEntity)) cir.setReturnValue(false);

        Optional<IAbstractChangedEntity> grabberSafe = GrabEntityAbility.getGrabberSafe(livingEntity);
        if (grabberSafe.isPresent() && grabberSafe.get() instanceof IGrabberEntity changedEntity) {
            // Only Target if it is grabbed by a Player or a non IGrabberEntity
            cir.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "tryAbsorbTarget", cancellable = true)
    private void tryAbsorbTargetInjector(LivingEntity target, IAbstractChangedEntity source, float amount, @Nullable List<TransfurVariant<?>> possibleMobFusions, CallbackInfoReturnable<Boolean> cir) {
        Optional<AccessorySlots> forEntity = AccessorySlots.getForEntity(maybeGetUnderlying());
        if (forEntity.isPresent()) {
            AccessorySlots accessorySlots = forEntity.get();
            Optional<ItemStack> item = accessorySlots.getItem(ChangedAccessorySlots.FULL_BODY.get());
            if (item.isPresent()) {
                ItemStack stack = item.get();
                if (stack.getItem() instanceof HazardBodySuit) {
                    ChangedAddonMod.LOGGER.info("Event Canceled Happened, value has been set to:{}", false);
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);

        boolean flag = pLevel.getLevel().getGameRules().getBoolean(ChangedAddonGameRules.CHANGED_ENTITIES_SPAWN_DRESSED);
        boolean match = ChangedAddonServerConfiguration.CHANGED_SPAWN_DRESS_MODE.get().isMatch(this);
        if (flag && match) this.setDefaultClothing((ChangedEntity) (Object) this);

        return spawnGroupData;
    }

    @Inject(at = @At("HEAD"), method = "addAdditionalSaveData", remap = true)
    private void saveExtraData(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("isPacified", isPacified());
    }

    @Inject(at = @At("HEAD"), method = "readAdditionalSaveData", remap = true)
    private void readExtraData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("isPacified")) setPacified(tag.getBoolean("isPacified"));
    }

    @Unique
    private static boolean isDarkLatexCoat(ItemStack itemStack) {
        return itemStack != null
                && !itemStack.isEmpty()
                && itemStack.getItem() instanceof DarkLatexCoatItem;
    }

    @Inject(method = "targetSelectorTest", at = @At("HEAD"), cancellable = true)
    private void CancelTarget(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        ItemStack Head = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack Chest = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
        if (ChangedAddonServerConfiguration.DL_COAT_AFFECT_ALL.get()) {
            if (isDarkLatexCoat(Head) && isDarkLatexCoat(Chest)) {
                cir.setReturnValue(false);
            } else if (isDarkLatexCoat(Head) ^ isDarkLatexCoat(Chest)) {
                if (livingEntity.distanceTo((ChangedEntity) (Object) this) >= 4) {
                    cir.setReturnValue(false);
                }
            }
        } else {
            if ((ChangedEntity) (Object) this instanceof AbstractDarkLatexWolf) {
                if (isDarkLatexCoat(Head) && isDarkLatexCoat(Chest)) {
                    cir.setReturnValue(false);
                } else if (isDarkLatexCoat(Head) ^ isDarkLatexCoat(Chest)) {
                    if (livingEntity.distanceTo((ChangedEntity) (Object) this) >= 4) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }
}
