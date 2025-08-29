package net.foxyas.changedaddon;

import net.foxyas.changedaddon.util.PlayerUtil;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SimpleAbility;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class DashAbility extends SimpleAbility {

    public DashAbility() {
        super();
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return true;
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.CHARGE_RELEASE;
    }

    @Override
    public int getChargeTime(IAbstractChangedEntity entity) {
        return 2;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 15;
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);
        LivingEntity livingEntity = entity.getEntity();
        double speed = 1;
        Vec3 motion = livingEntity.getViewVector(1).multiply(speed, speed * 0.5f, speed);
        livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(motion));
        playEffects(livingEntity, motion);
        if (livingEntity instanceof Player player) exhaustPlayer(player, 0.5F);
    }

    private static void playEffects(LivingEntity player, Vec3 motion) {
        if (!player.level.isClientSide()) {
            player.level.playSound(null, player.blockPosition(), ChangedSounds.BOW2,
                    player.getSoundSource(), 2.5F, 1.0F);
            if (player.getLevel() instanceof ServerLevel serverLevel) {
                PlayerUtil.ParticlesUtil.sendParticles(serverLevel, ParticleTypes.POOF, player.getEyePosition(), (float) motion.x(), (float) motion.y(), (float) motion.z(), 0, 1);
            }
        }
    }

    private static void exhaustPlayer(Player player, float exhaustion) {
        if (!player.isCreative()) {
            player.causeFoodExhaustion(exhaustion);
        }
    }
}
