package net.foxyas.changedaddon.variants;

import net.foxyas.changedaddon.entity.api.LivingEntityDataExtensor;
import net.foxyas.changedaddon.entity.customHandle.AttributesHandle;
import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.foxyas.changedaddon.procedures.CreatureDietsHandleProcedure.DietType;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import java.util.List;

public interface VariantExtraStats {

    // Variable Set By Entity
    default float extraBlockBreakSpeed() {
        return 0;
    }

    // Multiplier Based on % amount [Vanilla Attribute Style]
    default float getBlockBreakSpeedMultiplier() {
        return this.extraBlockBreakSpeed() + 1;
    }

    float defaultPlayerFlySpeed = AttributesHandle.DefaultPlayerFlySpeed;

    default float getFlySpeed() {
        return 0;
    }

    default FlyType getFlyType() {
        if (this instanceof ChangedEntity changedEntity) {
            if (changedEntity.getSelfVariant() != null) {
                var variant = changedEntity.getSelfVariant();
                return variant.canGlide ? FlyType.BOTH : FlyType.NONE;
            }
        }

        return FlyType.NONE;
    }

    default void readExtraData(CompoundTag tag) {
    }

    default void saveExtraData(CompoundTag tag) {
    }

    default List<DietType> getExtraDietTypes() {
        return List.of();
    }

    enum FlyType {
        NONE,
        ONLY_FALL,
        ONLY_FLY,
        BOTH;

        FlyType() {
        }

        public boolean canGlide() {
            return this == ONLY_FALL || this == BOTH;
        }

        public boolean canFly() {
            return this == ONLY_FLY || this == BOTH;
        }
    }

    default boolean variantOverrideSwim() {
        return false;
    }

    default boolean variantOverrideSwimUpdate() {
        return false;
    }

    default boolean variantOverrideIsInWater() {
        return false;
    }
}
