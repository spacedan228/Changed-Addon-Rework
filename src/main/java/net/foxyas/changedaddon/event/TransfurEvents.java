package net.foxyas.changedaddon.event;

import net.foxyas.changedaddon.init.ChangedAddonGameRules;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.foxyas.changedaddon.variant.ChangedAddonTransfurVariants;
import net.foxyas.changedaddon.variant.TransfurVariantInstanceExtensor;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TransfurEvents {

    @SubscribeEvent
    public static void WhenTransfured(ProcessTransfur.EntityVariantAssigned changedVariantEvent) {
        TransfurVariant<?> variant = changedVariantEvent.originalVariant;
        if (variant == null) return;

        LivingEntity entity = changedVariantEvent.livingEntity;
        if (!entity.level.getLevelData().getGameRules().getBoolean(ChangedAddonGameRules.NEED_PERMISSION_FOR_BOSS_TRANSFUR))
            return;

        if (variant.is(ChangedAddonTransfurVariants.EXPERIMENT_009_BOSS) && !getPlayerVars(entity).Exp009TransfurAllowed) {
            changedVariantEvent.variant = ChangedAddonTransfurVariants.EXPERIMENT_009.get();
        }
        if (variant.is(ChangedAddonTransfurVariants.EXPERIMENT_10_BOSS) && !getPlayerVars(entity).Exp10TransfurAllowed) {
            changedVariantEvent.variant = ChangedAddonTransfurVariants.EXPERIMENT_10.get();
        }
    }


    @SubscribeEvent
    public static void CancelUntransfur(UntransfurEvent untransfurEvent) {
        Player player = untransfurEvent.getPlayer();
        if (ProcessTransfur.getPlayerTransfurVariant(player) instanceof TransfurVariantInstanceExtensor transfurVariantInstanceExtensor) {
            untransfurEvent.setCanceled(transfurVariantInstanceExtensor.getUntransfurImmunity(untransfurEvent.untransfurType));
        }
    }

    public static ChangedAddonVariables.PlayerVariables getPlayerVars(LivingEntity entity) {
        return entity.getCapability(ChangedAddonVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new ChangedAddonVariables.PlayerVariables());
    }
}
