package net.foxyas.changedaddon.abilities;

import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.foxyas.changedaddon.variants.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class CarryAbility extends AbstractAbility<CarryAbilityInstance> {

    public CarryAbility() {
        super((a, b) -> new CarryAbilityInstance((CarryAbility) a, b));
    }

    public static void SafeRemove(Entity mainEntity) {
        if (mainEntity.getFirstPassenger() != null) {
            mainEntity.getFirstPassenger().stopRiding();
            broadcastPassengers(mainEntity.getFirstPassenger());
        }
    }

    @Override
    public TranslatableComponent getAbilityName(IAbstractChangedEntity entity) {
        return new TranslatableComponent("changed_addon.ability.carry");
    }

    public ResourceLocation getTexture(IAbstractChangedEntity entity) {
        return new ResourceLocation("changed_addon:textures/screens/carry_ability.png");
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.INSTANT;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 5;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        if (!(entity.getEntity() instanceof Player player) || player.isSpectator())
            return false;
        Optional<TransfurVariant<?>> variant = Optional.ofNullable(entity.getTransfurVariantInstance()).map(TransfurVariantInstance::getParent);
        return variant.filter(
                        v -> v.is(ChangedAddonTransfurVariants.Gendered.EXP2.getFemaleVariant()) || v.is(ChangedAddonTransfurVariants.Gendered.EXP2.getMaleVariant()) || v.is(ChangedAddonTransfurVariants.Gendered.ORGANIC_SNOW_LEOPARD.getFemaleVariant())
                                || v.is(ChangedAddonTransfurVariants.Gendered.ORGANIC_SNOW_LEOPARD.getMaleVariant()) || v.is(ChangedAddonTransfurVariants.Gendered.PURO_KIND.getFemaleVariant())
                                || v.is(ChangedAddonTransfurVariants.Gendered.PURO_KIND.getMaleVariant()) || v.is(ChangedAddonTags.TransfurTypes.ABLE_TO_CARRY))
                .isPresent();
    }

    public static Entity CarryTarget(Player player) {
        return PlayerUtil.getEntityPlayerLookingAt(player, 4);
    }

    public static boolean isPossibleToCarry(LivingEntity entity) {
        if (entity instanceof Player player) {
            var variant = ProcessTransfur.getPlayerTransfurVariant(player);
            if (variant != null) {
                var ability = variant.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
                if (ability != null
                        && ability.suited
                        && ability.grabbedHasControl) {
                    return false;
                }
            }
        }
        return GrabEntityAbility.getGrabber(entity) == null;
    }

    // === Helpers de rede (broadcast correto) ===
    protected static void broadcastPassengers(Entity vehicle) {
        if (!vehicle.level.isClientSide) {
            ServerLevel sl = (ServerLevel) vehicle.level;
            sl.getChunkSource().broadcastAndSend(vehicle, new ClientboundSetPassengersPacket(vehicle));
        }
    }
}
