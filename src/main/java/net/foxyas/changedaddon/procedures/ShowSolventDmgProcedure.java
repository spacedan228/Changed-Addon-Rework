package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.init.ChangedAddonEnchantments;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class ShowSolventDmgProcedure {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();

        int EnchantLevel = EnchantmentHelper.getItemEnchantmentLevel(ChangedAddonEnchantments.SOLVENT.get(), stack);
        double math = 0 + EnchantLevel * 0.2;
        if (!(stack.getItem() instanceof BowItem) && !(stack.getItem() instanceof CrossbowItem)) {
            if (EnchantmentHelper.getItemEnchantmentLevel(ChangedAddonEnchantments.SOLVENT.get(), stack) != 0) {
                if (Screen.hasShiftDown()) {
                    tooltip.add(new TextComponent(("§r§e+" + math * 100 + "%§r §nLatex Solvent Damage")));
                } else {
                    tooltip.add(new TextComponent("Press §e<Shift>§r for show tooltip"));
                }
            }
        }
    }
}
