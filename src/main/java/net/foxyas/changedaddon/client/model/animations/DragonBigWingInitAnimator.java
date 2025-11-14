package net.foxyas.changedaddon.client.model.animations;

import net.foxyas.changedaddon.ability.WingFlapAbility;
import net.foxyas.changedaddon.init.ChangedAddonAbilities;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator.AnimateStage;
import net.ltxprogrammer.changed.client.renderer.animate.wing.AbstractWingAnimatorV2;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.NotNull;

public class DragonBigWingInitAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractWingAnimatorV2<T, M> {
    public DragonBigWingInitAnimator(ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2, ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        super(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2);
    }

    public HumanoidAnimator.AnimateStage preferredStage() {
        return AnimateStage.INIT;
    }

    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float wingRootYAgeLerp = Mth.lerp(this.core.ageLerp, 0.174532F, 0.3490659F);
        float wingRootZAgeLerp = Mth.lerp(this.core.ageLerp, 0.174532F, 0.2617994F);

        if (!entity.isFallFlying()) {
            if (entity.isFlying()) {
                this.rightWingRoot.z = //0.85f;
                Mth.lerp(
                        this.core.flyAmount,
                        this.rightWingRoot.z,
                        0.85f
                );
                this.leftWingRoot.z = //0.85f;
                        Mth.lerp(
                                this.core.flyAmount,
                                this.leftWingRoot.z,
                                0.85f
                        );
                this.leftWingRoot.y = //-4;
                        Mth.lerp(
                                this.core.flyAmount,
                                this.leftWingRoot.y,
                                -4
                        );
                this.rightWingRoot.y = //-4;
                        Mth.lerp(
                                this.core.flyAmount,
                                this.rightWingRoot.y,
                                -4
                        );
            } else {
                if (entity.getPose() == Pose.CROUCHING) {
                    this.rightWingRoot.z = 0f;
                    this.leftWingRoot.z = 0f;
                    this.leftWingRoot.y = -1.5f;
                    this.rightWingRoot.y = -1.5f;
                } else {
                    this.rightWingRoot.z = -0.5f;
                    this.leftWingRoot.z = -0.5f;
                    this.leftWingRoot.y = -3.5f;
                    this.rightWingRoot.y = -3.5f;
                }
            }

        } else {
            this.rightWingRoot.y = -4;
            this.leftWingRoot.y = -4;
            this.leftWingRoot.z = -0.5f;
            this.rightWingRoot.z = -0.5f;
        }

        this.leftWingRoot.x = 0;
        this.rightWingRoot.x = 0;


        this.leftWingRoot.xRot = 0.0F;
        this.leftWingRoot.yRot = -wingRootYAgeLerp;
        this.leftWingRoot.zRot = -wingRootZAgeLerp;
        this.rightWingRoot.xRot = 0.0F;
        this.rightWingRoot.yRot = wingRootYAgeLerp;
        this.rightWingRoot.zRot = wingRootZAgeLerp;
        this.leftWingBone1.xRot = 0.0F;
        this.leftWingBone1.yRot = 0.0F;
        this.leftWingBone1.zRot = -0.087266F;
        this.leftWingBone2.xRot = 0.0F;
        this.leftWingBone2.yRot = 0.0F;
        this.leftWingBone2.zRot = -0.48171F;
        this.rightWingBone1.xRot = 0.0F;
        this.rightWingBone1.yRot = 0.0F;
        this.rightWingBone1.zRot = 0.087266F;
        this.rightWingBone2.xRot = 0.0F;
        this.rightWingBone2.yRot = 0.0F;
        this.rightWingBone2.zRot = 0.48171F;


        if (entity.getUnderlyingPlayer() != null && ProcessTransfur.getPlayerTransfurVariant(entity.getUnderlyingPlayer()) != null) {
            TransfurVariantInstance<?> variantInstance = ProcessTransfur.getPlayerTransfurVariant(entity.getUnderlyingPlayer());
            if (variantInstance.hasAbility(ChangedAddonAbilities.WING_FLAP_ABILITY.get()) && variantInstance.getAbilityInstance(ChangedAddonAbilities.WING_FLAP_ABILITY.get()).canUse()
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
            }
        }
    }


    // Função de suavização
    private static float easeInOut(float t) {
        return t * t * (3 - 2 * t);
    }

    private static float easeOutCubic(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }

    // Method para limitar o valor entre min e max
    private static float capLevel(float value, float min, float max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }
}
