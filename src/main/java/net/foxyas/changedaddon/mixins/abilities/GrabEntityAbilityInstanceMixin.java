package net.foxyas.changedaddon.mixins.abilities;

import net.foxyas.changedaddon.abilities.interfaces.GrabEntityAbilityExtensor;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
    @Shadow public float suitTransition;
    @Shadow public float grabStrength;
    @Unique
    private boolean safeMode = false;

    @Inject(method = "saveData", at = @At("TAIL"), cancellable = true)
    public void injectCustomData(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("safeMode", safeMode);
    }

    @Inject(method = "readData", at = @At("TAIL"), cancellable = true)
    public void readCustomData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("safeMode")) safeMode = tag.getBoolean("safeMode");
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

    @Inject(method = "tickIdle", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F", remap = true, shift = At.Shift.AFTER), cancellable = true)
    public void cancelSuitDmg(CallbackInfo ci) {
        if (this.isSafeMode()) {
            if (this.suitTransition >= 3.0f) {
                if (!(grabbedEntity instanceof Player player)) {
                    this.grabStrength = 1;
                    if (getSelf().getController().getHoldTicks() >= 1) {
                        this.suitTransition -= 0.5f;
                    }
                    this.runTightHug();
                }
                ci.cancel();
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
            return false;
        }
        // comportamento normal
        return ProcessTransfur.progressTransfur(grabbedEntity, damage, variant, ctx);
    }
}
