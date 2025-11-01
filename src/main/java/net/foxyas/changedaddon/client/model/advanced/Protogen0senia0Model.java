package net.foxyas.changedaddon.client.model.advanced;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.model.animations.ChangedAddonAnimationsPresets;
import net.foxyas.changedaddon.client.renderer.layers.animation.CarryAbilityAnimation;
import net.foxyas.changedaddon.entity.advanced.Protogen0senia0Entity;
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

public class Protogen0senia0Model extends AdvancedHumanoidModel<Protogen0senia0Entity> implements AdvancedHumanoidModelInterface<Protogen0senia0Entity, Protogen0senia0Model> {
    public static final ModelLayerLocation LAYER_LOCATION = ChangedAddonMod.layerLocation("0senia0_model", "main");;

    private final ModelPart Head;
    private final ModelPart RightEar;
    private final ModelPart LeftEar;
    private final ModelPart sigils;
    private final ModelPart visor;
    private final ModelPart middle;
    private final ModelPart right;
    private final ModelPart left;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final ModelPart TailPrimary;
    private final ModelPart TailSecondary;
    private final ModelPart TailTertiary;
    private final ModelPart TailQuaternary;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart RightLeg;
    private final ModelPart RightLowerLeg;
    private final ModelPart RightFoot;
    private final ModelPart RightPad;
    private final ModelPart LeftLeg;
    private final ModelPart LeftLowerLeg;
    private final ModelPart LeftFoot;
    private final ModelPart LeftPad;

    private final HumanoidAnimator<Protogen0senia0Entity, Protogen0senia0Model> animator;


    public Protogen0senia0Model(ModelPart root) {
        super(root);
        this.Head = root.getChild("Head");
        this.RightEar = this.Head.getChild("RightEar");
        this.LeftEar = this.Head.getChild("LeftEar");
        this.sigils = this.Head.getChild("sigils");
        this.visor = this.Head.getChild("visor");
        this.middle = this.visor.getChild("middle");
        this.right = this.visor.getChild("right");
        this.left = this.visor.getChild("left");
        this.Torso = root.getChild("Torso");
        this.Tail = this.Torso.getChild("Tail");
        this.TailPrimary = this.Tail.getChild("TailPrimary");
        this.TailSecondary = this.TailPrimary.getChild("TailSecondary");
        this.TailTertiary = this.TailSecondary.getChild("TailTertiary");
        this.TailQuaternary = this.TailTertiary.getChild("TailQuaternary");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        this.RightLeg = root.getChild("RightLeg");
        this.RightLowerLeg = this.RightLeg.getChild("RightLowerLeg");
        this.RightFoot = this.RightLowerLeg.getChild("RightFoot");
        this.RightPad = this.RightFoot.getChild("RightPad");
        this.LeftLeg = root.getChild("LeftLeg");
        this.LeftLowerLeg = this.LeftLeg.getChild("LeftLowerLeg");
        this.LeftFoot = this.LeftLowerLeg.getChild("LeftFoot");
        this.LeftPad = this.LeftFoot.getChild("LeftPad");

        animator = HumanoidAnimator.of(this).hipOffset(-1.5f)
                .addPreset(ChangedAddonAnimationsPresets.catLikeWolfTail(
                        Head, Head.getChild("LeftEar"), Head.getChild("RightEar"),
                        Torso, LeftArm, RightArm,
                        Tail, List.of(TailPrimary, TailSecondary, TailTertiary, TailQuaternary),
                        LeftLeg, LeftLowerLeg, LeftFoot, LeftPad, RightLeg, RightLowerLeg, RightFoot, RightPad));
    }

    @SuppressWarnings("unused")
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(24, 1).addBox(-3.1067F, -5.8663F, 0.628F, 6.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(56, 45).addBox(-3.1067F, -5.8727F, -0.3729F, 6.0F, 6.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.1067F, -0.7855F, -0.7724F, 0.0436F, 0.0F, 0.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.8067F, -2.8277F, 2.7487F, -0.6109F, 0.0F, 0.0F));

        PartDefinition RightEarFur_r1 = RightEar.addOrReplaceChild("RightEarFur_r1", CubeListBuilder.create().texOffs(59, 7).mirror().addBox(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 2.0F, new CubeDeformation(0.1F)).mirror(false), PartPose.offsetAndRotation(-1.35F, -2.7F, -1.25F, -0.4299F, -0.5707F, -0.3633F));

