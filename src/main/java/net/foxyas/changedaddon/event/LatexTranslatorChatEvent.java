package net.foxyas.changedaddon.event;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.foxyas.changedaddon.item.TranslatorItem;
import net.foxyas.changedaddon.process.features.LatexLanguageTranslator;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = ChangedAddonMod.MODID, value = Dist.CLIENT)
public class LatexTranslatorChatEvent {

    @SubscribeEvent
    public static void onClientChat(ClientChatReceivedEvent event) {
        if (!ChangedAddonServerConfiguration.TRANSFURED_PLAYERS_CHAT_IN_LATEX_LANGUAGE.get()) return;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        Player localPlayer = mc.player;

        if (level == null || localPlayer == null) return;

        UUID senderUUID = event.getSenderUUID();
        if (senderUUID == null) return;

        Entity senderEntity = PlayerUtil.GlobalEntityUtil.getEntityByUUID(level, senderUUID);
        if (!(senderEntity instanceof Player sender)) return;

        if (!isLatex(sender) || hasTranslator(sender)) return;

        if (canUnderstandLatex(localPlayer)) return;

        // Traduz apenas para ESTE client
        Component original = event.getMessage();
        String namePart = "<" + sender.getName().getString() + ">";
        String message = original.getString().replaceFirst(namePart, "");


        String translated = LatexLanguageTranslator.translateText(
                message,
                LatexLanguageTranslator.TranslationType.TO_LATEX_LANGUAGE
        );

        MutableComponent finalMessage = new TextComponent(namePart).append(new TextComponent(translated)).withStyle(original.getStyle());

        event.setMessage(finalMessage);
    }

    /* ===== helpers ===== */

    private static boolean canUnderstandLatex(Player player) {
        return isLatex(player) || hasTranslator(player);
    }

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

