package net.foxyas.changedaddon.client.model.clothes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.ltxprogrammer.changed.entity.beast.LatexHuman;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.jetbrains.annotations.NotNull;

public class LatexHumanHazardBodySuitModel extends AdvancedHumanoidModel<LatexHuman> implements AdvancedHumanoidModelInterface<LatexHuman, LatexHumanHazardBodySuitModel> {
    public static final ModelLayerLocation LATEX_PLAYER = ChangedAddonMod.layerLocation("latex_human_hazard_body_suit", "main");
    public static final ModelLayerLocation LATEX_PLAYER_SLIM = ChangedAddonMod.layerLocation("latex_human_hazard_body_suit_slim", "main");

    protected final ModelPart RightLeg;
    protected final ModelPart LeftLeg;
    protected final ModelPart RightArm;
    protected final ModelPart LeftArm;
    protected final ModelPart Head;
    protected final ModelPart Torso;
    protected final ModelPart RightPants;
    protected final ModelPart LeftPants;
    protected final ModelPart RightSleeve;
    protected final ModelPart LeftSleeve;
    protected final ModelPart Hat;
    protected final ModelPart Jacket;
    protected final HumanoidAnimator<LatexHuman, LatexHumanHazardBodySuitModel> animator;

    public LatexHumanHazardBodySuitModel(ModelPart root) {
        super(root);

        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        this.RightPants = this.RightLeg.getChild("RightPants");
        this.LeftPants = this.LeftLeg.getChild("LeftPants");
        this.RightSleeve = this.RightArm.getChild("RightSleeve");
        this.LeftSleeve = this.LeftArm.getChild("LeftSleeve");
        this.Hat = this.Head.getChild("Hat");
        this.Jacket = this.Torso.getChild("Jacket");
        this.animator = HumanoidAnimator.of(this).hipOffset(-1.5F).legLength(10.5F).addPreset(AnimatorPresets.humanLike(this.Head, this.Torso, this.LeftArm, this.RightArm, this.LeftLeg, this.RightLeg));
    }

    public void defaultModelProperties() {
        this.Hat.visible = true;
        this.Jacket.visible = true;
        this.LeftPants.visible = true;
        this.RightPants.visible = true;
        this.LeftSleeve.visible = true;
        this.RightSleeve.visible = true;
    }

    public void setModelProperties(AbstractClientPlayer player) {
        this.Hat.visible = player.isModelPartShown(PlayerModelPart.HAT);
        this.Jacket.visible = player.isModelPartShown(PlayerModelPart.JACKET);
        this.LeftPants.visible = player.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
        this.RightPants.visible = player.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
        this.LeftSleeve.visible = player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
        this.RightSleeve.visible = player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
    }

    public ModelPart getHat() {
        return Hat;
    }

    public static LayerDefinition createBodyLayer(CubeDeformation pCubeDeformation, boolean slim) {
        float armWidth = slim ? 3.0F : 4.0F;
        float rightArmOffset = slim ? 1.0F : 0.0F;

        CubeDeformation hatDeformation = pCubeDeformation.extend(0.5F);
        CubeDeformation clothingDeformation = pCubeDeformation.extend(0.25F);
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation), PartPose.offset(-2.0F, 12.0F, 0.0F));
        RightLeg.addOrReplaceChild("RightPants", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, clothingDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, pCubeDeformation), PartPose.offset(2.0F, 12.0F, 0.0F));
        LeftLeg.addOrReplaceChild("LeftPants", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, clothingDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, pCubeDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));
        Head.addOrReplaceChild("Hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, hatDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, pCubeDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));
        Torso.addOrReplaceChild("Jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, clothingDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F + rightArmOffset, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, pCubeDeformation), PartPose.offset(-5.0F, 2.0F, 0.0F));
        RightArm.addOrReplaceChild("RightSleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F + rightArmOffset, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, clothingDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, pCubeDeformation), PartPose.offset(5.0F, 2.0F, 0.0F));
        LeftArm.addOrReplaceChild("LeftSleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, clothingDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void prepareMobModel(@NotNull LatexHuman p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(this.animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand(LatexHuman entity) {
        this.animator.setupHand();
    }

    public void setupAnim(@NotNull LatexHuman entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public @NotNull ModelPart getArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public @NotNull ModelPart getSleeve(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.LeftSleeve : this.RightSleeve;
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

    public HumanoidAnimator<LatexHuman, LatexHumanHazardBodySuitModel> getAnimator(LatexHuman entity) {
        return this.animator;
    }
}
