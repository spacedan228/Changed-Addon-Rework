package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ShowTransfurTotemItemtipProcedure {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();

        if (stack.getItem() == ChangedAddonItems.TRANSFUR_TOTEM.get()) {
            if ((stack.getOrCreateTag().getString("form")).isEmpty()) {
                tooltip.add(new TextComponent("§6No Form Linked"));
            } else {
                if (Screen.hasShiftDown() && !Screen.hasAltDown() && !Screen.hasControlDown()) {
                    tooltip.add(new TextComponent(("§6Form=" + stack.getOrCreateTag().getString("form"))));
                } else if (Screen.hasAltDown() && Screen.hasControlDown()) {
                    tooltip.add(new TextComponent((new TranslatableComponent("item.changed_addon.transfur_totem.desc_1").getString())));
                } else {
                    String ID = net.ltxprogrammer.changed.item.Syringe.getVariantDescriptionId(stack);
                    tooltip.add(new TextComponent(("§6(" + new TranslatableComponent(ID).getString() + ")")));
                }
            }
        }
    }
}
