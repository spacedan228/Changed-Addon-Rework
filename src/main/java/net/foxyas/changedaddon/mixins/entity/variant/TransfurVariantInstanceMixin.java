package net.foxyas.changedaddon.mixins.entity.variant;

import net.foxyas.changedaddon.entity.customHandle.AttributesHandle;
import net.foxyas.changedaddon.item.armor.DarkLatexCoatItem;
import net.foxyas.changedaddon.item.armor.HazmatSuitItem;
import net.foxyas.changedaddon.variants.VariantExtraStats;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TransfurVariantInstance.class, remap = false)
public abstract class TransfurVariantInstanceMixin {

    @Shadow
    @Final
    protected TransfurVariant<ChangedEntity> parent;
    @Shadow
    @Final
    private Player host;
    @Unique
    private boolean appliedFlySpeed;

    @Shadow
    public abstract TransfurVariant<?> getParent();

    @Shadow
    public abstract boolean shouldApplyAbilities();

    @Shadow
    public abstract ChangedEntity getChangedEntity();

    @Shadow
    public int ticksFlying;

    @Inject(method = "canWear", at = @At("HEAD"), cancellable = true)
    private void negateArmor(Player player, ItemStack itemStack, EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir) {
        if ((itemStack.getItem() instanceof HazmatSuitItem || itemStack.getItem() instanceof DarkLatexCoatItem) && slot.getType() == EquipmentSlot.Type.ARMOR) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tickFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;getFoodLevel()I", remap = true, ordinal = 0),
            cancellable = true)
    private void negateFly(CallbackInfo cir) {
        if (!this.host.isCreative() && !this.host.isSpectator()) {
            if (getChangedEntity() instanceof VariantExtraStats variantExtraStats) {
                if (!variantExtraStats.getFlyType().canFly()) {
                    if (host.getAbilities().flying || host.getAbilities().mayfly) {
                        host.getAbilities().mayfly = false;
                        host.getAbilities().flying = false;
                        host.onUpdateAbilities();
                    }

                    ticksFlying = 0;
                    cir.cancel();
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void negateFlyInTick(CallbackInfo cir) {
        if (this.parent.canGlide && this.shouldApplyAbilities()) {
            if (!this.host.isCreative() && !this.host.isSpectator()) {
                if (this.getChangedEntity() instanceof VariantExtraStats variantExtraStats) {
                    if (!variantExtraStats.getFlyType().canFly()) {
                        if (this.host.getAbilities().flying || this.host.getAbilities().mayfly) {
                            this.host.getAbilities().mayfly = false;
                            this.host.getAbilities().flying = false;
                            this.host.onUpdateAbilities();
                        }
                    }
                }
            }

            if (!this.host.isSpectator()) { // Spectator Can have multiple fly speeds
                if (getChangedEntity() instanceof VariantExtraStats variantExtraStats) {
                    if (variantExtraStats.getFlySpeed() != 0) {
                        if (variantExtraStats.getFlyType().canFly()) {
                            if (!this.appliedFlySpeed) {
                                this.appliedFlySpeed = true;
                                this.host.getAbilities().setFlyingSpeed(variantExtraStats.getFlySpeed());
                                this.host.onUpdateAbilities();
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "unhookAll", at = @At("TAIL"), cancellable = true)
    private void injectUnHookALl(Player player, CallbackInfo ci) {
        if (this.getChangedEntity() instanceof VariantExtraStats stats) {
            if (this.appliedFlySpeed) {
                this.appliedFlySpeed = false;
                this.host.getAbilities().setFlyingSpeed(AttributesHandle.DefaultPlayerFlySpeed);
                this.host.onUpdateAbilities();
            }
        }
    }

    @Inject(method = "save", at = @At("RETURN"), cancellable = true)
    private void InjectData(CallbackInfoReturnable<CompoundTag> cir) {
        if (this.getChangedEntity() instanceof VariantExtraStats stats) {
            stats.saveExtraData(cir.getReturnValue());
        }
    }

    @Inject(method = "load", at = @At("RETURN"), cancellable = true)
    private void readInjectedData(CompoundTag tag, CallbackInfo cir) {
        if (this.getChangedEntity() instanceof VariantExtraStats variantExtraStats) {
            variantExtraStats.readExtraData(tag);
        }
    }

    /*@Inject(method = "canWear", at = @At("HEAD"), cancellable = true)
    private void negateArmorForms(Player player, ItemStack itemStack, EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir){
        if (this.getParent() == ChangedAddonTransfurVariants.LATEX_SNEP_FERAL_FORM.get() || this.getParent() == ChangedAddonTransfurVariants.LATEX_SNEP.get()){
            cir.setReturnValue(false);
        }
    }*/
}
