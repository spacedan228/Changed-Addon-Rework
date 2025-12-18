package net.foxyas.changedaddon.entity.api;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.ability.api.GrabEntityAbilityExtensor;
import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.foxyas.changedaddon.network.packet.DynamicGrabEntityPacket;
import net.foxyas.changedaddon.network.packet.S2CCheckGrabberEntity;
import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedAbilities;
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
                if (!grabbed.getLevel().isClientSide()) {
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
            grabInstanceTag.putInt("grabCooldown", this.getGrabCooldown());
            tag.put("grabAbility", grabInstanceTag);
        }
    }

    default void readGrabAbilityInTag(CompoundTag grabInstanceTag) {

        GrabEntityAbilityInstance grabAbilityInstance = this.getGrabAbilityInstance();
        if (grabAbilityInstance != null) {
            grabAbilityInstance.readData(grabInstanceTag);
            if (grabInstanceTag.contains("grabCooldown")) this.setGrabCooldown(grabInstanceTag.getInt("grabCooldown"));
        }
    }

    default void mayDropGrabbedEntity(DamageSource pDamageSource, float pDamageAmount) {
        if (!this.asMob().getLevel().isClientSide()) {
            GrabEntityAbilityInstance grabAbilityInstance = getGrabAbilityInstance();
            if (grabAbilityInstance != null) {
                LivingEntity grabbedEntity = grabAbilityInstance.grabbedEntity;
                if (grabbedEntity != null) {
                    grabAbilityInstance.releaseEntity();
                    // manda packet de GRAB (tipo ARMS)
                    ChangedAddonMod.PACKET_HANDLER.send(
                            PacketDistributor.TRACKING_ENTITY.with(this::asMob),
                            new DynamicGrabEntityPacket(this.asMob(), grabbedEntity, DynamicGrabEntityPacket.GrabType.RELEASE)
                    );

                    setGrabCooldown(120);
                }
            }
        }
    }

    void setGrabCooldown(int i);

    int getGrabCooldown();

    default boolean shouldRespectGrab(PathfinderMob entitiesTryingToTarget) {
        return entitiesTryingToTarget.getType().is(ChangedAddonTags.EntityTypes.IGNORE_GRABBED_TARGETS);
    }

    default int getGrabDamageCooldown() {
        return 20 * 2;
    }

    default void setCausingGrabDamage(boolean value) {
        GrabEntityAbilityInstance grabAbilityInstance = this.getGrabAbilityInstance();
        if (grabAbilityInstance != null) {
            grabAbilityInstance.attackDown = value;
        }
    }

    default boolean canCauseGrabDamage() {
        if (!(this instanceof LivingEntity living)) return false;
        GrabEntityAbilityInstance grabAbilityInstance = getGrabAbilityInstance();
        if (grabAbilityInstance == null) return false;

        return living.level.getNearbyEntities(
                LivingEntity.class,
                TargetingConditions.forNonCombat()
                        .range(16)
                        .ignoreLineOfSight()
                        .ignoreInvisibilityTesting(),
                living,
                living.getBoundingBox().inflate(16)
        ).stream().noneMatch((e) -> e != living && !(e instanceof ArmorStand) && e != grabAbilityInstance.grabbedEntity);
    }
}
