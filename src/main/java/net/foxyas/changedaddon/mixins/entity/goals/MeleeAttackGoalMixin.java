package net.foxyas.changedaddon.mixins.entity.goals;

import net.foxyas.changedaddon.entity.api.IGrabberEntity;
import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MeleeAttackGoal.class)
public class MeleeAttackGoalMixin {

    @Shadow @Final protected PathfinderMob mob;

    @Inject(at = @At("HEAD"), method = "canUse", cancellable = true)
    public void canUseHook(CallbackInfoReturnable<Boolean> cir) {
        if (this.mob instanceof IGrabberEntity grabber) {
            GrabEntityAbilityInstance grabAbilityInstance = grabber.getGrabAbilityInstance();
            if (grabAbilityInstance != null && grabAbilityInstance.grabbedEntity != null) {
                cir.setReturnValue(false);
            }
        }
    }
}
