package net.foxyas.changedaddon.mixins.abilities;

import net.foxyas.changedaddon.ability.api.GrabEntityAbilityExtensor;
import net.foxyas.changedaddon.entity.api.ChangedEntityExtension;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@Mixin(value = GrabEntityAbilityInstance.class, remap = false)
public class GrabEntityAbilityInstanceMixin implements GrabEntityAbilityExtensor {

    @Shadow
    public boolean attackDown;
    @Shadow
    public boolean suited;
    @Shadow
    @Nullable
    public LivingEntity grabbedEntity;
    @Shadow
    public boolean useDown;
    @Shadow
    public float suitTransition;
    @Shadow
    public float grabStrength;
    @Shadow
    int instructionTicks;
    @Shadow
    public float suitTransitionO;

    @Unique
    private boolean safeMode = false;
    @Unique
    private int snuggleCooldown = 0;
    @Unique
    private boolean alreadySnuggledTight = false;

    @Inject(method = "saveData", at = @At("TAIL"), cancellable = true)
    public void injectCustomData(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("safeMode", safeMode);
        tag.putBoolean("alreadySnuggledTight", alreadySnuggledTight);
    }

    @Inject(method = "readData", at = @At("TAIL"), cancellable = true)
    public void readCustomData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("safeMode")) safeMode = tag.getBoolean("safeMode");
        if (tag.contains("alreadySnuggledTight")) alreadySnuggledTight = tag.getBoolean("alreadySnuggledTight");
    }

    @Override
    public boolean isSafeMode() {
        return safeMode;
    }

    @Override
    public void setSafeMode(boolean safeMode) {
        this.safeMode = safeMode;
    }

    @Unique
    public GrabEntityAbilityInstance getSelf() {
        return (GrabEntityAbilityInstance) (Object) this;
    }

    @Override
    public LivingEntity grabber() {
        return getSelf().entity.getEntity();
    }

    @Redirect(
            method = "tickIdle",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/ltxprogrammer/changed/entity/ChangedEntity;tryAbsorbTarget(Lnet/minecraft/world/entity/LivingEntity;Lnet/ltxprogrammer/changed/ability/IAbstractChangedEntity;FLjava/util/List;)Z"
            )
    )
    private boolean changedaddon$customAbsorb(ChangedEntity instance, LivingEntity target, IAbstractChangedEntity loserPlayer, float amount, @Nullable List<TransfurVariant<?>> possibleMobFusions) {
        GrabEntityAbilityInstance selfThis = (GrabEntityAbilityInstance) (Object) this;
        AttributeInstance attribute = instance.maybeGetUnderlying().getAttribute(ChangedAttributes.TRANSFUR_DAMAGE.get());
        if (attribute == null) {
            return instance.tryAbsorbTarget(this.grabbedEntity, selfThis.entity, amount, possibleMobFusions);
        }
        double attributeValue = attribute.getValue();
        float dmg = Mth.ceil((float) attributeValue * 1.33f);
        boolean result = instance.tryAbsorbTarget(this.grabbedEntity, selfThis.entity, dmg, possibleMobFusions);
        return result;
    }

    @Inject(method = "tickIdle", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F", remap = true, shift = At.Shift.AFTER), cancellable = true)
    public void cancelSuitDmg(CallbackInfo ci) {
        if (this.isSafeMode()) {
            if (snuggleCooldown > 0) snuggleCooldown--;

            if (this.suitTransition >= 3.0f) {
                ci.cancel();

                if (getSelf().entity.getChangedEntity() instanceof ChangedEntityExtension changedEntityExtension && changedEntityExtension.shouldAlwaysHoldGrab(grabbedEntity)) {
                    this.grabStrength = 1;
                    if (getSelf().getController().getHoldTicks() >= 1) {
                        this.suitTransition -= 0.25f;
                    }

                    if (grabbedEntity != null) {
                        if (!isAlreadySnuggledTight()) {
                            this.runTightHug(this.grabbedEntity);
                        }
                    }
                } else {
                    if (grabbedEntity != null) {
                        if (!isAlreadySnuggledTight()) {
                            this.runTightHug(this.grabbedEntity);
                        }
                    }
                }

            } else {
                this.alreadySnuggledTight = false;
            }
        }
    }

    @Override
    public boolean isAlreadySnuggled() {
        return snuggleCooldown > 0;
    }

    @Override
    public void setSnuggled(boolean value) {
        this.snuggleCooldown = value ? SNUGGLED_COOLDOWN : 0;
    }

    @Override
    public boolean isAlreadySnuggledTight() {
        return alreadySnuggledTight;
    }

    @Override
    public void setSnuggledTight(boolean value) {
        this.alreadySnuggledTight = value;
    }

    @Inject(method = "handleInstructions", at = @At("HEAD"), cancellable = true)
    public void handleSafeModeInstructions(Level level, CallbackInfo ci) {
        if (level.isClientSide() && this.isSafeMode()) {
            ci.cancel();
            if (this.instructionTicks == 180) {
                getSelf().entity.displayClientMessage(Component.translatable("ability.changed_addon.grab_entity.extender.how_to_release", AbstractAbilityInstance.KeyReference.ABILITY.getName(level)), true);
            } else if (this.instructionTicks == 120) {
                getSelf().entity.displayClientMessage(Component.translatable("ability.changed_addon.grab_entity.extender.how_to_hug", AbstractAbilityInstance.KeyReference.ATTACK.getName(level)), true);
            } else if (this.instructionTicks == 60) {
                getSelf().entity.displayClientMessage(Component.translatable("ability.changed_addon.grab_entity.extender.how_to_hug.tightly", AbstractAbilityInstance.KeyReference.USE.getName(level)), true);
            }

            if (this.instructionTicks > 0) {
                --this.instructionTicks;
            }

            if (this.instructionTicks < 0) {
                ++this.instructionTicks;
            }
        }
    }

    @Redirect(
            method = "tickIdle",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/ltxprogrammer/changed/process/ProcessTransfur;progressTransfur(Lnet/minecraft/world/entity/LivingEntity;FLnet/ltxprogrammer/changed/entity/variant/TransfurVariant;Lnet/ltxprogrammer/changed/entity/TransfurContext;)Z"
            )
    )
    private boolean changedAddon$disableProgressTransfur(LivingEntity grabbedEntity, float damage, TransfurVariant variant, TransfurContext ctx) {
        if (safeMode) {
            // Safe mode -> nunca aplica transfur
            if (!isAlreadySnuggled()) {
                this.runHug(grabbedEntity);
            }
            return false;
        }
        // comportamento normal
        return ProcessTransfur.progressTransfur(grabbedEntity, damage, variant, ctx);
    }


    /**
     * Modify the computed keyStrength value during escape handling.
     * You can adjust or completely override it here.
     *
     * @param original The computed keyStrength value
     * @return The modified keyStrength
     */
    @ModifyVariable(
            method = "handleEscape",
            at = @At(
                    value = "STORE",
                    ordinal = 0 // ordinal 0 = first float stored in that method
            ),
            name = "keyStrength"
    )
    private float changedaddon$modifyKeyStrength(float original) {
        if (this.grabbedEntity != null) {
            //Todo New GrabResistance Enchantment or attribute

            float analogicPercent = 0;

            List<EquipmentSlot> armorSlots = Arrays.stream(EquipmentSlot.values()).filter((equipmentSlot -> equipmentSlot.getType() == EquipmentSlot.Type.ARMOR)).toList();

            for (EquipmentSlot slot : armorSlots) {
                ItemStack itemBySlot = this.grabbedEntity.getItemBySlot(slot);
                int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.THORNS, itemBySlot);
                if (enchantmentLevel > 0) {
                    analogicPercent += (float) enchantmentLevel / Enchantments.THORNS.getMaxLevel();
                }
            }

            if (analogicPercent > 0) {
                return original * (1 + analogicPercent);
            } else {
                return original;
            }
        }


        return original;
    }
}
