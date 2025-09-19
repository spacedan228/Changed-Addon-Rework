package net.foxyas.changedaddon.client.model.animations;

import net.foxyas.changedaddon.process.DEBUG;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator.AnimateStage;
import net.ltxprogrammer.changed.client.renderer.animate.wing.AbstractWingAnimatorV2;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
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
                this.rightWingRoot.z = (DEBUG.HeadPosZ);
                this.leftWingRoot.z = (DEBUG.HeadPosZ);
                this.leftWingRoot.y = (DEBUG.HeadPosY);
                this.rightWingRoot.y = (DEBUG.HeadPosY);
            } else {
                this.rightWingRoot.z = -0.5f;
                this.leftWingRoot.z = -0.5f;
                this.leftWingRoot.y = -3.5f;
                this.rightWingRoot.y = -3.5f;
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
    }
}
