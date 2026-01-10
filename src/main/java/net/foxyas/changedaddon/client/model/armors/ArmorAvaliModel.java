package net.foxyas.changedaddon.client.model.armors;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.model.advanced.AvaliModel;
import net.foxyas.changedaddon.entity.advanced.AvaliEntity;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModelSet;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ArmorAvaliModel<T extends AvaliEntity> extends LatexHumanoidArmorModel<T, ArmorAvaliModel<T>> {
    public static final ArmorModelSet<AvaliEntity, ArmorAvaliModel<AvaliEntity>> MODEL_SET = ArmorModelSet.of(ChangedAddonMod.resourceLoc("armor_avali"), ArmorLatexMaleDragonModel::createArmorLayer, ArmorAvaliModel::new);

    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart LeftLeg;
    private final ModelPart RightLeg;
    private final ModelPart LeftArm;
    private final ModelPart RightArm;
    private final ModelPart Tail;
    private final HumanoidAnimator<T, ArmorAvaliModel<T>> animator;

    public ArmorAvaliModel(ModelPart modelPart, ArmorModel model) {
        super(modelPart, model);
        this.Head = modelPart.getChild("Head");
        this.Torso = modelPart.getChild("Torso");
        this.LeftLeg = modelPart.getChild("LeftLeg");
        this.RightLeg = modelPart.getChild("RightLeg");
        this.LeftArm = modelPart.getChild("LeftArm");
        this.RightArm = modelPart.getChild("RightArm");
        this.Tail = this.Torso.getChild("Tail");
        ModelPart tailPrimary = this.Tail.getChild("TailPrimary");
        ModelPart tailSecondary = tailPrimary.getChild("TailSecondary");
        ModelPart leftLowerLeg = this.LeftLeg.getChild("LeftLowerLeg");
        ModelPart leftFoot = leftLowerLeg.getChild("LeftFoot");
        ModelPart leftPad = leftFoot.getChild("LeftPad");
        ModelPart rightLowerLeg = this.RightLeg.getChild("RightLowerLeg");
        ModelPart rightFoot = rightLowerLeg.getChild("RightFoot");
        ModelPart rightPad = rightFoot.getChild("RightPad");

        animator = HumanoidAnimator.of(this).hipOffset(-1.5f)
                .addPreset(AvaliModel.AvaliAnimationPresets.AvaliLike(
                        Head,
                        Torso, LeftArm, RightArm,
                        Tail, List.of(tailPrimary, tailSecondary),
                        LeftLeg, leftLowerLeg, leftFoot, leftPad, RightLeg, rightLowerLeg, rightFoot, rightPad));
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
    public HumanoidAnimator<T, ArmorAvaliModel<T>> getAnimator(T avaliEntity) {
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
