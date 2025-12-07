package net.foxyas.changedaddon.client.model.api;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.AnimationState;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface IHierModel {

    default HashMap<String, ModelPart> getDefaultPlayerParts(PlayerModel<?> playerModel) {
        HashMap<String, ModelPart> partMap = new HashMap<>();
        partMap.put("head", playerModel.getHead());
        partMap.put("hat", playerModel.hat);
        partMap.put("body", playerModel.body);
        partMap.put("right_arm", playerModel.rightArm);
        partMap.put("left_arm", playerModel.leftArm);
        partMap.put("right_leg", playerModel.rightLeg);
        partMap.put("left_leg", playerModel.leftLeg);
        partMap.put("left_sleeve", playerModel.leftSleeve);
        partMap.put("right_sleeve", playerModel.rightSleeve);
        partMap.put("left_pants", playerModel.leftPants);
        partMap.put("right_pants", playerModel.rightPants);
        partMap.put("jacket", playerModel.jacket);

        return partMap;
    }

    ModelPart getRootPart();

    Optional<ModelPart> getAnyChild(String name);

    Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    default void animate(AnimationState pAnimationState, AnimationDefinition pAnimationDefinition, float pAgeInTicks) {
        this.animate(pAnimationState, pAnimationDefinition, pAgeInTicks, 1.0F);
    }

    default void animateWalk(AnimationDefinition pAnimationDefinition, float pLimbSwing, float pLimbSwingAmount, float pMaxAnimationSpeed, float pAnimationScaleFactor) {
        if (!(this instanceof EntityModel<?> model)) return;
        long i = (long) (pLimbSwing * 50.0F * pMaxAnimationSpeed);
        float f = Math.min(pLimbSwingAmount * pAnimationScaleFactor, 1.0F);
        DynamicKeyframeAnimations.animate(model, pAnimationDefinition, i, f, ANIMATION_VECTOR_CACHE);
    }

    default void animate(AnimationState pAnimationState, AnimationDefinition pAnimationDefinition, float pAgeInTicks, float pSpeed) {
        if (!(this instanceof EntityModel<?> model)) return;
        pAnimationState.updateTime(pAgeInTicks, pSpeed);
        pAnimationState.ifStarted((p_233392_) -> DynamicKeyframeAnimations.animate(model, pAnimationDefinition, p_233392_.getAccumulatedTime(), 1.0F, ANIMATION_VECTOR_CACHE));
    }

    default void applyStatic(AnimationDefinition pAnimationDefinition) {
        if (!(this instanceof EntityModel<?> model)) return;
        DynamicKeyframeAnimations.animate(model, pAnimationDefinition, 0L, 1.0F, ANIMATION_VECTOR_CACHE);
    }
}
