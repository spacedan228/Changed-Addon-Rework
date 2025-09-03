package net.foxyas.changedaddon.mixins.abilities;

import net.foxyas.changedaddon.abilities.interfaces.GrabEntityAbilityExtensor;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

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

    @Inject(method = "tickIdle", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F", remap = true, shift = At.Shift.AFTER), cancellable = true)
    public void cancelSuitDmg(CallbackInfo ci) {
        if (this.isSafeMode()) {
            if (snuggleCooldown > 0) snuggleCooldown--;

            if (this.suitTransition >= 3.0f) {
                ci.cancel();

                if (!(grabbedEntity instanceof Player player)) {
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
                    if (!isAlreadySnuggledTight()) {
                        this.runTightHug(player);
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
                getSelf().entity.displayClientMessage(new TranslatableComponent("ability.changed_addon.grab_entity.extender.how_to_release", AbstractAbilityInstance.KeyReference.ABILITY.getName(level)), true);
            } else if (this.instructionTicks == 120) {
                getSelf().entity.displayClientMessage(new TranslatableComponent("ability.changed_addon.grab_entity.extender.how_to_hug", AbstractAbilityInstance.KeyReference.ATTACK.getName(level)), true);
            } else if (this.instructionTicks == 60) {
                getSelf().entity.displayClientMessage(new TranslatableComponent("ability.changed_addon.grab_entity.extender.how_to_hug.tightly", AbstractAbilityInstance.KeyReference.USE.getName(level)), true);
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
}
