package net.foxyas.changedaddon.client.model;// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.entity.bosses.VoidFoxEntity;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VoidFoxModel extends AdvancedHumanoidModel<VoidFoxEntity> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = ChangedAddonMod.layerLocation("void_fox", "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final ModelPart TailPrimary;
    private final ModelPart TailSecondary;
    private final ModelPart TailTertiary;
    private final ModelPart TailQuaternary;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;

    private final HumanoidAnimator<VoidFoxEntity, VoidFoxModel> animator;

    public VoidFoxModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Tail = this.Torso.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        this.TailPrimary = this.Tail.getChild("TailPrimary");
        this.TailSecondary = TailPrimary.getChild("TailSecondary");
        this.TailTertiary = TailSecondary.getChild("TailTertiary");
        this.TailQuaternary = TailTertiary.getChild("TailQuaternary");
        ModelPart leftLowerLeg = this.LeftLeg.getChild("LeftLowerLeg");
        ModelPart leftFoot = leftLowerLeg.getChild("LeftFoot");
        ModelPart rightLowerLeg = this.RightLeg.getChild("RightLowerLeg");
        ModelPart rightFoot = rightLowerLeg.getChild("RightFoot");
        this.animator = HumanoidAnimator.of(this).hipOffset(-1.5F).addPreset(AnimatorPresets.wolfLike(this.Head, this.Head.getChild("LeftEar"), this.Head.getChild("RightEar"), this.Torso, this.LeftArm, this.RightArm
                , this.Tail, List.of(TailPrimary, TailSecondary, TailTertiary, TailQuaternary)
                , this.LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), this.RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));


    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(48, 40).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(13, 57).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightArchFur = RightFoot.addOrReplaceChild("RightArchFur", CubeListBuilder.create(), PartPose.offset(0.0F, 7.075F, -4.975F));

        PartDefinition RightArchFur_r1 = RightArchFur.addOrReplaceChild("RightArchFur_r1", CubeListBuilder.create().texOffs(49, 76).addBox(-1.975F, -3.0F, -1.5F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(-0.1F, -4.3563F, 2.5923F, -0.3491F, 0.0148F, 0.0436F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(52, 32).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition RightPawBeans = RightPad.addOrReplaceChild("RightPawBeans", CubeListBuilder.create().texOffs(19, 93).mirror().addBox(-2.0F, 9.475F, -0.375F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F)).mirror(false)
                .texOffs(19, 89).mirror().addBox(-2.8F, 9.475F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false)
                .texOffs(19, 87).mirror().addBox(-1.5F, 9.475F, -1.875F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false)
                .texOffs(19, 91).mirror().addBox(-0.225F, 9.475F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false), PartPose.offset(1.0F, -8.5F, -0.05F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(32, 44).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(48, 22).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(56, 11).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftArchFur = LeftFoot.addOrReplaceChild("LeftArchFur", CubeListBuilder.create(), PartPose.offset(0.0F, 7.075F, -4.975F));

        PartDefinition LeftArchFur_r1 = LeftArchFur.addOrReplaceChild("LeftArchFur_r1", CubeListBuilder.create().texOffs(49, 67).addBox(-2.025F, -3.0F, -1.5F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.1F, -4.3563F, 2.5923F, -0.3491F, -0.0148F, -0.0436F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftPawBeans = LeftPad.addOrReplaceChild("LeftPawBeans", CubeListBuilder.create().texOffs(11, 93).addBox(0.0F, 9.475F, -0.375F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F))
                .texOffs(11, 89).addBox(1.8F, 9.475F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
                .texOffs(11, 87).addBox(0.5F, 9.475F, -1.875F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
                .texOffs(11, 91).addBox(-0.775F, 9.475F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)), PartPose.offset(-1.0F, -8.5F, -0.05F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(15, 32).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 22).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(24, 2).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 26.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition CheekFur = Head.addOrReplaceChild("CheekFur", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition LeftCheekFur = CheekFur.addOrReplaceChild("LeftCheekFur", CubeListBuilder.create(), PartPose.offsetAndRotation(0.2F, 0.0F, 0.0F, 0.0F, -0.2182F, 0.0F));

        PartDefinition cube_r1 = LeftCheekFur.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(84, 46).addBox(-4.0F, -2.0F, 0.0F, 5.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-3.8F, -1.3F, -2.0F, 0.1848F, 1.1264F, 0.2041F));

        PartDefinition cube_r2 = LeftCheekFur.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(84, 43).addBox(-4.0F, -2.0F, 0.0F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8F, -0.3F, -2.0F, 0.0F, 1.1345F, 0.0F));

        PartDefinition RightCheekFur = CheekFur.addOrReplaceChild("RightCheekFur", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.2F, 0.0F, 0.0F, 0.0F, 0.2182F, 0.0F));

        PartDefinition cube_r3 = RightCheekFur.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(84, 52).mirror().addBox(-1.0F, -2.0F, 0.0F, 5.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(3.8F, -1.3F, -2.0F, 0.1848F, -1.1264F, -0.2041F));

        PartDefinition cube_r4 = RightCheekFur.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(84, 49).mirror().addBox(-1.0F, -2.0F, 0.0F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(3.8F, -0.3F, -2.0F, 0.0F, -1.1345F, 0.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-3.0F, -7.7F, 0.0F));

        PartDefinition RightEarPivot = RightEar.addOrReplaceChild("RightEarPivot", CubeListBuilder.create().texOffs(0, 4).addBox(-1.9F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 16).addBox(-0.9F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(32, 22).addBox(-0.9F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(24, 0).addBox(0.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0501F)), PartPose.offsetAndRotation(0.5F, -1.25F, 0.0F, -0.1309F, 0.5236F, -0.3491F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(3.0F, -7.7F, 0.0F));

        PartDefinition LeftEarPivot = LeftEar.addOrReplaceChild("LeftEarPivot", CubeListBuilder.create().texOffs(0, 0).addBox(-1.1F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 20).addBox(-1.1F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(32, 24).addBox(-1.1F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 32).addBox(-1.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0501F)), PartPose.offsetAndRotation(-0.5F, -1.25F, 0.0F, -0.1309F, -0.5236F, 0.3491F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(24, 8).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(28, 28).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition BodyFur = Torso.addOrReplaceChild("BodyFur", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Neck = BodyFur.addOrReplaceChild("Neck", CubeListBuilder.create().texOffs(50, 88).addBox(-4.5F, -0.65F, -3.0F, 9.0F, 2.0F, 6.0F, new CubeDeformation(-0.3F)), PartPose.offset(0.0F, 0.25F, 0.0F));

        PartDefinition Back = Neck.addOrReplaceChild("Back", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition BackBigFur = Back.addOrReplaceChild("BackBigFur", CubeListBuilder.create(), PartPose.offset(0.0652F, 1.5802F, 1.542F));

        PartDefinition fur_r1 = BackBigFur.addOrReplaceChild("fur_r1", CubeListBuilder.create().texOffs(86, 36).mirror().addBox(-3.6904F, -1.4529F, -0.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.5516F, 0.1395F, 2.9193F));

        PartDefinition fur_r2 = BackBigFur.addOrReplaceChild("fur_r2", CubeListBuilder.create().texOffs(86, 33).mirror().addBox(-0.3096F, -1.4529F, -0.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(-0.1304F, 1.0F, 0.0F, 0.5516F, -0.1395F, -2.9193F));

        PartDefinition fur_r3 = BackBigFur.addOrReplaceChild("fur_r3", CubeListBuilder.create().texOffs(86, 30).mirror().addBox(-0.3096F, -1.4529F, -0.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(-0.1304F, 0.0F, 0.0F, 0.5516F, -0.1395F, -2.9193F));

        PartDefinition fur_r4 = BackBigFur.addOrReplaceChild("fur_r4", CubeListBuilder.create().texOffs(86, 27).mirror().addBox(-1.9107F, 0.0F, -0.75F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(86, 21).mirror().addBox(-1.9107F, 1.0F, 0.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(-0.1545F, -0.6826F, 0.708F, -0.5672F, 0.0F, 0.0F));

        PartDefinition fur_r5 = BackBigFur.addOrReplaceChild("fur_r5", CubeListBuilder.create().texOffs(86, 24).mirror().addBox(-1.9107F, 1.0F, 0.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(-0.1545F, -1.1826F, 0.708F, -0.5672F, 0.0F, 0.0F));

        PartDefinition fur_r6 = BackBigFur.addOrReplaceChild("fur_r6", CubeListBuilder.create().texOffs(86, 39).mirror().addBox(-3.6904F, -1.4529F, -0.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5516F, 0.1395F, 2.9193F));

        PartDefinition BackSmallFur = Back.addOrReplaceChild("BackSmallFur", CubeListBuilder.create(), PartPose.offset(-0.1848F, 3.0802F, 1.542F));

        PartDefinition fur_r7 = BackSmallFur.addOrReplaceChild("fur_r7", CubeListBuilder.create().texOffs(80, 27).mirror().addBox(1.6904F, -0.4529F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(0.3696F, -1.0F, 0.0F, 0.5516F, -0.1395F, -2.9193F));

        PartDefinition fur_r8 = BackSmallFur.addOrReplaceChild("fur_r8", CubeListBuilder.create().texOffs(80, 25).mirror().addBox(-3.6904F, -0.4529F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.5516F, 0.1395F, 2.9193F));

        PartDefinition fur_r9 = BackSmallFur.addOrReplaceChild("fur_r9", CubeListBuilder.create().texOffs(80, 23).mirror().addBox(1.6904F, -0.4529F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(0.3696F, 0.0F, 0.0F, 0.5516F, -0.1395F, -2.9193F));

        PartDefinition fur_r10 = BackSmallFur.addOrReplaceChild("fur_r10", CubeListBuilder.create().texOffs(80, 21).mirror().addBox(-3.6904F, -0.4529F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.5516F, 0.1395F, 2.9193F));

        PartDefinition Front = Neck.addOrReplaceChild("Front", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition FrontBigFur = Front.addOrReplaceChild("FrontBigFur", CubeListBuilder.create(), PartPose.offset(0.0652F, 1.5802F, -1.542F));

        PartDefinition fur_r11 = FrontBigFur.addOrReplaceChild("fur_r11", CubeListBuilder.create().texOffs(86, 15).addBox(-3.6904F, -1.4529F, -0.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, -0.5516F, -0.1395F, 2.9193F));

        PartDefinition fur_r12 = FrontBigFur.addOrReplaceChild("fur_r12", CubeListBuilder.create().texOffs(86, 12).mirror().addBox(-0.3096F, -1.4529F, -0.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(-0.1304F, 1.0F, 0.0F, -0.5516F, 0.1395F, -2.9193F));

        PartDefinition fur_r13 = FrontBigFur.addOrReplaceChild("fur_r13", CubeListBuilder.create().texOffs(86, 9).mirror().addBox(-0.3096F, -1.4529F, -0.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(-0.1304F, 0.0F, 0.0F, -0.5516F, 0.1395F, -2.9193F));

        PartDefinition fur_r14 = FrontBigFur.addOrReplaceChild("fur_r14", CubeListBuilder.create().texOffs(86, 6).addBox(-1.9107F, 0.0F, -0.25F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(86, 0).addBox(-1.9107F, 1.0F, -1.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-0.1545F, -0.6826F, -0.708F, 0.5672F, 0.0F, 0.0F));

        PartDefinition fur_r15 = FrontBigFur.addOrReplaceChild("fur_r15", CubeListBuilder.create().texOffs(86, 3).addBox(-1.9107F, 1.0F, -1.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-0.1545F, -1.1826F, -0.708F, 0.5672F, 0.0F, 0.0F));

        PartDefinition fur_r16 = FrontBigFur.addOrReplaceChild("fur_r16", CubeListBuilder.create().texOffs(86, 18).addBox(-3.6904F, -1.4529F, -0.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5516F, -0.1395F, 2.9193F));

        PartDefinition FrontSmallFur = Front.addOrReplaceChild("FrontSmallFur", CubeListBuilder.create(), PartPose.offset(-0.1848F, 3.0802F, -1.542F));

        PartDefinition fur_r17 = FrontSmallFur.addOrReplaceChild("fur_r17", CubeListBuilder.create().texOffs(80, 6).mirror().addBox(1.6904F, -0.4529F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(0.3696F, -1.0F, 0.0F, -0.5516F, 0.1395F, -2.9193F));

        PartDefinition fur_r18 = FrontSmallFur.addOrReplaceChild("fur_r18", CubeListBuilder.create().texOffs(80, 4).addBox(-3.6904F, -0.4529F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.5516F, -0.1395F, 2.9193F));

        PartDefinition fur_r19 = FrontSmallFur.addOrReplaceChild("fur_r19", CubeListBuilder.create().texOffs(80, 2).mirror().addBox(1.6904F, -0.4529F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(0.3696F, 0.0F, 0.0F, -0.5516F, 0.1395F, -2.9193F));

        PartDefinition fur_r20 = FrontSmallFur.addOrReplaceChild("fur_r20", CubeListBuilder.create().texOffs(80, 0).addBox(-3.6904F, -0.4529F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5516F, -0.1395F, 2.9193F));

        PartDefinition Body = BodyFur.addOrReplaceChild("Body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition TorsoFur_r1 = Body.addOrReplaceChild("TorsoFur_r1", CubeListBuilder.create().texOffs(11, 71).addBox(-2.5F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 5.0F, -1.9F, 0.2618F, 0.0F, 0.0F));

        PartDefinition TorsoFur_r2 = Body.addOrReplaceChild("TorsoFur_r2", CubeListBuilder.create().texOffs(11, 66).addBox(-2.5F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 2.5F, -2.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.0F, 0.4F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.1F, 0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(48, 50).addBox(-2.0F, 0.75F, -1.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 5.6F));

        PartDefinition Base_r2 = TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(27, 55).addBox(-2.5F, -0.45F, -2.0F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.7F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.75F, 7.1F));

        PartDefinition Base_r3 = TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, -0.7F, -1.95F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.6F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 1.8326F, 0.0F, 0.0F));

        PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 1.0F));

        PartDefinition Base_r4 = TailQuaternary.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(48, 59).addBox(-2.0F, -1.2F, -1.95F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.45F)), PartPose.offsetAndRotation(0.0F, -0.7F, 3.0F, 2.0071F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(16, 40).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.5F, 0.0F));

        PartDefinition RightArmPawBeans = RightArm.addOrReplaceChild("RightArmPawBeans", CubeListBuilder.create().texOffs(27, 93).mirror().addBox(-2.0F, 9.475F, -0.375F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F)).mirror(false)
                .texOffs(27, 89).mirror().addBox(-2.8F, 9.475F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false)
                .texOffs(27, 87).mirror().addBox(-1.5F, 9.475F, -1.875F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false)
                .texOffs(27, 91).mirror().addBox(-0.225F, 9.475F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition RightArmFur = RightArm.addOrReplaceChild("RightArmFur", CubeListBuilder.create(), PartPose.offset(-2.0F, 7.5F, -2.4F));

        PartDefinition RightArmFur_r1 = RightArmFur.addOrReplaceChild("RightArmFur_r1", CubeListBuilder.create().texOffs(0, 91).addBox(-3.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(3.3F, 0.0F, 3.4F, 0.0F, -1.5708F, 0.0873F));

        PartDefinition RightArmFur_r2 = RightArmFur.addOrReplaceChild("RightArmFur_r2", CubeListBuilder.create().texOffs(0, 86).mirror().addBox(-1.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(-1.3F, 0.0F, 3.4F, 0.0F, 1.5708F, -0.0873F));

        PartDefinition RightArmFur_r3 = RightArmFur.addOrReplaceChild("RightArmFur_r3", CubeListBuilder.create().texOffs(0, 81).mirror().addBox(-1.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 3.7F, -0.0873F, 0.0F, 0.0F));

        PartDefinition RightArmFur_r4 = RightArmFur.addOrReplaceChild("RightArmFur_r4", CubeListBuilder.create().texOffs(0, 76).mirror().addBox(-1.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.1F, 0.0873F, 0.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 40).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 1.5F, 0.0F));

        PartDefinition LeftArmPawBeans = LeftArm.addOrReplaceChild("LeftArmPawBeans", CubeListBuilder.create().texOffs(35, 93).addBox(0.0F, 9.475F, -0.375F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F))
                .texOffs(35, 89).addBox(1.8F, 9.475F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
                .texOffs(35, 87).addBox(0.5F, 9.475F, -1.875F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
                .texOffs(35, 91).addBox(-0.775F, 9.475F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition LeftArmFur = LeftArm.addOrReplaceChild("LeftArmFur", CubeListBuilder.create(), PartPose.offset(2.0F, 7.5F, -2.4F));

        PartDefinition LeftArmFur_r1 = LeftArmFur.addOrReplaceChild("LeftArmFur_r1", CubeListBuilder.create().texOffs(0, 71).mirror().addBox(-1.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(-3.3F, 0.0F, 3.4F, 0.0F, 1.5708F, -0.0873F));

        PartDefinition LeftArmFur_r2 = LeftArmFur.addOrReplaceChild("LeftArmFur_r2", CubeListBuilder.create().texOffs(0, 66).addBox(-3.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(1.3F, 0.0F, 3.4F, 0.0F, -1.5708F, 0.0873F));

        PartDefinition LeftArmFur_r3 = LeftArmFur.addOrReplaceChild("LeftArmFur_r3", CubeListBuilder.create().texOffs(0, 61).addBox(-3.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 3.7F, -0.0873F, 0.0F, 0.0F));

        PartDefinition LeftArmFur_r4 = LeftArmFur.addOrReplaceChild("LeftArmFur_r4", CubeListBuilder.create().texOffs(0, 56).addBox(-3.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.1F, 0.0873F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    public void prepareMobModel(@NotNull VoidFoxEntity p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        super.prepareMobModel(p_102861_, p_102862_, p_102863_, p_102864_);

        // Cabeça
        this.Head.x = 0;
        this.Head.y = 0;
        this.Head.z = 0;
        this.Head.xRot = 0;
        this.Head.yRot = 0;
        this.Head.zRot = 0;

        // Tronco
        this.Torso.x = 0;
        this.Torso.y = 0;
        this.Torso.z = 0;
        this.Torso.xRot = 0;
        this.Torso.yRot = 0;
        this.Torso.zRot = 0;

        // Braço esquerdo
        this.LeftArm.x = 0;
        this.LeftArm.y = 0;
        this.LeftArm.z = 0;
        this.LeftArm.xRot = 0;
        this.LeftArm.yRot = 0;
        this.LeftArm.zRot = 0;

        // Braço direito
        this.RightArm.x = 0;
        this.RightArm.y = 0;
        this.RightArm.z = 0;
        this.RightArm.xRot = 0;
        this.RightArm.yRot = 0;
        this.RightArm.zRot = 0;

        // Perna esquerda
        this.LeftLeg.x = 0;
        this.LeftLeg.y = 0;
        this.LeftLeg.z = 0;
        this.LeftLeg.xRot = 0;
        this.LeftLeg.yRot = 0;
        this.LeftLeg.zRot = 0;

        // Perna direita
        this.RightLeg.x = 0;
        this.RightLeg.y = 0;
        this.RightLeg.z = 0;
        this.RightLeg.xRot = 0;
        this.RightLeg.yRot = 0;
        this.RightLeg.zRot = 0;
    }

    @Override
    public void setupHand(VoidFoxEntity entity) {
        this.animator.setupHand();
    }

    public void setupAnim(@NotNull VoidFoxEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public @NotNull ModelPart getArm(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getLeg(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftLeg : this.RightLeg;
    }

    public @NotNull ModelPart getHead() {
        return this.Head;
    }

    public ModelPart getTorso() {
        return this.Torso;
    }

    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.Head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.RightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.LeftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public HumanoidAnimator<VoidFoxEntity, VoidFoxModel> getAnimator(VoidFoxEntity entity) {
        return animator;
    }
}