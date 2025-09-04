package net.foxyas.changedaddon.abilities;

import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.foxyas.changedaddon.variants.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.ability.*;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class CarryAbility extends AbstractAbility<CarryAbilityInstance> {
    private CarryAbilityInstance abilityInstance;

    public CarryAbility() {
        super(CarryAbilityInstance::new);
    }

    @Override
    public TranslatableComponent getAbilityName(IAbstractChangedEntity entity) {
        return new TranslatableComponent("changed_addon.ability.carry");
    }

    public ResourceLocation getTexture(IAbstractChangedEntity entity) {
        return new ResourceLocation("changed_addon:textures/screens/carry_ability.png");
    }

    @Override
    public CarryAbilityInstance makeInstance(IAbstractChangedEntity entity) {
        abilityInstance = super.makeInstance(entity);
        return abilityInstance;
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.INSTANT;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 5;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        if (Spectator(entity.getEntity()))
            return false;
        Optional<TransfurVariant<?>> variant = Optional.ofNullable(entity.getTransfurVariantInstance()).map(TransfurVariantInstance::getParent);
        return variant.filter(
                        v -> v.is(ChangedAddonTransfurVariants.Gendered.EXP2.getFemaleVariant()) || v.is(ChangedAddonTransfurVariants.Gendered.EXP2.getMaleVariant()) || v.is(ChangedAddonTransfurVariants.Gendered.ORGANIC_SNOW_LEOPARD.getFemaleVariant())
                                || v.is(ChangedAddonTransfurVariants.Gendered.ORGANIC_SNOW_LEOPARD.getMaleVariant()) || v.is(ChangedAddonTransfurVariants.Gendered.PURO_KIND.getFemaleVariant())
                                || v.is(ChangedAddonTransfurVariants.Gendered.PURO_KIND.getMaleVariant()) || v.is(ChangedAddonTags.TransfurTypes.ABLE_TO_CARRY))
                .isPresent();
    }


    public static boolean Spectator(Entity entity) {
        return entity instanceof Player player && player.isSpectator();
    }

}
