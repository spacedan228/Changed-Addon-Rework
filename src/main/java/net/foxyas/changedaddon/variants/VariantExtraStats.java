package net.foxyas.changedaddon.variants;

import net.minecraft.nbt.CompoundTag;

public interface VariantExtraStats {

    // Variable Set By Entity
    float extraBlockBreakSpeed();

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

    enum FlyType {
        NONE,
        ONLY_FALL,
        ONLY_FLY,
        BOTH;

        FlyType(){
        }

        public boolean canGlide() {
            return this == ONLY_FALL || this == BOTH;
        }

        public boolean canFly() {
            return this == ONLY_FLY || this == BOTH;
        }
    }
}
