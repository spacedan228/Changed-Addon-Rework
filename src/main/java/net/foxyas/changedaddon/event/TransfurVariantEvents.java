package net.foxyas.changedaddon.event;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

public class TransfurVariantEvents {


    public static class KillAfterTransfurredEvent extends Event {

        public final LivingEntity targetEntity;
        public final LivingEntity sourceEntity;

        public KillAfterTransfurredEvent(LivingEntity targetEntity, LivingEntity sourceEntity) {
            this.targetEntity = targetEntity;
            this.sourceEntity = sourceEntity;
        }

        public LivingEntity getTargetEntity() {
            return targetEntity;
        }

        public LivingEntity getSourceEntity() {
            return sourceEntity;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class SpawnAtTransfurredEntityEvent extends Event {

        public final LivingEntity spawnAt;
        public ChangedEntity changedEntity;

        public SpawnAtTransfurredEntityEvent(LivingEntity spawnAt, ChangedEntity changedEntity) {
            this.spawnAt = spawnAt;
            this.changedEntity = changedEntity;
        }

        public LivingEntity getSpawnAt() {
            return spawnAt;
        }

        public ChangedEntity getChangedEntity() {
            return changedEntity;
        }

        public void setChangedEntity(ChangedEntity changedEntity) {
            this.changedEntity = changedEntity;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }
}
