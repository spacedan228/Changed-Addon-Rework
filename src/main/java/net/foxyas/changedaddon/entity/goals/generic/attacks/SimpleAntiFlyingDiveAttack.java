package net.foxyas.changedaddon.entity.goals.generic.attacks;

import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SimpleAntiFlyingDiveAttack extends Goal {

    private final Mob attacker;
    private final IntProvider cooldownProvider;

    private LivingEntity target;

    private int cooldown;
    private int ticks;

    private final float minRange;
    private final float maxRange;
    private final float damage;
    private final int delay;

    /* Dive system */
    private boolean jumpingUp;
    private boolean divingDown;

    private Vec3 diveEndPos = Vec3.ZERO;

    /* Configs */
    private static final double DIVE_TRIGGER_DISTANCE = 8.0;
    private static final double INITIAL_JUMP_Y = 1.1;
    private static final double PULL_UP_SPEED = 0.35;
    private static final double PULL_HORIZONTAL = 0.25;
    private static final double REQUIRED_HEIGHT_ABOVE_TARGET = 2.2;

    private static final double DIVE_SPEED = 1.6;
    private static final double DIVE_DOWN = -2.2;

    public SimpleAntiFlyingDiveAttack(
            Mob attacker,
            IntProvider cooldownProvider,
            float minRange,
            float maxRange,
            float damage,
            int delay
    ) {
        this.attacker = attacker;
        this.cooldownProvider = cooldownProvider;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.damage = damage;
        this.delay = delay;

        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        target = attacker.getTarget();

        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        if (target == null || !target.isAlive()) return false;
        if (!attacker.isOnGround()) return false;

        if (target instanceof Player p && (p.isCreative() || p.isSpectator()))
            return false;

        double dist = attacker.distanceTo(target);

        return dist >= minRange
                && dist <= maxRange
                && dist >= DIVE_TRIGGER_DISTANCE
                && !target.isOnGround();
    }

    @Override
    public boolean canContinueToUse() {
        return target != null
                && target.isAlive()
                && (jumpingUp || divingDown);
    }

    @Override
    public void start() {
        ticks = 0;
        jumpingUp = true;
        divingDown = false;

        attacker.getNavigation().stop();

        // Jump inicial só pra sair do chão
        attacker.setDeltaMovement(
                attacker.getLookAngle().x * 0.2,
                INITIAL_JUMP_Y,
                attacker.getLookAngle().z * 0.2
        );
        attacker.hasImpulse = true;

        attacker.level.playSound(
                null,
                attacker.blockPosition(),
                SoundEvents.ENDER_DRAGON_FLAP,
                SoundSource.HOSTILE,
                1.2f,
                0.9f
        );
    }

    @Override
    public void tick() {
        ticks++;

        if (jumpingUp) {
            handleJumpUp();
            return;
        }

        if (divingDown) {
            handleDive();
        }
    }

    /* ===================== JUMP UP STAGE ===================== */

    private void handleJumpUp() {
        attacker.getLookControl().setLookAt(target, 90, 90);

        Vec3 toTarget = target.position().subtract(attacker.position());
        Vec3 horizontal = new Vec3(toTarget.x, 0, toTarget.z).normalize();

        double yDiff = (target.getY() + REQUIRED_HEIGHT_ABOVE_TARGET) - attacker.getY();

        Vec3 pull = new Vec3(
                horizontal.x * PULL_HORIZONTAL,
                Mth.clamp(yDiff * 0.15, -0.1, PULL_UP_SPEED),
                horizontal.z * PULL_HORIZONTAL
        );

        attacker.setDeltaMovement(attacker.getDeltaMovement().add(pull));
        attacker.hasImpulse = true;

        // Condição GARANTIDA: só mergulha quando estiver acima
        if (attacker.getY() >= target.getY() + REQUIRED_HEIGHT_ABOVE_TARGET) {
            startDive();
        }
    }

    /* ===================== DIVE STAGE ===================== */

    private void startDive() {
        jumpingUp = false;
        divingDown = true;

        Vec3 lookDir = attacker.getLookAngle().normalize();

        double groundY = Math.min(target.getY(), attacker.getY() - 3);

        diveEndPos = attacker.position()
                .add(lookDir.scale(8.0))
                .with(Direction.Axis.Y, groundY);

        attacker.setDeltaMovement(
                lookDir.x * DIVE_SPEED,
                DIVE_DOWN,
                lookDir.z * DIVE_SPEED
        );
        attacker.hasImpulse = true;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    private void handleDive() {
        attacker.getLookControl().setLookAt(
                diveEndPos.x,
                diveEndPos.y,
                diveEndPos.z,
                30,
                30
        );

        pullTargetAlongDive();

        if (attacker.isOnGround()
                || attacker.position().distanceTo(diveEndPos) < 1.2) {
            impact();
        }
    }

    private void pullTargetAlongDive() {
        if (target == null) return;
        if (target instanceof Player player) {
            Abilities abilities = player.getAbilities();
            abilities.flying = false;
            player.onUpdateAbilities();
        }

        Vec3 dir = attacker.getLookAngle().normalize();

        Vec3 forced = new Vec3(
                dir.x * (DIVE_SPEED * 0.85),
                DIVE_DOWN * 0.85,
                dir.z * (DIVE_SPEED * 0.85)
        );

        target.setDeltaMovement(forced);
        target.hasImpulse = true;
        broadcastMotion(target);
    }

    /* ===================== IMPACT ===================== */

    private void impact() {
        divingDown = false;

        if (target != null && target.isAlive()) {
            target.invulnerableTime = 0;
            target.hurt(DamageSource.mobAttack(attacker), damage);
        }

        attacker.level.playSound(
                null,
                attacker.blockPosition(),
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.HOSTILE,
                1.4f,
                0.9f
        );

        stop();
    }

    @Override
    public void stop() {
        jumpingUp = false;
        divingDown = false;
        diveEndPos = Vec3.ZERO;
        ticks = 0;
        cooldown = cooldownProvider.sample(attacker.getRandom());
    }

    private static void broadcastMotion(Entity entity) {
        if (!entity.level.isClientSide) {
            ServerLevel sl = (ServerLevel) entity.level;
            sl.getChunkSource()
                    .broadcastAndSend(entity, new ClientboundSetEntityMotionPacket(entity));
        }
    }
}