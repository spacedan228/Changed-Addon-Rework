package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.StackUtil;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LatexTotemKeepConscienceProcedure {

    @SubscribeEvent
    public static void execute(ProcessTransfur.KeepConsciousEvent event) {
        if (event.shouldKeepConscious
                || event.player == null
                || !event.player.getInventory().contains(new ItemStack(ChangedAddonItems.TRANSFUR_TOTEM.get()))) return;

        if (ProcessTransfur.getPlayerTransfurVariant(event.player) != null && StackUtil.callStackContainsClass(WhiteLatexTransportInterface.class, 15)) return;

        event.shouldKeepConscious = true;
        if (event.player instanceof ServerPlayer serverPlayer) {
            TranslatableComponent text = new TranslatableComponent("changed_addon.latex_totem.tittle.text_1");
            TranslatableComponent text2 = new TranslatableComponent("changed_addon.latex_totem.tittle.text_2");
            serverPlayer.displayClientMessage(text, true);
            serverPlayer.sendMessage(text, ChatType.CHAT, serverPlayer.getUUID());
            serverPlayer.sendMessage(text2, ChatType.CHAT, serverPlayer.getUUID());
        }
    }
}