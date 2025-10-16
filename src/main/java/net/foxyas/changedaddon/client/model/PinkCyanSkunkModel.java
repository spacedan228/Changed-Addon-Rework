package net.foxyas.changedaddon.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.entity.simple.PinkCyanSkunkEntity;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PinkCyanSkunkModel extends AdvancedHumanoidModel<PinkCyanSkunkEntity> implements AdvancedHumanoidModelInterface<PinkCyanSkunkEntity, PinkCyanSkunkModel> {

    public static final ModelLayerLocation LAYER_LOCATION = ChangedAddonMod.layerLocation(("pink_cyan_skunk"), "main");

    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final HumanoidAnimator<PinkCyanSkunkEntity, PinkCyanSkunkModel> animator;

    public PinkCyanSkunkModel(ModelPart root) {
        super(root);
        RightLeg = root.getChild("RightLeg");
        LeftLeg = root.getChild("LeftLeg");
        RightArm = root.getChild("RightArm");
        LeftArm = root.getChild("LeftArm");
        Head = root.getChild("Head");
        Torso = root.getChild("Torso");
        Tail = this.Torso.getChild("Tail");

        ModelPart tailPrimary = this.Tail.getChild("TailPrimary");
        ModelPart tailSecondary = tailPrimary.getChild("TailSecondary");
        ModelPart tailTertiary = tailSecondary.getChild("TailTertiary");
        ModelPart tailQuaternary = tailTertiary.getChild("TailQuaternary");
        ModelPart leftLowerLeg = this.LeftLeg.getChild("LeftLowerLeg");
        ModelPart leftFoot = leftLowerLeg.getChild("LeftFoot");
        ModelPart rightLowerLeg = this.RightLeg.getChild("RightLowerLeg");
        ModelPart rightFoot = rightLowerLeg.getChild("RightFoot");
        this.animator = HumanoidAnimator.of(this).hipOffset(-1.5F).addPreset(AnimatorPresets.catLike(this.Head, this.Head.getChild("LeftEar"), this.Head.getChild("RightEar"), this.Torso, this.LeftArm, this.RightArm, this.Tail, List.of(tailPrimary, tailSecondary, tailTertiary, tailQuaternary), this.LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), this.RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
    }

    @Override
    public void setupAnim(@NotNull PinkCyanSkunkEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));
        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(32, 58).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));
        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));
        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(64, 19).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));
        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));
        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(32, 69).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));
        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(68, 0).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));
        PartDefinition RightLegPawBeans = RightPad.addOrReplaceChild("RightLegPawBeans", CubeListBuilder.create().texOffs(64, 85).mirror().addBox(-4.3F, -13.15F, -0.575F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F)).mirror(false)
                .texOffs(64, 83).mirror().addBox(-2.375F, -13.025F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F)).mirror(false)
                .texOffs(64, 79).mirror().addBox(-3.3F, -13.025F, -2.575F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F)).mirror(false)
                .texOffs(64, 81).mirror().addBox(-4.3F, -13.025F, -2.575F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F)).mirror(false)
                .texOffs(64, 83).mirror().addBox(-5.225F, -13.025F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F)).mirror(false), PartPose.offset(3.3F, 14.125F, 0.15F));
        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));
        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(48, 62).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));
        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));
        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(64, 29).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));
        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));
        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(64, 69).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));
        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(64, 62).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));
        PartDefinition LeftLegPawBeans = LeftPad.addOrReplaceChild("LeftLegPawBeans", CubeListBuilder.create().texOffs(72, 83).addBox(4.225F, -13.025F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
                .texOffs(72, 79).addBox(2.3F, -13.025F, -2.575F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
                .texOffs(72, 81).addBox(3.3F, -13.025F, -2.575F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
                .texOffs(72, 83).addBox(1.375F, -13.025F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
                .texOffs(72, 85).addBox(2.3F, -13.15F, -0.575F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F)), PartPose.offset(-3.3F, 14.125F, 0.15F));
        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 15).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(36, 11).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 11).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));
        PartDefinition Nose_r1 = Head.addOrReplaceChild("Nose_r1", CubeListBuilder.create().texOffs(28, 47).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 26.0F, 0.0F, 0.1745F, 0.0F, 0.0F));
        PartDefinition HeadFlower = Head.addOrReplaceChild("HeadFlower", CubeListBuilder.create().texOffs(86, 0).addBox(-1.5F, -1.3F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(-0.35F)), PartPose.offsetAndRotation(-3.7F, -5.5F, 2.2F, 0.1354F, -0.495F, -1.4069F));
        PartDefinition petals_r1 = HeadFlower.addOrReplaceChild("petals_r1", CubeListBuilder.create().texOffs(86, 5).addBox(-1.5F, -0.55F, -1.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.3502F)), PartPose.offsetAndRotation(-0.95F, -0.8F, 0.0F, 0.0F, -1.5708F, 0.4363F));
        PartDefinition petals_r2 = HeadFlower.addOrReplaceChild("petals_r2", CubeListBuilder.create().texOffs(86, 13).addBox(-1.5F, -0.55F, -1.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.3503F)), PartPose.offsetAndRotation(0.95F, -0.8F, 0.0F, 0.0F, 1.5708F, -0.4363F));
        PartDefinition petals_r3 = HeadFlower.addOrReplaceChild("petals_r3", CubeListBuilder.create().texOffs(86, 17).addBox(-1.5F, -0.55F, -1.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.3501F)), PartPose.offsetAndRotation(0.0F, -0.8F, 0.95F, 0.4363F, 0.0F, 0.0F));
        PartDefinition petals_r4 = HeadFlower.addOrReplaceChild("petals_r4", CubeListBuilder.create().texOffs(86, 9).addBox(-1.5F, -0.55F, -1.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.3501F)), PartPose.offsetAndRotation(0.0F, -0.8F, -0.95F, -2.7053F, 0.0F, 3.1416F));
        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-2.5F, -5.0F, 0.0F));
        PartDefinition rightear_r1 = RightEar.addOrReplaceChild("rightear_r1", CubeListBuilder.create().texOffs(28, 78).addBox(4.25F, -31.25F, -18.25F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6F, 30.5F, -1.9F, -0.5672F, -0.1745F, -0.2618F));
        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(2.5F, -5.0F, 0.0F));
        PartDefinition leftear_r1 = LeftEar.addOrReplaceChild("leftear_r1", CubeListBuilder.create().texOffs(46, 73).addBox(-6.25F, -31.25F, -18.25F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.6F, 30.5F, -1.9F, -0.5672F, 0.1745F, 0.2618F));
        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 31).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(32, 15).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(32, 42).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 80).addBox(-4.5F, -0.5F, -3.0F, 9.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));
        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.5F, 0.0F));
        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 0.0F));
        PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(14, 73).addBox(-2.0F, 2.4F, 0.6F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 2.7F, 0.2F, 1.3526F, 0.0F, 0.0F));
        PartDefinition Base_r2 = TailPrimary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(0, 73).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.1F, 0.0F, 1.0908F, 0.0F, 0.0F));
        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.6F, 4.6F));
        PartDefinition Base_r3 = TailSecondary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(64, 11).addBox(-2.5F, -0.8F, -0.3F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(0.0F, 1.8F, 1.0F, 1.5272F, 0.0F, 0.0F));
        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.6F, 2.9F));
        PartDefinition Base_r4 = TailTertiary.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(56, 52).addBox(-3.5F, 3.0F, -2.5F, 7.0F, 5.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.25F, -2.7F, 1.7017F, 0.0F, 0.0F));
        PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 3.4F));
        PartDefinition Base_r5 = TailQuaternary.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(32, 29).addBox(-4.5F, -1.0F, -2.5F, 9.0F, 6.0F, 7.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.55F, 1.15F, 1.8762F, 0.0F, 0.0F));
        PartDefinition TailQuinary = TailQuaternary.addOrReplaceChild("TailQuinary", CubeListBuilder.create(), PartPose.offset(0.0F, -1.2F, 3.9F));
        PartDefinition Base_r6 = TailQuinary.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, -2.5F, 10.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.95F, 0.9F, 2.0071F, 0.0F, 0.0F));
        PartDefinition TailFlower = TailQuinary.addOrReplaceChild("TailFlower", CubeListBuilder.create().texOffs(86, 43).addBox(-1.5F, -1.3F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(-0.35F)), PartPose.offsetAndRotation(-3.4F, -4.3F, 0.3F, 0.7478F, -0.1377F, -0.3399F));
        PartDefinition petals_r5 = TailFlower.addOrReplaceChild("petals_r5", CubeListBuilder.create().texOffs(86, 48).addBox(-1.5F, -0.55F, -1.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.3502F)), PartPose.offsetAndRotation(-0.95F, -0.8F, 0.0F, 0.0F, -1.5708F, 0.4363F));
        PartDefinition petals_r6 = TailFlower.addOrReplaceChild("petals_r6", CubeListBuilder.create().texOffs(86, 56).addBox(-1.5F, -0.55F, -1.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.3503F)), PartPose.offsetAndRotation(0.95F, -0.8F, 0.0F, 0.0F, 1.5708F, -0.4363F));
        PartDefinition petals_r7 = TailFlower.addOrReplaceChild("petals_r7", CubeListBuilder.create().texOffs(86, 60).addBox(-1.5F, -0.55F, -1.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.3501F)), PartPose.offsetAndRotation(0.0F, -0.8F, 0.95F, 0.4363F, 0.0F, 0.0F));
        PartDefinition petals_r8 = TailFlower.addOrReplaceChild("petals_r8", CubeListBuilder.create().texOffs(86, 52).addBox(-1.5F, -0.55F, -1.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.3501F)), PartPose.offsetAndRotation(0.0F, -0.8F, -0.95F, -2.7053F, 0.0F, 3.1416F));
        PartDefinition TailSenary = TailQuinary.addOrReplaceChild("TailSenary", CubeListBuilder.create(), PartPose.offset(0.0F, -2.8F, 5.4F));
        PartDefinition Base_r7 = TailSenary.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(36, 0).addBox(-4.5F, 2.0F, -2.5F, 9.0F, 4.0F, 7.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.95F, -1.6F, 1.7453F, 0.0F, 0.0F));
        PartDefinition TailFlower2nd = TailSenary.addOrReplaceChild("TailFlower2nd", CubeListBuilder.create().texOffs(86, 64).addBox(-1.5F, -1.3F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(-0.35F)), PartPose.offsetAndRotation(-3.0F, -3.9F, 1.4F, 3.0142F, -0.6407F, 2.8664F));
        PartDefinition petals_r9 = TailFlower2nd.addOrReplaceChild("petals_r9", CubeListBuilder.create().texOffs(86, 69).addBox(-1.5F, -0.55F, -1.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.3502F)), PartPose.offsetAndRotation(-0.95F, -0.8F, 0.0F, 0.0F, -1.5708F, 0.4363F));
        PartDefinition petals_r10 = TailFlower2nd.addOrReplaceChild("petals_r10", CubeListBuilder.create().texOffs(86, 77).addBox(-1.5F, -0.55F, -1.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.3503F)), PartPose.offsetAndRotation(0.95F, -0.8F, 0.0F, 0.0F, 1.5708F, -0.4363F));
        PartDefinition petals_r11 = TailFlower2nd.addOrReplaceChild("petals_r11", CubeListBuilder.create().texOffs(86, 81).addBox(-1.5F, -0.55F, -1.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.3501F)), PartPose.offsetAndRotation(0.0F, -0.8F, 0.95F, 0.4363F, 0.0F, 0.0F));
        PartDefinition petals_r12 = TailFlower2nd.addOrReplaceChild("petals_r12", CubeListBuilder.create().texOffs(86, 73).addBox(-1.5F, -0.55F, -1.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.3501F)), PartPose.offsetAndRotation(0.0F, -0.8F, -0.95F, -2.7053F, 0.0F, 3.1416F));
        PartDefinition TailSeptenary = TailSenary.addOrReplaceChild("TailSeptenary", CubeListBuilder.create(), PartPose.offset(0.0F, -1.2F, 2.3F));
        PartDefinition Base_r8 = TailSeptenary.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(56, 42).addBox(-4.0F, 3.95F, -2.5F, 8.0F, 4.0F, 6.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, -0.6F, -3.4F, 1.309F, 0.0F, 0.0F));
        PartDefinition TailOctonary = TailSeptenary.addOrReplaceChild("TailOctonary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.1F, 3.5F));
        PartDefinition Base_r9 = TailOctonary.addOrReplaceChild("Base_r9", CubeListBuilder.create().texOffs(0, 47).addBox(-4.0F, 3.0F, -2.5F, 8.0F, 4.0F, 6.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, -1.4F, -2.8F, 0.8727F, 0.0F, 0.0F));
        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(16, 57).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.5F, 0.0F));
        PartDefinition ArmFlower = RightArm.addOrReplaceChild("ArmFlower", CubeListBuilder.create().texOffs(86, 21).addBox(-1.5F, -1.3F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(-0.35F)), PartPose.offsetAndRotation(-1.2F, -1.4F, -1.4F, 0.9641F, 0.2699F, 0.0953F));
        PartDefinition petals_r13 = ArmFlower.addOrReplaceChild("petals_r13", CubeListBuilder.create().texOffs(86, 27).addBox(-1.5F, -0.55F, -1.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.3502F)), PartPose.offsetAndRotation(-0.95F, -0.8F, 0.0F, 0.0F, -1.5708F, 0.4363F));
        PartDefinition petals_r14 = ArmFlower.addOrReplaceChild("petals_r14", CubeListBuilder.create().texOffs(86, 35).addBox(-1.5F, -0.55F, -1.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.3503F)), PartPose.offsetAndRotation(0.95F, -0.8F, 0.0F, 0.0F, 1.5708F, -0.4363F));
        PartDefinition petals_r15 = ArmFlower.addOrReplaceChild("petals_r15", CubeListBuilder.create().texOffs(86, 39).addBox(-1.5F, -0.55F, -1.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.3501F)), PartPose.offsetAndRotation(0.0F, -0.8F, 0.95F, 0.4363F, 0.0F, 0.0F));
        PartDefinition petals_r16 = ArmFlower.addOrReplaceChild("petals_r16", CubeListBuilder.create().texOffs(86, 31).addBox(-1.5F, -0.55F, -1.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(-0.3501F)), PartPose.offsetAndRotation(0.0F, -0.8F, -0.95F, -2.7053F, 0.0F, 3.1416F));
        PartDefinition RightArmPawBeans = RightArm.addOrReplaceChild("RightArmPawBeans", CubeListBuilder.create().texOffs(80, 83).mirror().addBox(-5.075F, -13.0F, -1.425F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F)).mirror(false)
                .texOffs(80, 79).mirror().addBox(-6.0F, -13.0F, -2.375F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F)).mirror(false)
                .texOffs(80, 81).mirror().addBox(-7.0F, -13.0F, -2.375F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F)).mirror(false)
                .texOffs(80, 83).mirror().addBox(-7.925F, -13.0F, -1.425F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F)).mirror(false)
                .texOffs(80, 85).mirror().addBox(-7.0F, -13.075F, -0.4175F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, 22.1F, 0.35F));
        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 57).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 1.5F, 0.0F));
        PartDefinition LeftArmPawBeans = LeftArm.addOrReplaceChild("LeftArmPawBeans", CubeListBuilder.create().texOffs(56, 83).addBox(4.575F, -13.025F, -0.925F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
                .texOffs(56, 85).addBox(5.5F, -13.1F, 0.0825F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(56, 83).addBox(7.425F, -13.025F, -0.925F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
                .texOffs(56, 81).addBox(6.5F, -13.025F, -1.875F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
                .texOffs(56, 79).addBox(5.5F, -13.025F, -1.875F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F)), PartPose.offset(-5.5F, 22.125F, -0.15F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void prepareMobModel(@NotNull PinkCyanSkunkEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
        prepareMobModel(this.animator, pEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
    }

    @Override
    public @NotNull ModelPart getArm(HumanoidArm humanoidArm) {
        return humanoidArm == HumanoidArm.RIGHT ? RightArm : LeftArm;
    }

    @Override
    public ModelPart getLeg(HumanoidArm humanoidArm) {
        return humanoidArm == HumanoidArm.RIGHT ? RightLeg : LeftLeg;
    }

    @Override
    public @NotNull ModelPart getHead() {
        return Head;
    }

    @Override
    public ModelPart getTorso() {
        return Torso;
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        RightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        Torso.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        RightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupHand(PinkCyanSkunkEntity changedEntity) {
        animator.setupHand();
    }

    @Override
    public HumanoidAnimator<PinkCyanSkunkEntity, PinkCyanSkunkModel> getAnimator(PinkCyanSkunkEntity changedEntity) {
        return animator;
    }
}
