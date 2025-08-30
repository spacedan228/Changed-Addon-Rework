package net.foxyas.changedaddon.abilities;

import com.mojang.math.Vector3f;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SimpleAbility;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collection;

public class WindPassiveAbility extends SimpleAbility {
    public boolean isActive = false;

    public WindPassiveAbility() {
        super();
    }

    public static void spawnAirParticles(LivingEntity livingEntity) {
        PlayerUtil.ParticlesUtil.sendParticles(livingEntity.getLevel(),
                new DustParticleOptions(
                        new Vector3f(
                                1.2f,
                                1.2f,
                                1.2f),
                        1f),
                livingEntity.position(), 0, 0.25f, 0, 0, 1);
    }

    @Override
    public void saveData(CompoundTag tag, IAbstractChangedEntity entity) {
        super.saveData(tag, entity);
        tag.putBoolean("isActive", isActive);
    }

    @Override
    public void readData(CompoundTag tag, IAbstractChangedEntity entity) {
        super.readData(tag, entity);
        if (tag.contains("isActive")) {
            this.isActive = tag.getBoolean("isActive");
        }
    }

    @Override
    public Component getAbilityName(IAbstractChangedEntity entity) {
        return new TranslatableComponent("changed_addon.ability.wind_control_passive");
    }

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        Collection<Component> list = new ArrayList<>(super.getAbilityDescription(entity));
        list.add(new TranslatableComponent("changed_addon.ability.wind_control_passive.desc"));
        return list;
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.MENU;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 5;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return entity.getTransfurVariantInstance() != null;
    }

    @Override
    public boolean canKeepUsing(IAbstractChangedEntity entity) {
        return entity.getTransfurVariantInstance() != null;
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        if (!entity.getLevel().isClientSide()) {
            TurnOnPassive(entity);
            this.setDirty(entity);
        }
    }

    public void TurnOnPassive(IAbstractChangedEntity entity) {
        this.isActive = !this.isActive;
        entity.displayClientMessage(this.isActive ?
                        new TranslatableComponent("changed_addon.ability.passive.toggle.on") :
                        new TranslatableComponent("changed_addon.ability.passive.toggle.off")
                , true);
    }
}
