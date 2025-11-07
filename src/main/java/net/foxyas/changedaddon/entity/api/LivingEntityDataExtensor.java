package net.foxyas.changedaddon.entity.api;

import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

public interface LivingEntityDataExtensor {

    @Nullable
    static LivingEntityDataExtensor ofEntity(LivingEntity entity) {
        return (entity instanceof LivingEntityDataExtensor livingEntityDataExtensor) ? livingEntityDataExtensor : null;
    }

    default boolean canOverrideSwim() {
        if (this instanceof Player player) {
            return isEyeOnLavaWithTransfurAndFireResistance(player);
        }
        return false;
    }

    default boolean canOverrideSwimUpdate() {
        if (this instanceof Player player) {
            return isEyeOnLavaWithTransfurAndFireResistance(player);
        }
        return false;
    }

    default boolean canOverrideIsInWater() {
        if (this instanceof Player player) {
            return isOnLavaWithTransfurAndFireResistance(player);
        }

        return false;
    }


    // Utils

    private boolean isEyeOnLavaWithTransfurAndFireResistance(Player player) {
        TransfurVariantInstance<?> transfurVariant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (transfurVariant != null && (player.hasEffect(MobEffects.FIRE_RESISTANCE) && player.isEyeInFluid(FluidTags.LAVA))) {
            boolean aquaticLike = transfurVariant.getParent().is(ChangedAddonTags.TransfurTypes.AQUATIC_LIKE);
            boolean fastSwimSpeed = transfurVariant.getChangedEntity().getAttributeValue(ForgeMod.SWIM_SPEED.get()) > 1;
            boolean aquaticBreath = transfurVariant.getParent().breatheMode.canBreatheWater();
            boolean aquaticAffinity = transfurVariant.getParent().breatheMode.hasAquaAffinity();

            return aquaticLike || fastSwimSpeed || aquaticBreath || aquaticAffinity;
        }

        return false;
    }

    private boolean isOnLavaWithTransfurAndFireResistance(Player player) {
        TransfurVariantInstance<?> transfurVariant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (transfurVariant != null && (player.hasEffect(MobEffects.FIRE_RESISTANCE) && player.getLevel().getFluidState(player.blockPosition()).is(FluidTags.LAVA))) {
            boolean aquaticLike = transfurVariant.getParent().is(ChangedAddonTags.TransfurTypes.AQUATIC_LIKE);
            boolean fastSwimSpeed = transfurVariant.getChangedEntity().getAttributeValue(ForgeMod.SWIM_SPEED.get()) > 1;
            boolean aquaticBreath = transfurVariant.getParent().breatheMode.canBreatheWater();
            boolean aquaticAffinity = transfurVariant.getParent().breatheMode.hasAquaAffinity();

            return aquaticLike || fastSwimSpeed || aquaticBreath || aquaticAffinity;
        }

        return false;
    }
}
