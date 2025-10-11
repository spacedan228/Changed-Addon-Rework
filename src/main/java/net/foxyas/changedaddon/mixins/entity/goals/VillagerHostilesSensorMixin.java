package net.foxyas.changedaddon.mixins.entity.goals;

import net.foxyas.changedaddon.entity.api.ChangedEntityExtension;
import net.foxyas.changedaddon.init.ChangedAddonMobEffects;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedTags.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerHostilesSensor.class)
public abstract class VillagerHostilesSensorMixin {

    @Inject(method = "isClose", at = @At("HEAD"), cancellable = true)
    private void isClose(LivingEntity villager, LivingEntity hostile, CallbackInfoReturnable<Boolean> callback) {
        if (hostile instanceof ChangedEntity latex) {
            if (latex.getType().is(EntityTypes.LATEX) && latex.hasEffect(ChangedAddonMobEffects.PACIFIED.get())) {
                callback.setReturnValue(false);
            } else if (latex.getType().is(EntityTypes.LATEX) && ChangedEntityExtension.of(latex).isPacified()) {
                callback.setReturnValue(false);
            }
        }
    }

    @Inject(method = "isHostile", at = @At("HEAD"), cancellable = true)
    private void isHostile(LivingEntity hostile, CallbackInfoReturnable<Boolean> callback) {
        if (hostile instanceof ChangedEntity latex) {
            if (latex.getType().is(EntityTypes.LATEX) && latex.hasEffect(ChangedAddonMobEffects.PACIFIED.get())) {
                callback.setReturnValue(false);
            } else if (latex.getType().is(EntityTypes.LATEX) && ChangedEntityExtension.of(latex).isPacified()) {
                callback.setReturnValue(false);
            }
        }

    }
}
