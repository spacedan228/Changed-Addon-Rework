package net.foxyas.changedaddon.client.model.animations;

import net.foxyas.changedaddon.ability.WingFlapAbility;
import net.foxyas.changedaddon.init.ChangedAddonAbilities;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator.AnimateStage;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static net.foxyas.changedaddon.client.model.animations.AvaliUpperBodyInitAnimator.easeOutCubic;

public class AvaliFallFlyAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.Animator<T, M> {

    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public static final float WING_FLAP_TARGET_X = (float) Math.toRadians(-25);

    public AvaliFallFlyAnimator(ModelPart rightArm, ModelPart leftArm) {
        super();
        this.rightArm = rightArm;
        this.leftArm = leftArm;
    }

    @Override
    public AnimateStage preferredStage() {
        return AnimateStage.FALL_FLY;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float ticks = (float) entity.getFallFlyingTicks();
        // Aplicamos uma curva ease-in-out para suavizar o início e fim
        float t = Mth.clamp(ticks / 15.0F, 0.0F, 1.0F);
        float flyAmount = smootherStep(t); // Muito mais suave!

        float targetY = (float) Math.toRadians(90);
        float targetZ = (float) Math.toRadians(90);

        this.rightArm.yRot = Mth.lerp(flyAmount, this.rightArm.yRot, targetY);
        this.rightArm.zRot = Mth.lerp(flyAmount, this.rightArm.zRot, targetZ);

        this.leftArm.yRot = Mth.lerp(flyAmount, this.leftArm.yRot, -targetY);
        this.leftArm.zRot = Mth.lerp(flyAmount, this.leftArm.zRot, -targetZ);


        if (entity.getUnderlyingPlayer() != null && ProcessTransfur.getPlayerTransfurVariant(entity.getUnderlyingPlayer()) != null) { // Just a Fail Safe Check
            TransfurVariantInstance<?> variantInstance = ProcessTransfur.getPlayerTransfurVariant(entity.getUnderlyingPlayer());
            variantInstance.ifHasAbility(ChangedAddonAbilities.WING_FLAP_ABILITY.get(), (instance) -> {
                if (!instance.canUse()) return;
                if (entity.getUnderlyingPlayer().getAbilities().flying) return;

                // Aplicação no cálculo da rotação
                float progress = instance.getController().getHoldTicks() / (float) WingFlapAbility.MAX_TICK_HOLD;
                float easedProgress = easeOutCubic(progress); // Aplica suavização

                // Interpolação suave
                this.rightArm.xRot = Mth.lerp(easedProgress, this.rightArm.xRot, WING_FLAP_TARGET_X);
                this.leftArm.xRot = Mth.lerp(easedProgress, this.leftArm.xRot, WING_FLAP_TARGET_X);

            });
        }
    }

    float smootherStep(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

}
