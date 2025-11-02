package net.foxyas.changedaddon.client.model.clothes;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanModel;
import net.ltxprogrammer.changed.entity.beast.LatexHuman;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

public class LatexHumanHazardBodySuitModel extends LatexHumanModel {
    public static final ModelLayerLocation LATEX_PLAYER = ChangedAddonMod.layerLocation("latex_human_hazard_body_suit", "main");
    public static final ModelLayerLocation LATEX_PLAYER_SLIM = ChangedAddonMod.layerLocation("latex_human_hazard_body_suit_slim", "main");

    public LatexHumanHazardBodySuitModel(ModelPart root) {
        super(root);
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
}
