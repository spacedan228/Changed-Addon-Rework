package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class SprayDurabilityProcedure {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();

        if (stack.getItem() == ChangedAddonItems.LITIX_CAMONIA_SPRAY.get()) {
            tooltip.add(new TextComponent(((stack).getDamageValue() + "/" + (stack).getMaxDamage() + " Uses")));
        }
        if (stack.getItem() == ChangedAddonItems.DARK_LATEX_SPRAY.get()) {
            tooltip.add(new TextComponent(((stack).getDamageValue() + "/" + (stack).getMaxDamage() + " Uses")));
        }
        if (stack.getItem() == ChangedAddonItems.WHITE_LATEX_SPRAY.get()) {
            tooltip.add(new TextComponent(((stack).getDamageValue() + "/" + (stack).getMaxDamage() + " Uses")));
        }
    }
}
