package net.foxyas.changedaddon.entity.advanced.handle;

import net.foxyas.changedaddon.entity.advanced.LuminaraFlowerBeastEntity;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.foxyas.changedaddon.variants.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class VoidTransformationHandler {

    /**
     * Check if player is in the void and trigger transformation if conditions are met.
     */
    public static void handlePlayerVoid(Player player) {
        if (player.getLevel().isClientSide) return;
        if (player.isSpectator()) return;

        // Check if player is under world min height (void)
        if (player.getY() < player.getLevel().getMinBuildHeight() - 4) {
            if (isVoidForm(player)) {
                triggerVoidTransformation(player);
            }
        }
    }

    /**
     * Cancel void damage if player is transformed.
     */
    public static void handleVoidDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.isSpectator()) return;

        // Only cancel OUT_OF_WORLD damage
        if (event.getSource() == DamageSource.OUT_OF_WORLD) {
            if (isVoidForm(player)) {
                event.setCanceled(true);
                triggerVoidTransformation(player);
            }
        }
    }

    /**
     * Main logic for the transformation effect:
     * - Transform player into void form
     * - Launch them upwards
     * - Give potion effects and flight
     * - Spawn explosion-like particles
     */
    public static void triggerVoidTransformation(Player player) {
        // Cancel fall/void velocity and launch player upwards
        player.setDeltaMovement(0, 8.0, 0); // strong vertical push
        player.hurtMarked = true; // force velocity update to client

        // Grant effects
        player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 20 * 10, 1));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 10, 2));

        // Enable flight
        player.getAbilities().mayfly = true;
        player.getAbilities().flying = true; // auto fly
        player.onUpdateAbilities();

        // Explosion-like particles
        if (player.getLevel() instanceof ServerLevel serverLevel) {
            double radius = 2f;
            double angle = 25f;
            for (double theta = 0; theta < 360; theta += angle) {
                double angleTheta = Math.toRadians(theta);
                for (double phi = 0; phi <= 180; phi += angle) {
                    double anglePhi = Math.toRadians(phi);
                    double x = player.getX() + Math.sin(anglePhi) * Math.cos(angleTheta) * radius;
                    double y = player.getY() + Math.cos(anglePhi) * radius;
                    double z = player.getZ() + Math.sin(anglePhi) * Math.sin(angleTheta) * radius;
                    Vec3 pos = new Vec3(x, y, z);
                    PlayerUtil.ParticlesUtil.sendParticlesWithMotion(
                            player.getLevel(),
                            ParticleTypes.PORTAL,
                            pos,
                            Vec3.ZERO,
                            pos.subtract(player.position()),
                            1, 1
                    );
                }
            }

            serverLevel.playSound(null, player.blockPosition(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 2.0F, 0.8F);

            TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
            if (instance != null && instance.getChangedEntity() instanceof LuminaraFlowerBeastEntity luminaraFlowerBeastEntity) {
                if (!luminaraFlowerBeastEntity.isAwakened()) {
                    luminaraFlowerBeastEntity.setAwakened(true);
                }
            }
        }
    }

    /**
     * Check if player is in the special void form.
     */
    public static boolean isVoidForm(Player player) {
        TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
        return instance != null
                && instance.is(ChangedAddonTransfurVariants.LUMINARA_FLOWER_BEAST.get())
                && instance.getChangedEntity() instanceof LuminaraFlowerBeastEntity luminaraFlowerBeastEntity
                && !luminaraFlowerBeastEntity.isAwakened();
    }
}
