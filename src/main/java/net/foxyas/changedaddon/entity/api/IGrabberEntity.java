package net.foxyas.changedaddon.entity.api;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.network.packet.S2CCheckGrabberEntity;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.network.PacketDistributor;

public interface IGrabberEntity {
    PathfinderMob asMob();

    LivingEntity getGrabTarget();

    GrabEntityAbilityInstance getGrabAbilityInstance();

    default GrabEntityAbilityInstance createGrabAbility() {
        if (this instanceof ChangedEntity changedEntity) {
            return new GrabEntityAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get(), IAbstractChangedEntity.forEntity(changedEntity));
        } else return null;
    }

    default void mayTickGrabAbility() {
        GrabEntityAbilityInstance grabAbilityInstance = this.getGrabAbilityInstance();
        if (grabAbilityInstance != null) {
            grabAbilityInstance.tickIdle();

            if (grabAbilityInstance.getController().getHoldTicks() > 0 && (grabAbilityInstance.canUse() && grabAbilityInstance.canKeepUsing())) {
                grabAbilityInstance.tick();
            }

            LivingEntity grabbed = grabAbilityInstance.grabbedEntity;
            if (grabbed != null) {
                IAbstractChangedEntity entity = grabAbilityInstance.entity;
                int grabberId = entity.getEntity().getId();
                if (!grabbed.level().isClientSide()) {
                    ChangedAddonMod.PACKET_HANDLER.send(
                            PacketDistributor.TRACKING_ENTITY.with(entity::getEntity),
                            new S2CCheckGrabberEntity(grabberId, grabbed.getId())
                    );
                }
            }

        }
    }

    default void saveGrabAbilityInTag(CompoundTag tag) {
        CompoundTag grabInstanceTag = new CompoundTag();

        GrabEntityAbilityInstance grabAbilityInstance = this.getGrabAbilityInstance();
        if (grabAbilityInstance != null) {
            grabAbilityInstance.saveData(grabInstanceTag);

            tag.put("grabAbility", grabInstanceTag);
        }
    }

    default void readGrabAbilityInTag(CompoundTag tag) {
        CompoundTag grabInstanceTag = new CompoundTag();

        GrabEntityAbilityInstance grabAbilityInstance = this.getGrabAbilityInstance();
        if (grabAbilityInstance != null) {
            grabAbilityInstance.readData(grabInstanceTag);

            tag.put("grabAbility", grabInstanceTag);
        }
    }

    default void mayDropGrabbedEntity(DamageSource pDamageSource, float pDamageAmount) {
        if (!this.asMob().level().isClientSide()) {
            GrabEntityAbilityInstance grabAbilityInstance = getGrabAbilityInstance();
            if (grabAbilityInstance != null) {
                LivingEntity grabbedEntity = grabAbilityInstance.grabbedEntity;
                if (grabbedEntity != null) {
                    grabAbilityInstance.releaseEntity();
                    // manda packet de GRAB (tipo ARMS)
                    Changed.PACKET_HANDLER.send(
                            PacketDistributor.TRACKING_ENTITY.with(this::asMob),
                            new GrabEntityPacket(this.asMob(), grabbedEntity, GrabEntityPacket.GrabType.RELEASE)
                    );
                }
            }
        }
    }
}