        PartDefinition RightEar_r1 = RightEar.addOrReplaceChild("RightEar_r1", CubeListBuilder.create().texOffs(56, 1).addBox(4.25F, -31.25F, -18.25F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.5F, 30.0F, 0.0F, -0.5236F, -0.1745F, -0.2618F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offsetAndRotation(1.5933F, -2.8277F, 2.7487F, -0.6109F, 0.0F, 0.0F));

        PartDefinition LeftEarFur_r1 = LeftEar.addOrReplaceChild("LeftEarFur_r1", CubeListBuilder.create().texOffs(49, 7).addBox(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(1.35F, -2.7F, -1.25F, -0.4299F, 0.5707F, 0.3633F));

        PartDefinition LeftEar_r1 = LeftEar.addOrReplaceChild("LeftEar_r1", CubeListBuilder.create().texOffs(46, 1).addBox(-6.25F, -31.25F, -18.25F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-2.5F, 30.0F, 0.0F, -0.5236F, 0.1745F, 0.2618F));

        PartDefinition sigils = Head.addOrReplaceChild("sigils", CubeListBuilder.create(), PartPose.offset(2.9896F, -3.3865F, 0.4468F));

        PartDefinition left_r1 = sigils.addOrReplaceChild("left_r1", CubeListBuilder.create().texOffs(80, 75).addBox(-0.5F, -1.5F, -1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(-0.45F))
                .texOffs(72, 78).mirror().addBox(-6.8908F, -1.5F, -1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(-0.45F)).mirror(false), PartPose.offsetAndRotation(0.099F, 0.3316F, 1.6932F, -1.2654F, 0.0F, 0.0F));

        PartDefinition left_r2 = sigils.addOrReplaceChild("left_r2", CubeListBuilder.create().texOffs(64, 78).addBox(-0.7F, -1.5F, -1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.01F))
                .texOffs(56, 75).addBox(-6.3F, -1.5F, -1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-0.0963F, 1.8316F, 0.6932F, -0.5672F, 0.0F, 0.0F));

        PartDefinition top_r1 = sigils.addOrReplaceChild("top_r1", CubeListBuilder.create().texOffs(56, 75).addBox(-0.5256F, -1.5002F, -1.3553F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-3.0963F, -2.1684F, 1.1932F, -0.7854F, -0.0436F, 1.5708F));

        PartDefinition visor = Head.addOrReplaceChild("visor", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.125F, -2.5727F, 1.1271F, -0.1745F, 0.0F, 0.0F));

        PartDefinition middle = visor.addOrReplaceChild("middle", CubeListBuilder.create(), PartPose.offset(0.0183F, 0.9562F, -8.081F));

        PartDefinition screen_r1 = middle.addOrReplaceChild("screen_r1", CubeListBuilder.create().texOffs(36, 41).addBox(-2.0F, -2.5F, -3.0F, 4.0F, 5.0F, 6.0F, new CubeDeformation(0.05F))
                .texOffs(16, 41).addBox(-2.0F, -2.5F, -3.0F, 4.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.625F, 3.75F, 0.1745F, 0.0F, 0.0F));

        PartDefinition left = visor.addOrReplaceChild("left", CubeListBuilder.create(), PartPose.offsetAndRotation(6.3737F, 0.4585F, -4.7672F, 0.0F, 0.0873F, 0.0F));

