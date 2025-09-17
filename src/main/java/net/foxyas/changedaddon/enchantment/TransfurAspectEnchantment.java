package net.foxyas.changedaddon.enchantment;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class TransfurAspectEnchantment extends Enchantment {

    public TransfurAspectEnchantment(EquipmentSlot... pApplicableSlots) {
        super(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON, pApplicableSlots);
    }

    @Override
    public void doPostHurt(LivingEntity pTarget, Entity pAttacker, int pLevel) {
        super.doPostHurt(pTarget, pAttacker, pLevel);
    }

    @Override
    public void doPostAttack(LivingEntity pAttacker, Entity pTarget, int pLevel) {
        super.doPostAttack(pAttacker, pTarget, pLevel);
        if (pAttacker instanceof Player player) {
            TransfurVariantInstance<?> variant = ProcessTransfur.getPlayerTransfurVariant(player);
            if (variant != null) {
                boolean isLatex = variant.getChangedEntity().getType().is(ChangedTags.EntityTypes.LATEX);
                if (isLatex && pTarget instanceof LivingEntity livingEntity) {
                    IAbstractChangedEntity iAbstractChangedEntity = IAbstractChangedEntity.forPlayer(player);
                    TransfurCause[] causes = TransfurCause.values();
                    TransfurCause randomCause = causes[player.getRandom().nextInt(causes.length)];
                    TransfurContext transfurContext = new TransfurContext(randomCause, iAbstractChangedEntity);
                    ProcessTransfur.progressTransfur(livingEntity, pLevel, variant.getParent(), transfurContext);
                }
            }
        }
    }
}
