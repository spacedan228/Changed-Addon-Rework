package net.foxyas.changedaddon.abilities;

import net.foxyas.changedaddon.entity.advanced.LuminaraFlowerBeastEntity;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SimpleAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;

public class LuminarFireballAbility extends SimpleAbility {

    public LuminarFireballAbility() {
        super();
    }

    @Override
    public TranslatableComponent getAbilityName(IAbstractChangedEntity entity) {
        return new TranslatableComponent("changed_addon.ability.fireball");
    }

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        Collection<Component> description = new ArrayList<>(super.getAbilityDescription(entity));
        description.add(new TranslatableComponent("changed_addon.ability.fireball.desc"));
        return description;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return entity.getChangedEntity() instanceof LuminaraFlowerBeastEntity;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 80;
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        if(!(entity.getChangedEntity() instanceof LuminaraFlowerBeastEntity holder)) return;

        Level level = entity.getLevel();
        AbstractHurtingProjectile fireball;

        Vec3 view = holder.getLookAngle();

        if(holder.isAwakened()){
            if(holder.isCrouching()){
                fireball = new LargeFireball(level, holder, view.x, view.y, view.z, 1);
            } else fireball = new DragonFireball(level, holder, view.x, view.y, view.z);
        } else fireball = new SmallFireball(level, entity.getEntity(), view.x, view.y, view.z);

        level.playSound(null, holder, SoundEvents.GHAST_SHOOT, SoundSource.NEUTRAL, 1, 1);
        fireball.moveTo(holder.getX(), holder.getY() + 1, holder.getZ());
        level.addFreshEntity(fireball);
    }
}
