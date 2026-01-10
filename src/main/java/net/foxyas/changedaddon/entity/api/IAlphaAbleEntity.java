package net.foxyas.changedaddon.entity.api;

import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;

public interface IAlphaAbleEntity {

    EntityDataAccessor<Boolean> IS_ALPHA = SynchedEntityData.defineId(ChangedEntity.class, EntityDataSerializers.BOOLEAN);
    EntityDataAccessor<Float> ALPHA_SCALE = SynchedEntityData.defineId(ChangedEntity.class, EntityDataSerializers.FLOAT);

    void setAlpha(boolean alphaGene);

    boolean isAlpha();

    void setAlphaScale(float scale);

    default float chanceToSpawnAsAlpha() {
        if (this instanceof ChangedEntity changedEntity) {
            boolean cantSpawn = changedEntity.getType().is(ChangedAddonTags.EntityTypes.CANT_SPAWN_AS_ALPHA_ENTITY);
            if (cantSpawn) return 0f;

            Level level = changedEntity.level;
            Difficulty difficulty = level.getDifficulty();
            if (level.getLevelData().isHardcore()) return ChangedAddonServerConfiguration.ALPHA_SPAWN_HARDCORE.get().floatValue();

            return switch (difficulty) {
                case PEACEFUL -> ChangedAddonServerConfiguration.ALPHA_SPAWN_PEACEFUL.get().floatValue();
                case EASY -> ChangedAddonServerConfiguration.ALPHA_SPAWN_EASY.get().floatValue();
                case NORMAL -> ChangedAddonServerConfiguration.ALPHA_SPAWN_NORMAL.get().floatValue();
                case HARD -> ChangedAddonServerConfiguration.ALPHA_SPAWN_HARD.get().floatValue();
            };
        }

        return 0.025f; //Fail Safe
    }

    default float alphaCameraOffset() {
        if (isAlpha()) return alphaScaleForRender() / 2f;
        return 0;
    }

    default float alphaScaleForRender() {
        if (this instanceof ChangedEntity changedEntity) {
            return 1 + alphaAdditionalScale(); // For future changes
        }
        return 1f;
    }

    default float alphaAdditionalScale() {
        if (this instanceof ChangedEntity changedEntity) {
            SynchedEntityData entityData = changedEntity.getEntityData();
            return entityData.hasItem(ALPHA_SCALE) ? entityData.get(ALPHA_SCALE) : 0.75f; // For future changes
        }
        return 0f;
    }

}
