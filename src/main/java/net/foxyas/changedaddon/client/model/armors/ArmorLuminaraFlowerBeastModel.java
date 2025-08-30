package net.foxyas.changedaddon.client.model.armors;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.entity.advanced.LuminaraFlowerBeastEntity;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModelSet;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArmorLuminaraFlowerBeastModel<T extends ChangedEntity> extends LatexHumanoidArmorModel<T, ArmorLuminaraFlowerBeastModel<T>> {
    public static final ArmorModelSet<ChangedEntity, ArmorLuminaraFlowerBeastModel<ChangedEntity>> MODEL_SET = ArmorModelSet.of(ChangedAddonMod.resourceLoc("armor_luminara_flower_beast"), ArmorLuminaraFlowerBeastModel::createArmorLayer, ArmorLuminaraFlowerBeastModel::new);
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart LeftLeg;
    private final ModelPart RightLeg;
    private final ModelPart LeftArm;
    private final ModelPart RightArm;
    private final ModelPart Tail;
    private final ModelPart RightWing;
    private final ModelPart LeftWing;
    private final HumanoidAnimator<T, ArmorLuminaraFlowerBeastModel<T>> animator;

    public ArmorLuminaraFlowerBeastModel(ModelPart modelPart, ArmorModel model) {
        super(modelPart, model);
        this.Head = modelPart.getChild("Head");
        this.Torso = modelPart.getChild("Torso");
        this.LeftLeg = modelPart.getChild("LeftLeg");
        this.RightLeg = modelPart.getChild("RightLeg");
        this.LeftArm = modelPart.getChild("LeftArm");
        this.RightArm = modelPart.getChild("RightArm");
        this.Tail = this.Torso.getChild("Tail");
        this.RightWing = this.Torso.getChild("RightWing");
        this.LeftWing = this.Torso.getChild("LeftWing");
        ModelPart tailPrimary = this.Tail.getChild("TailPrimary");
        ModelPart tailSecondary = tailPrimary.getChild("TailSecondary");
        ModelPart leftLowerLeg = this.LeftLeg.getChild("LeftLowerLeg");
        ModelPart leftFoot = leftLowerLeg.getChild("LeftFoot");
        ModelPart rightLowerLeg = this.RightLeg.getChild("RightLowerLeg");
        ModelPart rightFoot = rightLowerLeg.getChild("RightFoot");
        ModelPart leftWingRoot = this.LeftWing.getChild("leftWingRoot");
        ModelPart rightWingRoot = this.RightWing.getChild("rightWingRoot");
        this.animator = HumanoidAnimator.of(this).hipOffset(-1.5F).addPreset(AnimatorPresets.wingedDragonLike(this.Head, this.Torso, this.LeftArm, this.RightArm, this.Tail, List.of(tailPrimary, tailSecondary), this.LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), this.RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad"), leftWingRoot, leftWingRoot.getChild("leftSecondaries"), leftWingRoot.getChild("leftSecondaries").getChild("leftTertiaries"), rightWingRoot, rightWingRoot.getChild("rightSecondaries"), rightWingRoot.getChild("rightSecondaries").getChild("rightTertiaries")));
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        addUnifiedLegs(partdefinition, layer);
        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, layer.dualDeformation), PartPose.offset(0.0F, -0.5F, 0.0F));
        PartDefinition RightWing = Torso.addOrReplaceChild("RightWing", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, 5.0F, 2.0F, 0.0F, 0.48F, 0.0F));
        PartDefinition rightWingRoot = RightWing.addOrReplaceChild("rightWingRoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        rightWingRoot.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(25, 23).addBox(-12.775F, -19.75F, 1.2F, 5.0F, 2.0F, 1.0F, layer.deformation.extend(-0.5F)), PartPose.offsetAndRotation(2.0F, 20.0F, -2.0F, 0.0F, 0.0F, 0.3491F));
        PartDefinition rightSecondaries = rightWingRoot.addOrReplaceChild("rightSecondaries", CubeListBuilder.create(), PartPose.offsetAndRotation(-7.3F, -7.0F, -0.5F, 0.0F, 0.0F, 0.5236F));
        PartDefinition rightTertiaries = rightSecondaries.addOrReplaceChild("rightTertiaries", CubeListBuilder.create(), PartPose.offsetAndRotation(0.3F, 0.0F, 0.0F, 0.0F, 0.0F, 0.9599F));
        PartDefinition LeftWing = Torso.addOrReplaceChild("LeftWing", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0F, 5.0F, 2.0F, 0.0F, -0.48F, 0.0F));
        PartDefinition leftWingRoot = LeftWing.addOrReplaceChild("leftWingRoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        leftWingRoot.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(25, 23).mirror().addBox(7.775F, -19.75F, 1.2F, 5.0F, 2.0F, 1.0F, layer.deformation.extend(-0.5F)).mirror(false), PartPose.offsetAndRotation(-2.0F, 20.0F, -2.0F, 0.0F, 0.0F, -0.3491F));
        PartDefinition leftSecondaries = leftWingRoot.addOrReplaceChild("leftSecondaries", CubeListBuilder.create(), PartPose.offsetAndRotation(7.3F, -7.0F, -0.5F, 0.0F, 0.0F, -0.5236F));
        PartDefinition leftTertiaries = leftSecondaries.addOrReplaceChild("leftTertiaries", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.3F, 0.0F, 0.0F, 0.0F, 0.0F, -0.9599F));
        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));
        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, layer.altDeformation.extend(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.4206F, 2.5285F, -2.0944F, 0.0F, 0.0F));
        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 3.5F));
        TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(0, 20).addBox(-1.5F, -1.5F, -3.0F, 3.0F, 3.0F, 6.0F, layer.altDeformation.extend(-0.1F)), PartPose.offsetAndRotation(0.0F, 1.2072F, 2.7389F, 2.7489F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, layer.dualDeformation), PartPose.offset(0.0F, -0.5F, 0.0F));
        partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.dualDeformation), PartPose.offset(-5.0F, 1.5F, 0.0F));
        partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.dualDeformation).mirror(false), PartPose.offset(5.0F, 1.5F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public void prepareVisibility(EquipmentSlot armorSlot, ItemStack item) {
        super.prepareVisibility(armorSlot, item);
        if (armorSlot == EquipmentSlot.LEGS) {
            prepareUnifiedLegsForArmor(item, this.LeftLeg, this.RightLeg, this.Tail);
        }

    }

    public void unprepareVisibility(EquipmentSlot armorSlot, ItemStack item) {
        super.unprepareVisibility(armorSlot, item);
        if (armorSlot == EquipmentSlot.LEGS) {
            prepareUnifiedLegsForArmor(item, this.LeftLeg, this.RightLeg, this.Tail);
        }
    }

    public void renderForSlot(T entity, RenderLayerParent<? super T, ?> parent, ItemStack stack, EquipmentSlot slot, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        this.scaleForSlot(parent, slot, poseStack);

        if (entity instanceof LuminaraFlowerBeastEntity luminaraFlowerBeastEntity) {
            Tail.visible &= luminaraFlowerBeastEntity.isAwakened();
            LeftWing.visible &= luminaraFlowerBeastEntity.isAwakened();
            RightWing.visible &= luminaraFlowerBeastEntity.isAwakened();
        }

        switch (slot) {
            case HEAD:
                this.Head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                break;
            case CHEST:
                this.Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                this.LeftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                this.RightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                break;
            case LEGS:
                this.Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                this.LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                this.RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                break;
            case FEET:
                this.LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                this.RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        poseStack.popPose();
    }

    @Override
    public void prepareMobModel(@NotNull T entity, float p_102862_, float p_102863_, float partialTicks) {
        super.prepareMobModel(entity, p_102862_, p_102863_, partialTicks);
    }

    @Override
    public void prepareMobModel(HumanoidAnimator<T, ? extends EntityModel<T>> animator, T entity, float p_102862_, float p_102863_, float partialTicks) {
        super.prepareMobModel(animator, entity, p_102862_, p_102863_, partialTicks);
        if (entity instanceof LuminaraFlowerBeastEntity luminaraFlowerBeastEntity) {
            LeftWing.visible = luminaraFlowerBeastEntity.isAwakened();
            RightWing.visible = luminaraFlowerBeastEntity.isAwakened();
            Tail.visible = luminaraFlowerBeastEntity.isAwakened();
        }
    }

    @Override
    public void setAllLimbsVisible(T entity, boolean visible) {
        super.setAllLimbsVisible(entity, visible);
        if (entity instanceof LuminaraFlowerBeastEntity luminaraFlowerBeastEntity) {
            LeftWing.visible = visible && luminaraFlowerBeastEntity.isAwakened();
            RightWing.visible = visible && luminaraFlowerBeastEntity.isAwakened();
            Tail.visible = luminaraFlowerBeastEntity.isAwakened();
        }
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public HumanoidAnimator<T, ArmorLuminaraFlowerBeastModel<T>> getAnimator(T entity) {
        return this.animator;
    }

    public ModelPart getArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getLeg(HumanoidArm leg) {
        return leg == HumanoidArm.LEFT ? this.LeftLeg : this.RightLeg;
    }

    public ModelPart getHead() {
        return this.Head;
    }

    public ModelPart getTorso() {
        return this.Torso;
    }
}
