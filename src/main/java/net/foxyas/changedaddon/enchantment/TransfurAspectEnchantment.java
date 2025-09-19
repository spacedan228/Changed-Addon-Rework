package net.foxyas.changedaddon.enchantment;

import net.foxyas.changedaddon.init.ChangedAddonEnchantments;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TransfurAspectEnchantment extends Enchantment {

    public TransfurAspectEnchantment(EquipmentSlot... pApplicableSlots) {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, pApplicableSlots);
    }

    @Override
    public void doPostHurt(@NotNull LivingEntity pTarget, @NotNull Entity pAttacker, int pLevel) {
        super.doPostHurt(pTarget, pAttacker, pLevel);
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getDamageProtection(int pLevel, @NotNull DamageSource pSource) {
        return super.getDamageProtection(pLevel, pSource);
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack itemstack) {
        return super.canApplyAtEnchantingTable(itemstack)
                || itemstack.getItem() instanceof SwordItem
                || itemstack.getItem() instanceof AxeItem;
    }

    @Override
    public boolean canEnchant(@NotNull ItemStack itemstack) {
        return super.canEnchant(itemstack) // Default Behavior
                || itemstack.getItem() instanceof SwordItem
                || itemstack.getItem() instanceof AxeItem;
    }

    @Override
    public void doPostAttack(@NotNull LivingEntity pAttacker, @NotNull Entity pTarget, int pLevel) {
        super.doPostAttack(pAttacker, pTarget, pLevel);
        if (pAttacker.getLevel().isClientSide()) return;

        if (!(pAttacker instanceof Player player)) {
            if (pAttacker instanceof ChangedEntity changedEntity) {
                if (!(pTarget instanceof LivingEntity livingEntity)) return;

                TransfurVariant<?> variant = changedEntity.getSelfVariant();
                if (variant == null) return;

                // Only works if the variant is latex-based
                boolean isLatex = changedEntity.getType().is(ChangedTags.EntityTypes.LATEX);
                if (!isLatex) return;

                // Context requires the changed entity representation
                IAbstractChangedEntity iAbstractChangedEntity = IAbstractChangedEntity.forEntity(changedEntity);

                // Pick a random transfur cause
                TransfurCause[] causes = TransfurCause.values();
                TransfurCause randomCause = causes[changedEntity.getRandom().nextInt(causes.length)];

                // Build context with random cause
                TransfurContext context = new TransfurContext(randomCause, iAbstractChangedEntity);

                // Calculate transfur damage
                float transfurDamage = getTransfurDamage(changedEntity, livingEntity, pLevel);
                if (transfurDamage <= 0) return;

                // Apply transfur progress
                ProcessTransfur.progressTransfur(livingEntity, transfurDamage, variant, context);
                return;
            }
            return;
        }


        if (!(pTarget instanceof LivingEntity livingEntity)) return;

        // Get player transfur variant
        TransfurVariantInstance<?> variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (variant == null) return;

        // Only works if the variant is latex-based
        boolean isLatex = variant.getChangedEntity().getType().is(ChangedTags.EntityTypes.LATEX);
        if (!isLatex) return;

        // Context requires the changed entity representation
        IAbstractChangedEntity changedEntity = IAbstractChangedEntity.forPlayer(player);

        // Pick a random transfur cause
        TransfurCause[] causes = TransfurCause.values();
        TransfurCause randomCause = causes[player.getRandom().nextInt(causes.length)];

        // Build context with random cause
        TransfurContext context = new TransfurContext(randomCause, changedEntity);

        // Calculate transfur damage
        float transfurDamage = getTransfurDamage(player, livingEntity, pLevel);
        if (transfurDamage <= 0) return;

        // Apply transfur progress
        ProcessTransfur.progressTransfur(livingEntity, transfurDamage, variant.getParent(), context);
    }

    @Mod.EventBusSubscriber
    public static class ShowTransfurDmg {

        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
            ItemStack stack = event.getItemStack();
            List<Component> tooltip = event.getToolTip();

            // Get the Transfur Aspect enchantment level from the item
            int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(
                    ChangedAddonEnchantments.TRANSFUR_ASPECT.get(), stack
            );
            if (enchantLevel <= 0) return; // Exit if the item doesn't have the enchantment

            // Display detailed tooltip only when Shift is held
            if (Screen.hasShiftDown()) {
                // Use base value of 1.0 here; can be dynamically adjusted for the actual player stats
                double baseValue = getTransfurDamage(event.getEntityLiving(), null, enchantLevel);

                // Calculate the Transfur Damage using the same formula as in doPostAttack
                double damage = baseValue * 0.75 * enchantLevel / 4.0;

                // Add tooltip showing the damage
                tooltip.add(new TextComponent("§r§e+" + String.format("%.2f", damage) + "§r Transfur Damage to Humanoids"));
            } else {
                // If Shift not held, show hint
                tooltip.add(new TextComponent("Press §e<Shift>§r for show tooltip"));
            }
        }
    }


    public static float getTransfurDamage(@NotNull LivingEntity attacker, @Nullable Entity target, int enchantLevel) {
        AttributeInstance instance = attacker.getAttribute(ChangedAttributes.TRANSFUR_DAMAGE.get());
        if (instance == null) return 0f;

        double baseValue = instance.getValue();

        // Formula notes:
        //   - Divide by 4.0 to keep numbers small
        //   - Multiply by enchant level
        //   - Extra multiplier (0.75) to soften scaling
        return (float) (baseValue * 0.75f * enchantLevel / 4.0);
    }

}
