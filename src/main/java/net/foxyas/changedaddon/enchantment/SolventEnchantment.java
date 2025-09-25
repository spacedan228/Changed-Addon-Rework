package net.foxyas.changedaddon.enchantment;

import net.foxyas.changedaddon.init.ChangedAddonEnchantments;
import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class SolventEnchantment extends Enchantment {
    public SolventEnchantment(EquipmentSlot... slots) {
        super(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON, slots);
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment enchantment) {
        return this != enchantment && !Objects.equals(Enchantments.SHARPNESS, enchantment);
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack itemstack) {
        return  super.canApplyAtEnchantingTable(itemstack)
                || itemstack.is(ChangedAddonTags.Items.LATEX_SOLVENT_APPLICABLE)
                || itemstack.getItem() instanceof SwordItem
                || itemstack.getItem() instanceof AxeItem;
    }

    @Mod.EventBusSubscriber
    public static class ShowSolventDmgProcedure {

        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
            ItemStack stack = event.getItemStack();
            List<Component> tooltip = event.getToolTip();

            int EnchantLevel = EnchantmentHelper.getItemEnchantmentLevel(ChangedAddonEnchantments.SOLVENT.get(), stack);
            double math = 0 + EnchantLevel * 0.2;
            if (!(stack.getItem() instanceof BowItem) && !(stack.getItem() instanceof CrossbowItem)) {
                if (EnchantmentHelper.getItemEnchantmentLevel(ChangedAddonEnchantments.SOLVENT.get(), stack) != 0) {
                    if (Screen.hasShiftDown()) {
                        tooltip.add(new TextComponent(("§r§e+" + String.format("%.2f", math * 100) + "%§r §nLatex Solvent Damage")));
                    } else {
                        tooltip.add(new TextComponent("Press §e<Shift>§r for show tooltip"));
                    }
                }
            }
        }
    }
}
