package net.foxyas.changedaddon.mixins.entity.variant;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.foxyas.changedaddon.item.armor.DarkLatexCoatItem;
import net.foxyas.changedaddon.item.armor.HazmatSuitItem;
import net.foxyas.changedaddon.variants.VariantExtraStats;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Inject(method = "tickFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;getFoodLevel()I", ordinal = 0),
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

    @WrapOperation(at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Abilities;flying:Z", ordinal = 3),
            method = "tickFlying")
    private boolean modifyFlySpeed(Abilities instance, Operation<Boolean> original){
        boolean isFlying = original.call(instance);
        if(!(getChangedEntity() instanceof VariantExtraStats extraStats) || !isFlying) return isFlying;

        Vec3 delta = host.getDeltaMovement();
        if(extraStats.flightSpeedXZMul() != 1 || extraStats.flightSpeedYMul() != 1) delta = delta.multiply(extraStats.flightSpeedXZMul(), extraStats.flightSpeedYMul(), extraStats.flightSpeedXZMul());

        float horizontalPenalty = host.isSprinting() ? 0.825F : 0.8F;
        float verticalPenalty = delta.y > (double)0.0F ? 0.45F : 0.8F;
        host.setDeltaMovement(delta.multiply(horizontalPenalty, verticalPenalty, horizontalPenalty));

        float foodExhaustion = host.isSprinting() ? 0.05f : 0.025f;
        if(extraStats.flightFoodExhaustionMul() != 1) foodExhaustion *= extraStats.flightFoodExhaustionMul();
        host.causeFoodExhaustion(foodExhaustion);
        return false;
    }

    @Inject(method = "tick", at = @At("TAIL"))//seems unnecessary
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
