package net.foxyas.changedaddon.event;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.renderer.layers.features.SonarOutlineLayer;
import net.foxyas.changedaddon.process.sounds.BossMusicHandler;
import net.foxyas.changedaddon.util.TransfurVariantUtils;
import net.foxyas.changedaddon.variant.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.item.Syringe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = ChangedAddonMod.MODID, value = Dist.CLIENT)
public class ClientEvent {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().level != null) {
            BossMusicHandler.tick(Minecraft.getInstance().level);
            SonarOutlineLayer.SonarClientState.tick();
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        showExtraTransfurInfo(event.getEntity(), event.getItemStack(), event.getToolTip());
    }

    public static void showExtraTransfurInfo(@Nullable Player entity, ItemStack itemstack, List<Component> tooltip) {
        if (entity == null || itemstack == null || tooltip == null) return;

        if (!(itemstack.is(ChangedItems.LATEX_SYRINGE.get()) || itemstack.is(ChangedItems.LATEX_FLASK.get())
                || itemstack.is(ChangedItems.LATEX_TIPPED_ARROW.get()))) return;

        ResourceLocation loc = ResourceLocation.tryParse(itemstack.getOrCreateTag().getString("form"));
        if (loc == null) return;

        TransfurVariant<?> tf = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(loc);
        if (tf == null) return;

        //boolean hasInformantBlock = entity.getInventory().contains(new ItemStack(ChangedAddonModBlocks.INFORMANTBLOCK.get()));

//        if (hasInformantBlock || isCreative) {
//            if (hasInformantBlock && !Screen.hasShiftDown()) {
//                String variantName = Component.translatable(Syringe.getVariantDescriptionId(itemstack)).getString();
//                tooltip.add(Component.literal("Hold ").append(Component.literal("<Shift>").withStyle(style -> style.withColor(0xFFD700)))
//                        .append(" to show the stats of the " + variantName + " Transfur"));
//            }

        if (entity.isCreative()) {
            if (!Screen.hasShiftDown()) {
                String variantName = Component.translatable(Syringe.getVariantDescriptionId(itemstack)).getString();
                tooltip.add(Component.translatable("item.changed_addon.latex_syringe.tooltip", variantName));
            } else {
                int index = Math.min(tooltip.size(), 3);

                float extraHp = TransfurVariantUtils.GetExtraHp(tf, entity) / 2f;
                tooltip.add(index, Component.translatable("text.changed_addon.additionalHealth")
                        .append("")
                        .append(extraHp == 0
                                ? Component.literal("§7None§r")
                                : Component.literal((extraHp > 0 ? "§a+" : "§c") + extraHp + "§r"))
                        .append(Component.translatable("text.changed_addon.additionalHealth.Hearts")));

                index++;
                tooltip.add(index, Component.translatable("text.changed_addon.miningStrength", TransfurVariantUtils.getMiningStrength(tf)));

                index++;
                float landSpeed = TransfurVariantUtils.GetLandSpeed(tf, entity);
                float landSpeedPct = landSpeed == 0 ? 0 : (landSpeed - 1) * 100;
                tooltip.add(index, Component.translatable("text.changed_addon.land_speed")
                        .append("")
                        .append(landSpeedPct == 0
                                ? Component.literal("§7None§r")
                                : Component.literal((landSpeedPct > 0 ? "§a+" : "§c") + (int) landSpeedPct + "%")));

                index++;
                float swimSpeed = TransfurVariantUtils.GetSwimSpeed(tf, entity);
                float swimSpeedPct = swimSpeed == 0 ? 0 : (swimSpeed - 1) * 100;
                tooltip.add(index, Component.translatable("text.changed_addon.swim_speed")
                        .append("")
                        .append(swimSpeedPct == 0
                                ? Component.literal("§7None§r")
                                : Component.literal((swimSpeedPct > 0 ? "§a+" : "§c") + (int) swimSpeedPct + "%")));

                index++;
                float jumpStrength = TransfurVariantUtils.GetJumpStrength(tf);
                float jumpStrengthPct = jumpStrength == 0 ? 0 : (jumpStrength - 1) * 100;
                tooltip.add(index, Component.translatable("text.changed_addon.jumpStrength")
                        .append("")
                        .append(jumpStrengthPct == 0
                                ? Component.literal("§7None§r")
                                : Component.literal((jumpStrengthPct > 0 ? "§a+" : "§c") + (int) jumpStrengthPct + "%")));

                index++;
                tooltip.add(index, Component.translatable("text.changed_addon.canGlide/Fly")
                        .append("")
                        .append(TransfurVariantUtils.CanGlideAndFly(tf)
                                ? Component.literal("§aTrue§r")
                                : Component.literal("§cFalse§r")));
            }

            if (ChangedAddonTransfurVariants.isVariantOC(loc, entity.level())) {
                Component ocVariantComponent = ChangedAddonTransfurVariants.getOcVariantComponent(tf);
                MutableComponent append = Component.literal("§8OC Transfur");
                if (ocVariantComponent != null) append.append("\n").append(ocVariantComponent);
                tooltip.add(append);
            }
        }

        if (ChangedAddonTransfurVariants.isBossVariant(tf)) {
            tooltip.add(Component.literal("§8Boss Version"));
        }
    }
}
