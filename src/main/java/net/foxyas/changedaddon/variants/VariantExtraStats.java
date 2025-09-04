package net.foxyas.changedaddon.variants;

import net.foxyas.changedaddon.procedures.CreatureDietsHandleProcedure.DietType;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.LatexBee;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

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

    default FlyType getFlyType() {
        return FlyType.BOTH;
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
}
