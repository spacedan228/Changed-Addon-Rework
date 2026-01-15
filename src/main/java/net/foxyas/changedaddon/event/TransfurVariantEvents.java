package net.foxyas.changedaddon.event;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

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
        @Nullable
        public ChangedEntity changedEntity;

        public SpawnAtTransfurredEntityEvent(LivingEntity spawnAt, @Nullable ChangedEntity changedEntity) {
            this.spawnAt = spawnAt;
            this.changedEntity = changedEntity;
        }

        public LivingEntity getSpawnAt() {
            return spawnAt;
        }

        public @Nullable ChangedEntity getChangedEntity() {
            return changedEntity;
        }

        public void setChangedEntity(@Nullable ChangedEntity changedEntity) {
            this.changedEntity = changedEntity;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class KillAfterTransfurredSpecificEvent extends Event {

        private final LivingEntity targetEntity;
        protected IAbstractChangedEntity iAbstractChangedEntity;

        public KillAfterTransfurredSpecificEvent(LivingEntity targetEntity, @Nullable IAbstractChangedEntity iAbstractChangedEntity) {
            this.targetEntity = targetEntity;
            this.iAbstractChangedEntity = iAbstractChangedEntity;
        }

        public IAbstractChangedEntity getiAbstractChangedEntity() {
            return iAbstractChangedEntity;
        }

        public LivingEntity getTargetEntity() {
            return targetEntity;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }
}
