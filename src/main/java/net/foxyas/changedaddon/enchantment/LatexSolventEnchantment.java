package net.foxyas.changedaddon.enchantment;

import net.foxyas.changedaddon.init.ChangedAddonAttributes;
import net.foxyas.changedaddon.init.ChangedAddonEnchantments;
import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class LatexSolventEnchantment extends Enchantment {
    public LatexSolventEnchantment(EquipmentSlot... slots) {
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
        return super.canApplyAtEnchantingTable(itemstack)
                || itemstack.is(ChangedAddonTags.Items.LATEX_SOLVENT_APPLICABLE)
                || itemstack.getItem() instanceof SwordItem
                || itemstack.getItem() instanceof AxeItem;
    }

    @Mod.EventBusSubscriber
    public static class LatexSolventEnchantmentEventHandle {

        // UUID fixo para garantir que o atributo não duplique
        private static final UUID LATEX_SOLVENT_DAMAGE_UUID = UUID.fromString("abcde123-4567-890a-bcde-f1234567890a");

        @SubscribeEvent
        public static void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
            ItemStack stack = event.getItemStack();
            if (stack.isEmpty() || stack.is(Items.ENCHANTED_BOOK)) return;

            int level = EnchantmentHelper.getItemEnchantmentLevel(ChangedAddonEnchantments.LATEX_SOLVENT.get(), stack);
            if (level <= 0) return;

            // Só aplica se estiver na mão principal
            if (event.getSlotType() == EquipmentSlot.MAINHAND) {
                double bonus = 0.2 * level; // +20% de dano por nível

                // Cria o modificador
                AttributeModifier modifier = new AttributeModifier(
                        LATEX_SOLVENT_DAMAGE_UUID,
                        "Latex Solvent Bonus",
                        bonus,
                        AttributeModifier.Operation.ADDITION // multiplica o dano base
                );

                // Adiciona o bônus de ataque
                event.addModifier(ChangedAddonAttributes.LATEX_SOLVENT_DAMAGE_MULTIPLIER.get(), modifier);
            }
        }

        /*@SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
            ItemStack stack = event.getItemStack();
            List<Component> tooltip = event.getToolTip();

            int EnchantLevel = EnchantmentHelper.getItemEnchantmentLevel(ChangedAddonEnchantments.LATEX_SOLVENT.get(), stack);
            double math = 0 + EnchantLevel * 0.2;
            if (!(stack.getItem() instanceof BowItem) && !(stack.getItem() instanceof CrossbowItem)) {
                if (EnchantmentHelper.getItemEnchantmentLevel(ChangedAddonEnchantments.LATEX_SOLVENT.get(), stack) != 0) {
                    TextComponent spaceText = new TextComponent(" ");
                    String mathInText = "§r§e+" + String.format("%.2f", math * 100);
                    TextComponent latexSolventDamageInText = new TextComponent((mathInText + "%§r §nLatex Solvent Damage"));

                    int idx = -1;
                    for (int i = 0; i < tooltip.size(); i++) {
                        if (tooltip.get(i).toString().contains("item.modifiers.mainhand")) {
                            idx = i;
                            break;
                        }
                    }

                    if (idx != -1) {
                        tooltip.add(idx, spaceText.append(latexSolventDamageInText));
                    } else {
                        //Fall Back
                        if (Screen.hasShiftDown()) {
                            tooltip.add(new TextComponent(("§r§e+" + String.format("%.2f", math * 100) + "%§r §nLatex Solvent Damage")));
                        } else {
                            tooltip.add(new TextComponent("Press §e<Shift>§r for show tooltip"));
                        }
                    }
                }
            }
        }*/
    }
}
