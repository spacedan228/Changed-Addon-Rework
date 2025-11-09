package net.foxyas.changedaddon.abilities.handle;

import net.foxyas.changedaddon.abilities.DodgeAbilityInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.jetbrains.annotations.Nullable;

import static net.foxyas.changedaddon.abilities.DodgeAbilityInstance.DodgeType;

public class CounterDodgeType extends DodgeType {

    public static final CounterDodgeType COUNTER = new CounterDodgeType();

    public CounterDodgeType() {
        super();
    }

    @Override
    public void runDodge(DodgeAbilityInstance dodgeAbilityInstance, LevelAccessor levelAccessor, LivingEntity dodger, Entity attacker, LivingAttackEvent event, double distance, Vec3 dodgePosBehind, boolean causeExhaustion) {
        //dodgeAbilityInstance.dodgeAwayFromAttacker(dodger, attacker);
        if (event != null) {
            event.setCanceled(true);
        }
    }

    @Override
    public void runDodgeEffects(DodgeAbilityInstance dodgeAbilityInstance, LevelAccessor levelAccessor, @Nullable LivingEntity dodger, @Nullable Entity attacker, DodgeType dodgeType, @Nullable LivingAttackEvent event, boolean causeExhaustion) {
        if (event != null) {
            event.setCanceled(true);
        }
        dodgeAbilityInstance.executeDodgeAnimations(levelAccessor, dodger);
        dodgeAbilityInstance.subDodgeAmount();
        if (dodger != null) {
            dodger.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 2, true, true));
        }
    }

    @Override
    public boolean shouldPlayDodgeAnimation() {
        return true;
    }
}