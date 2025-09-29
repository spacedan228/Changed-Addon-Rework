package net.foxyas.changedaddon.abilities;

import net.foxyas.changedaddon.entity.advanced.LuminaraFlowerBeastEntity;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.client.AbilityColors;
import net.ltxprogrammer.changed.client.gui.AbstractRadialScreen;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class PollenCarryAbility extends AbstractAbility<PollenCarryAbilityInstance> {

    public PollenCarryAbility() {
        super(PollenCarryAbilityInstance::new);
    }

    @Override
    public TranslatableComponent getAbilityName(IAbstractChangedEntity entity) {
        return new TranslatableComponent("changed_addon.ability.pollen_carry");
    }

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return List.of(new TranslatableComponent("changed_addon.ability.pollen_carry.description"));
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