        PartDefinition screen_r2 = left.addOrReplaceChild("screen_r2", CubeListBuilder.create().texOffs(70, 1).addBox(-0.5F, -2.5217F, -3.0F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.05F))
                .texOffs(32, 70).addBox(-0.5F, -2.5217F, -3.0F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.3939F, -0.1114F, 0.067F, 0.1752F, 0.0859F, 0.0152F));

        PartDefinition right = visor.addOrReplaceChild("right", CubeListBuilder.create(), PartPose.offsetAndRotation(-6.3371F, 0.4585F, -4.7672F, 0.0F, -0.0873F, 0.0F));

        PartDefinition screen_r3 = right.addOrReplaceChild("screen_r3", CubeListBuilder.create().texOffs(50, 64).addBox(-0.5F, -2.5217F, -3.0F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.05F))
                .texOffs(65, 21).addBox(-0.5F, -2.5217F, -3.0F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.3939F, -0.1114F, 0.067F, 0.1752F, -0.0859F, -0.0152F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.5F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition cube_r1 = Torso.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 25).addBox(-4.5F, 1.0F, -2.5F, 9.0F, 2.0F, 5.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 6.9963F, 0.1489F, -0.0873F, 0.0F, -3.1416F));

        PartDefinition cube_r2 = Torso.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 25).addBox(-4.5F, -3.0F, -2.5F, 9.0F, 5.0F, 5.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 3.1F, 0.125F, -0.1309F, 0.0F, 0.0F));

        PartDefinition fluff_r1 = Torso.addOrReplaceChild("fluff_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-4.6067F, -1.5F, -3.5F, 9.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(51, 88).addBox(-2.6067F, -2.5F, -2.5F, 5.0F, 3.4F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1067F, 0.7934F, 0.6411F, 0.2182F, 0.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.5F, 0.0F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 87).addBox(-2.0F, 0.75F, -1.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.4F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1345F, 0.0F, 0.0F));

        PartDefinition Base_r2 = TailPrimary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(16, 62).addBox(-2.0F, 0.75F, -1.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.0908F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 4.5F));

        PartDefinition Base_r3 = TailSecondary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(45, 21).addBox(-2.5F, -0.45F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 1.3526F, 0.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.75F, 4.5F));

        PartDefinition Base_r4 = TailTertiary.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(13, 81).addBox(-2.5F, 3.55F, -3.3F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.2F))
                .texOffs(30, 12).addBox(-2.5F, 4.55F, -3.3F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.25F, -4.5F, 1.5272F, 0.0F, 0.0F));

        PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 8.0F));

        PartDefinition Base_r5 = TailQuaternary.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(28, 86).addBox(-2.5F, 4.55F, -3.3F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.21F))
                .texOffs(76, 29).addBox(-2.5F, 4.55F, -3.3F, 5.0F, 8.0F, 5.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, -0.75F, -4.75F, 1.5708F, 0.0F, 0.0F));

        PartDefinition TailQuinternary = TailQuaternary.addOrReplaceChild("TailQuinternary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 7.5F));

        PartDefinition Base_r6 = TailQuinternary.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(64, 71).addBox(-2.0F, 5.5F, -3.8F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(0.0F, -1.0F, -5.5F, 1.7017F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(28, 25).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(20, 52).addBox(-3.5F, -2.35F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(-0.3F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

        PartDefinition cube_r3 = RightArm.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 78).addBox(-1.0F, -1.5F, -1.5F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.4F, 0.25F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 36).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 52).addBox(-1.5F, -2.35F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(-0.3F)), PartPose.offset(5.0F, 2.0F, 0.0F));

        PartDefinition cube_r4 = LeftArm.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(46, 75).addBox(-1.0F, -1.5F, -1.5F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.4F, 0.25F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

        PartDefinition cube_r5 = RightLeg.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(72, 54).addBox(-1.0F, -2.5F, -1.5F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-1.2F, 4.1464F, -0.8876F, -0.2618F, 0.0F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(40, 52).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.02F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(56, 54).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(70, 12).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(32, 63).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

        PartDefinition cube_r6 = LeftLeg.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(74, 44).addBox(-1.0F, -2.5F, -1.5F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2F, 4.1464F, -0.8876F, -0.2618F, 0.0F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(56, 34).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.02F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(0, 62).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(16, 71).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(64, 64).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    @Override
    public void prepareMobModel(@NotNull Protogen0senia0Entity p_162861, float p_102862, float p_102863, float p_102864_) {
        this.prepareMobModel(animator, p_162861, p_102862, p_102863, p_102864_);
    }

    @Override
    public void setupHand(Protogen0senia0Entity entity) {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull Protogen0senia0Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.Tail.xRot += 20f * ((float) Math.PI / 180F);
        CarryAbilityAnimation.playAnimation(entity, this);
    }

    public @NotNull ModelPart getArm(HumanoidArm p_102852) {
        return p_102852 == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public @NotNull ModelPart getHead() {
        return this.Head;
    }

    public ModelPart getTorso() {
        return Torso;
    }

    public ModelPart getLeg(HumanoidArm humanoidArm) {
        return humanoidArm == HumanoidArm.LEFT ? this.LeftLeg : this.RightLeg;
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        //poseStack.pushPose();
        //poseStack.scale(1.05f, 1.05f, 1.05f);
        Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        //poseStack.popPose();

        Torso.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        RightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        RightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public HumanoidAnimator<Protogen0senia0Entity, Protogen0senia0Model> getAnimator(Protogen0senia0Entity entity) {
        return animator;
    }
}