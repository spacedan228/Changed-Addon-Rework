package net.foxyas.changedaddon.mixins.client.renderer;

import net.foxyas.changedaddon.ability.WingFlapAbility;
import net.foxyas.changedaddon.client.model.animations.AvaliUpperBodyInitAnimator;
import net.foxyas.changedaddon.init.ChangedAddonAbilities;
import net.ltxprogrammer.changed.client.renderer.animate.wing.AbstractWingAnimatorV2;
import net.ltxprogrammer.changed.client.renderer.animate.wing.DragonWingInitAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DragonWingInitAnimator.class, remap = false)
public abstract class WingFlyAnimationMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractWingAnimatorV2<T, M> {

    public WingFlyAnimationMixin(ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2, ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        super(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2);
    }


    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void WingAnimation(@NotNull ChangedEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (entity.getUnderlyingPlayer() != null && ProcessTransfur.getPlayerTransfurVariant(entity.getUnderlyingPlayer()) != null) {
            TransfurVariantInstance<?> variantInstance = ProcessTransfur.getPlayerTransfurVariant(entity.getUnderlyingPlayer());
            variantInstance.ifHasAbility(ChangedAddonAbilities.WING_FLAP_ABILITY.get(), (instance) -> {
                if (!instance.canUse()) return;
                if (entity.getUnderlyingPlayer().getAbilities().flying) return;

                // Aplicação no cálculo da rotação
                float progress = instance.getController().getHoldTicks() / (float) WingFlapAbility.MAX_TICK_HOLD;
                float easedProgress = AvaliUpperBodyInitAnimator.easeOutCubic(progress); // Aplica suavização
                float maxRotation = AvaliUpperBodyInitAnimator.capLevel(35 * easedProgress, 0, 35); // Aplica o level cap

                // Interpolação suave
                this.leftWingRoot.zRot = -maxRotation * Mth.DEG_TO_RAD;
                this.rightWingRoot.zRot = maxRotation * Mth.DEG_TO_RAD;
            });



            /* Old Code
            WingFlapAbility.AbilityInstance abilityInstance = variantInstance.getAbilityInstance(ChangedAddonAbilities.WING_FLAP_ABILITY.get());
            if (variantInstance.hasAbility(ChangedAddonAbilities.WING_FLAP_ABILITY.get()) && abilityInstance.canUse()
                    && variantInstance.getSelectedAbility() instanceof WingFlapAbility.AbilityInstance WingFlapAbilityInstance) {
                if (entity.getUnderlyingPlayer().getAbilities().flying) {
                    return;
                }

                // Aplicação no cálculo da rotação
                float progress = WingFlapAbilityInstance.getController().getHoldTicks() / (float) WingFlapAbility.MAX_TICK_HOLD;
                float easedProgress = easeOutCubic(progress); // Aplica suavização
                float maxRotation = capLevel(35 * easedProgress, 0, 35); // Aplica o level cap

                // Interpolação suave
                this.leftWingRoot.zRot = -maxRotation * Mth.DEG_TO_RAD;
                this.rightWingRoot.zRot = maxRotation * Mth.DEG_TO_RAD;
            }*/
        }
    }
}
