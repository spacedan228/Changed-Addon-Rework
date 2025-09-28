package net.foxyas.changedaddon.abilities;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.minecraft.network.chat.TranslatableComponent;

public class PollenCarryAbility extends AbstractAbility<PollenCarryAbilityInstance> {

    public PollenCarryAbility() {
        super(PollenCarryAbilityInstance::new);
    }

    @Override
    public TranslatableComponent getAbilityName(IAbstractChangedEntity entity) {
        return new TranslatableComponent("changed_addon.ability.pollen_carry");
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        if(entity.getEntity().isSpectator() || entity.getTransfurVariant() == null) return false;

        return entity.getTransfurVariant().is(ChangedTransfurVariants.LATEX_BEE);
    }

    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.CHARGE_RELEASE;
    }
}
