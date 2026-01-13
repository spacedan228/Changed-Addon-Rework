package net.foxyas.changedaddon.event;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.foxyas.changedaddon.item.TranslatorItem;
import net.foxyas.changedaddon.process.features.LatexLanguageTranslator;
import net.foxyas.changedaddon.util.ComponentUtil;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

        if (!isLatex(sender)) {
            return;
        }

        if (hasTranslator(sender)) {
            return;
        }

        event.setCanceled(true);

        for (Player receiver : sender.level.players()) {
            String finalMessage = message;

            if (!receiverCanUnderstand(receiver)) {
                finalMessage = LatexLanguageTranslator.translateText(
                        message,
                        LatexLanguageTranslator.TranslationType.TO_LATEX_LANGUAGE
                );
            }

            receiver.sendMessage(
                    ComponentUtil.literal(
                            "<" + sender.getName().getString() + "> " + finalMessage
                    ),
                    sender.getUUID()
            );
        }
    }

    private static boolean receiverCanUnderstand(Player receiver) {
        boolean receiverIsLatex = isLatex(receiver);
        boolean receiverHasTranslator = hasTranslator(receiver);
        return receiverIsLatex || receiverHasTranslator;
    }

    /* ===== helpers ===== */

    private static boolean isLatex(Player player) {
        return ProcessTransfur.isPlayerTransfurred(player);
    }

    private static boolean hasTranslator(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty()
                    && stack.getItem() instanceof TranslatorItem
                    && TranslatorItem.isEnabled(stack)) {
                return true;
            }
        }
        return false;
    }

}

