package net.foxyas.changedaddon.event;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.foxyas.changedaddon.process.features.LatexLanguageTranslator;
import net.foxyas.changedaddon.util.ComponentUtil;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedAddonMod.MODID)
public class ServerEvent {

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        if (!ChangedAddonServerConfiguration.TRANSFURED_PLAYERS_CHAT_IN_LATEX_LANGUAGE.get()) return;

        ServerPlayer sender = event.getPlayer();
        String message = event.getMessage();

        for (Player receiver : sender.level.players()) {

            String finalMessage = message;

            if (shouldTranslateTo(sender, receiver)) {
                finalMessage = LatexLanguageTranslator.translateText(
                        message,
                        LatexLanguageTranslator.TranslationType.TO
                );
            }
            else if (shouldTranslateFrom(sender, receiver)) {
                finalMessage = LatexLanguageTranslator.translateText(
                        message,
                        LatexLanguageTranslator.TranslationType.FROM
                );
            }

            receiver.sendMessage(
                    ComponentUtil.literal("<" + sender.getName().getString() + "> " + finalMessage),
                    sender.getUUID()
            );
        }

        // Cancela o broadcast vanilla
        event.setCanceled(true);
    }

    /* ===== helpers ===== */

    private static boolean isLatex(Player player) {
        return ProcessTransfur.isPlayerTransfurred(player);
    }

    private static boolean hasTranslator(Player player) {
        return player.getInventory().contains(Items.DEBUG_STICK.getDefaultInstance()); //.contains(new ItemStack(ChangedAddonItems.TRANSLATOR.get()));
    }

    private static boolean shouldTranslateTo(Player sender, Player receiver) {
        return isLatex(sender)
                && !hasTranslator(sender)
                && !isLatex(receiver)
                && !hasTranslator(receiver);
    }

    private static boolean shouldTranslateFrom(Player sender, Player receiver) {
        return !isLatex(sender)
                && !hasTranslator(sender)
                && isLatex(receiver)
                && !hasTranslator(receiver);
    }
}

