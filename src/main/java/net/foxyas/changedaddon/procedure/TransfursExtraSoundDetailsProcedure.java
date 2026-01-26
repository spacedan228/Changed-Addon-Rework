package net.foxyas.changedaddon.procedure;

import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.foxyas.changedaddon.util.DelayedTask;
import net.foxyas.changedaddon.variant.TransfurSoundsDetails;
import net.foxyas.changedaddon.variant.TransfurSoundsDetails.TransfurSoundAction;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TransfursExtraSoundDetailsProcedure {

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        String text = event.getMessage();

        if (!ProcessTransfur.isPlayerTransfurred(player)) return;

        ChangedAddonVariables.PlayerVariables vars = ChangedAddonVariables.nonNullOf(player);
        if (vars.actCooldown) return;

        for (TransfurSoundAction action : TransfurSoundAction.values()) {

            if (!action.matchesChat(text)) continue;
            if (!action.canUse(player)) continue;

            SoundEvent sound = TransfurSoundsDetails.getSoundFor(player, action);
            if (sound == null) continue;

            player.level.playSound(
                    null,
                    player,
                    sound,
                    SoundSource.PLAYERS,
                    2f,
                    1f
            );

            applyCooldown(vars, player, action.getCooldown());
            return; // só um som por mensagem
        }
    }

    private static void applyCooldown(
            ChangedAddonVariables.PlayerVariables vars,
            ServerPlayer player,
            int ticks
    ) {
        vars.actCooldown = true;
        vars.syncPlayerVariables(player);

        DelayedTask.schedule(ticks, () -> {
            vars.actCooldown = false;
            vars.syncPlayerVariables(player);
        });
    }
}
