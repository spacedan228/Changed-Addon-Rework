package net.foxyas.changedaddon.entity.api;

import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;

public interface IAlphaAbleEntity {

    EntityDataAccessor<Boolean> IS_ALPHA = SynchedEntityData.defineId(ChangedEntity.class, EntityDataSerializers.BOOLEAN);

    void setAlpha(boolean alphaGene);

    boolean isAlpha();

    default float chanceToSpawnAsAlpha() {
        if (this instanceof ChangedEntity changedEntity) {
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
        if (isAlpha()) return alphaScaleForRender() - 1.25f;
        return 0;
    }

    default float alphaScaleForRender() {
        if (this instanceof ChangedEntity changedEntity) {
            return 1.75f; // For future changes
        }
        return 1f;
    }

    default float alphaAdditionalScale() {
        if (this instanceof ChangedEntity changedEntity) {
            return 0.75f; // For future changes
        }
        return 0f;
    }

}
