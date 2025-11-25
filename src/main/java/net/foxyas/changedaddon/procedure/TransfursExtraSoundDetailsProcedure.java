package net.foxyas.changedaddon.procedure;

import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.foxyas.changedaddon.util.DelayedTask;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.foxyas.changedaddon.variant.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TransfursExtraSoundDetailsProcedure {

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        String text = event.getMessage().getString();
        if (text == null || !ProcessTransfur.isPlayerTransfurred(player)) return;

        ChangedAddonVariables.PlayerVariables vars = ChangedAddonVariables.nonNullOf(player);
        if(vars.actCooldown) return;

        Level level = player.level;
        TransfurVariant<?> var = ProcessTransfur.getPlayerTransfurVariant(player).getParent();
        if(var.is(ChangedTransfurVariants.LATEX_TIGER_SHARK)){
            if(text.contains("roar")) {
                level.playSound(null, player, ChangedSounds.TIGER_SHARK_ROAR.get(), SoundSource.HOSTILE, 5, 1);
                setCooldown(vars, player);
                return;
            }
        }

        if(var.is(ChangedAddonTransfurVariants.EXPERIMENT_009) || var.is(ChangedAddonTransfurVariants.EXPERIMENT_009_BOSS)){
            if(text.contains("roar")) {
                level.playSound(null, player, ChangedSounds.TIGER_SHARK_ROAR.get(), SoundSource.HOSTILE, 35, 1);
                setCooldown(vars, player);
                return;
            }
        }

        if(PlayerUtil.isCatTransfur(player)){
            if(text.contains("meow")){
                level.playSound(null, player, SoundEvents.CAT_AMBIENT, SoundSource.PLAYERS, 2, 1);
                setCooldown(vars, player);
                return;
            }

            if(text.contains("purreow")){
                level.playSound(null, player, SoundEvents.CAT_PURREOW, SoundSource.PLAYERS, 2, 1);
                setCooldown(vars, player);
                return;
            }

            if(text.contains("hiss")){
                level.playSound(null, player, SoundEvents.CAT_HISS, SoundSource.PLAYERS, 2, 1);
                setCooldown(vars, player);
                return;
            }

            if(text.contains("purr")){
                level.playSound(null, player, SoundEvents.CAT_PURR, SoundSource.PLAYERS, 2, 1);
                setCooldown(vars, player);
                return;
            }
        }

        if (PlayerUtil.isWolfTransfur(player)) {
            if (text.contains("growl")) {
                level.playSound(null, player, SoundEvents.WOLF_GROWL, SoundSource.PLAYERS, 2, 1);
                setCooldown(vars, player);
                return;
            }

            if (text.contains("bark")) {
                level.playSound(null, player, SoundEvents.WOLF_AMBIENT, SoundSource.PLAYERS, 2, 1);
                setCooldown(vars, player);
                return;
            }

            if (text.contains("howl")) {
                level.playSound(null, player, SoundEvents.WOLF_HOWL, SoundSource.PLAYERS, 2, 1);
                setCooldown(vars, player);
            }
        }
    }

    private static void setCooldown(ChangedAddonVariables.PlayerVariables vars, ServerPlayer player){
        vars.actCooldown = true;
        vars.syncPlayerVariables(player);

        DelayedTask.schedule(60, () -> {
            vars.actCooldown = false;
            vars.syncPlayerVariables(player);
        });
    }
}
