package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class SignalCatherToolTipProcedure {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();

        double deltaZ;
        double distance;
        double deltaX;
        double deltaY;

        if (stack.getItem() == ChangedAddonItems.SIGNAL_CATCHER.get()) {
            deltaX = stack.getOrCreateTag().getDouble("x") - player.getX();
            deltaY = stack.getOrCreateTag().getDouble("y") - player.getY();
            deltaZ = stack.getOrCreateTag().getDouble("z") - player.getZ();
            distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (!Screen.hasShiftDown()) {
                tooltip.add(new TextComponent("Hold §6<Shift>§r for Info"));
            } else {
                tooltip.add(new TextComponent("Hold §b<Right Click>§r For scan a 32 blocks area"));
                tooltip.add(new TextComponent("Hold §c<Shift + Right Click>§r For Super scan and scan a 120 blocks area"));
            }
            tooltip.add(new TextComponent(("§oCords §l" + stack.getOrCreateTag().getDouble("x") + " " + stack.getOrCreateTag().getDouble("y") + " " + stack.getOrCreateTag().getDouble("z"))));
            if (stack.getOrCreateTag().getBoolean("set")) {
                tooltip.add(new TextComponent(("§oDistance §l" + Math.round(distance))));
            }
        }
    }
}
