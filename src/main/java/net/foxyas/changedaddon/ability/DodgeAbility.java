package net.foxyas.changedaddon.ability;

import net.foxyas.changedaddon.ability.handle.CounterDodgeType;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class DodgeAbility extends AbstractAbility<DodgeAbilityInstance> {

    private DodgeAbilityInstance instance = null;

    public DodgeAbility() {
        super(DodgeAbilityInstance::new);
    }

    public DodgeAbility(int Dodges) {
        super((ab, ia) -> new DodgeAbilityInstance(ab, ia, Dodges));
    }

    public DodgeAbility(DodgeAbilityInstance.DodgeType dodgeType) {
        super((ab, ia) -> new DodgeAbilityInstance(ab, ia).withDodgeType(dodgeType));
    }

    public DodgeAbility(int Dodges, DodgeAbilityInstance.DodgeType dodgeType) {
        super((ab, ia) -> new DodgeAbilityInstance(ab, ia, Dodges).withDodgeType(dodgeType));
    }

    @Override
    public DodgeAbilityInstance makeInstance(IAbstractChangedEntity entity) {
        DodgeAbilityInstance dodgeAbilityInstance = super.makeInstance(entity);
        this.instance = dodgeAbilityInstance;
        return dodgeAbilityInstance;
    }

    @Override
    public Component getAbilityName(IAbstractChangedEntity entity) {
        return Component.translatable("changed_addon.ability.dodge");
    }

    public ResourceLocation getTexture(IAbstractChangedEntity entity) {
        return ResourceLocation.parse("changed_addon:textures/screens/dodge_ability.png");
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        if (this.instance != null && this.instance.getDodgeType() instanceof CounterDodgeType) {
            return UseType.INSTANT;
        }
        return UseType.HOLD;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        if (this.instance != null && this.instance.getDodgeType() instanceof CounterDodgeType) {
            return 90;
        }
        return super.getCoolDown(entity);
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);
        this.setDirty(entity);
    }

    @Override
    public void tick(IAbstractChangedEntity entity) {
        super.tick(entity);
        this.setDirty(entity);
    }

    @Override
    public void stopUsing(IAbstractChangedEntity entity) {
        super.stopUsing(entity);
        this.setDirty(entity);
    }
}
