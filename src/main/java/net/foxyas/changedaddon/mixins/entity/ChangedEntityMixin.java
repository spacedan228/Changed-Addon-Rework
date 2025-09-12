package net.foxyas.changedaddon.mixins.entity;

import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.foxyas.changedaddon.entity.defaults.AbstractExp2SnepChangedEntity;
import net.foxyas.changedaddon.entity.interfaces.ChangedEntityExtension;
import net.foxyas.changedaddon.init.ChangedAddonMobEffects;
import net.foxyas.changedaddon.item.armor.DarkLatexCoatItem;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.AbstractDarkLatexWolf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChangedEntity.class, remap = false)
public abstract class ChangedEntityMixin extends Monster implements ChangedEntityExtension {

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
    public boolean c_additions$isNeutralTo(@NotNull LivingEntity target) {
        if (hasEffect(ChangedAddonMobEffects.PACIFIED.get())) return true;
        if (this.isPacified()) return true;
        return false;
    }

    @Inject(at = @At("HEAD"), method = "targetSelectorTest", cancellable = true)
    private void onTargetSelectorTest(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (c_additions$isNeutralTo(livingEntity)) cir.setReturnValue(false);
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
