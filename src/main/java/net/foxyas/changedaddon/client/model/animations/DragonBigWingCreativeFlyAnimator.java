package net.foxyas.changedaddon.client.model.animations;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.wing.AbstractWingAnimatorV2;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class DragonBigWingCreativeFlyAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractWingAnimatorV2<T, M> {
    public static final float WING_FLAP_RATE = 0.2F;
    public static final float BODY_FLY_SCALE = 0.5F;

    public DragonBigWingCreativeFlyAnimator(ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2, ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        super(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2);
    }

    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.CREATIVE_FLY;
    }

    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
//        this.leftWingRoot.xRot = Mth.lerp(this.core.flyAmount, this.leftWingRoot.xRot, (float) Math.toRadians(DEBUG.HeadPosJ));
//        this.leftWingRoot.yRot = Mth.lerp(this.core.flyAmount, this.leftWingRoot.yRot, 0.087266F);
//        this.leftWingRoot.zRot = Mth.lerp(this.core.flyAmount, this.leftWingRoot.zRot, 0.0F);
//        this.rightWingRoot.xRot = Mth.lerp(this.core.flyAmount, this.rightWingRoot.xRot, (float) Math.toRadians(DEBUG.HeadPosJ));
//        this.rightWingRoot.yRot = Mth.lerp(this.core.flyAmount, this.rightWingRoot.yRot, -0.087266F);
//        this.rightWingRoot.zRot = Mth.lerp(this.core.flyAmount, this.rightWingRoot.zRot, 0.0F);


        this.leftWingRoot.xRot = Mth.lerp(this.core.flyAmount, this.leftWingRoot.xRot, (float) Math.toRadians(40f));
        this.leftWingRoot.yRot = Mth.lerp(this.core.flyAmount, this.leftWingRoot.yRot, 0.087266F);
        this.leftWingRoot.zRot = Mth.lerp(this.core.flyAmount, this.leftWingRoot.zRot, 0.0F);
        this.rightWingRoot.xRot = Mth.lerp(this.core.flyAmount, this.rightWingRoot.xRot, (float) Math.toRadians(40f));
        this.rightWingRoot.yRot = Mth.lerp(this.core.flyAmount, this.rightWingRoot.yRot, -0.087266F);
        this.rightWingRoot.zRot = Mth.lerp(this.core.flyAmount, this.rightWingRoot.zRot, 0.0F);
        this.leftWingBone1.zRot = Mth.lerp(this.core.flyAmount, this.leftWingBone1.zRot, -0.523598F);
        this.leftWingBone2.zRot = Mth.lerp(this.core.flyAmount, this.leftWingBone2.zRot, -0.959931F);
        this.rightWingBone1.zRot = Mth.lerp(this.core.flyAmount, this.rightWingBone1.zRot, 0.523598F);
        this.rightWingBone2.zRot = Mth.lerp(this.core.flyAmount, this.rightWingBone2.zRot, 0.959931F);
        float flapAmount = Mth.cos(ageInTicks * WING_FLAP_RATE);
        flapAmount *= flapAmount;
        float flapRotate = Mth.map(flapAmount, 0.0F, 1.0F, -0.174533f, 0.55850536F); // -0.34906584F, 0.55850536F old
        this.leftWingRoot.yRot = Mth.lerp(this.core.flyAmount, this.leftWingRoot.yRot, -flapRotate);
        this.rightWingRoot.yRot = Mth.lerp(this.core.flyAmount, this.rightWingRoot.yRot, flapRotate);
        this.leftWingRoot.zRot = Mth.lerp(this.core.flyAmount, this.leftWingRoot.zRot, -flapRotate);
        this.rightWingRoot.zRot = Mth.lerp(this.core.flyAmount, this.rightWingRoot.zRot, flapRotate);
    }
}
