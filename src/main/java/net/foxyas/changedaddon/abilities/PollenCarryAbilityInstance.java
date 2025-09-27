package net.foxyas.changedaddon.abilities;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.nbt.CompoundTag;

public class PollenCarryAbilityInstance extends AbstractAbilityInstance {

    private int withPollenTicks;

    public PollenCarryAbilityInstance(AbstractAbility<PollenCarryAbilityInstance> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    public boolean canUse() {
        return this.ability.canUse(this.entity);
    }

    public boolean canKeepUsing() {
        return this.ability.canKeepUsing(this.entity);
    }

    public void startUsing() {
        withPollenTicks = 120;
    }

    public void tick() {
        this.ability.tick(this.entity);
    }

    public void stopUsing() {
        this.ability.stopUsing(this.entity);
    }

    public void onRemove() {
        this.ability.onRemove(this.entity);
    }

    @Override
    public void saveData(CompoundTag tag) {
        tag.putInt("withPollenTicks", withPollenTicks);
    }

    @Override
    public void readData(CompoundTag tag) {
        withPollenTicks = tag.getInt("withPollenTicks");
    }
}
