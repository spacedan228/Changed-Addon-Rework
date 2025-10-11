package net.foxyas.changedaddon.entity.api;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public interface CustomPatReaction {
    default void WhenPattedReactionSimple() {
    }

    default void WhenPattedReaction(Player patter, InteractionHand hand) {
    }

    default void WhenPattedReactionSpecific(Player patter, InteractionHand hand, Vec3 pattedLocation) {
    }

    default void WhenPatEvent(LivingEntity patter, InteractionHand hand, LivingEntity patTarget) {

    }

    default void WhenPatEventSpecific(LivingEntity patter, InteractionHand hand, LivingEntity patTarget, EntityHitResult patResult) {

    }
}
