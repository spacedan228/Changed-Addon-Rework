package net.foxyas.changedaddon.abilities;

import net.foxyas.changedaddon.init.ChangedAddonAnimationEvents;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.init.ChangedAnimationEvents;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.jetbrains.annotations.Nullable;

public class DodgeAbilityInstance extends AbstractAbilityInstance {

    public enum DodgeType {

        TELEPORT(),

        WEAVE();

        DodgeType() {
        }

    }

    private int dodgeAmount = 4;
    private int maxDodgeAmount = 4;
    private boolean dodgeActive = false;

    private final int defaultRegenCooldown = 20;
    private int dodgeRegenCooldown = defaultRegenCooldown;
    public DodgeType dodgeType = DodgeType.WEAVE;

    public DodgeAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    public DodgeAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity, int maxDodge) {
        this(ability, entity);
        this.maxDodgeAmount = maxDodge;
        this.dodgeAmount = maxDodge;
    }

    public DodgeAbilityInstance withDodgeType(DodgeType dodgeType) {
        this.dodgeType = dodgeType;
        return this;
    }

    public boolean isDodgeActive() {
        return dodgeActive;
    }

    public void setDodgeActivate(boolean active) {
        this.dodgeActive = active;
    }

    public int getDodgeAmount() {
        return dodgeAmount;
    }

    public void setDodgeAmount(int amount) {
        dodgeAmount = Math.min(amount, maxDodgeAmount);
    }

    public void addDodgeAmount() {
        if (dodgeAmount < maxDodgeAmount) dodgeAmount++;
    }

    public void subDodgeAmount() {
        if (dodgeAmount > 0) dodgeAmount--;
    }

    public DodgeType getDodgeType() {
        return dodgeType;
    }

    public void executeDodgeEffects(ServerLevel serverLevel, LivingEntity attacker, Player dodger, @Nullable LivingAttackEvent event) {
        this.subDodgeAmount();
        dodger.displayClientMessage(new TranslatableComponent("changed_addon.ability.dodge.dodge_amount_left", this.getDodgeStaminaRatio()), false);
        dodger.invulnerableTime = 20 * 3;
        dodger.hurtDuration = 20 * 3;
        dodger.hurtTime = dodger.hurtDuration;
        dodger.causeFoodExhaustion(8f);
        if (event != null) {
            event.setCanceled(true);
        }
        spawnDodgeParticles(serverLevel, dodger, 0.5f, 0.3f, 0.3f, 0.3f, 10, 0.25f);
        int randomValue = serverLevel.getRandom().nextInt(6);
        switch (randomValue) {
            case 0 -> ChangedAnimationEvents.broadcastEntityAnimation(dodger, ChangedAddonAnimationEvents.DODGE_LEFT.get(), null);
            case 1 -> ChangedAnimationEvents.broadcastEntityAnimation(dodger, ChangedAddonAnimationEvents.DODGE_RIGHT.get(), null);
            case 2 -> ChangedAnimationEvents.broadcastEntityAnimation(dodger, ChangedAddonAnimationEvents.DODGE_WEAVE_LEFT.get(), null);
            case 3 -> ChangedAnimationEvents.broadcastEntityAnimation(dodger, ChangedAddonAnimationEvents.DODGE_WEAVE_RIGHT.get(), null);
            case 4 -> ChangedAnimationEvents.broadcastEntityAnimation(dodger, ChangedAddonAnimationEvents.DODGE_DOWN_LEFT.get(), null);
            case 5 -> ChangedAnimationEvents.broadcastEntityAnimation(dodger, ChangedAddonAnimationEvents.DODGE_DOWN_RIGHT.get(), null);
            //default -> ChangedAnimationEvents.broadcastEntityAnimation(player, ChangedAddonAnimationEvents.DODGE_LEFT.get(), null);
        }
    }

    public void executeDodgeHandle(ServerLevel serverLevel, LivingEntity attacker, Player dodger, LivingAttackEvent event){
        Vec3 attackerPos = attacker.position();
        Vec3 lookDirection = attacker.getLookAngle().normalize();
        Vec3 dodgerLookDirection = dodger.getLookAngle();
        final double distanceBehind = 2;
        Vec3 dodgePosBehind = attackerPos.subtract(lookDirection.scale(distanceBehind));
        double distance = attacker.distanceTo(dodger);

        if (distance <= 1.5f && this.getDodgeType() == DodgeType.TELEPORT) {
            BlockPos teleportPos = new BlockPos(dodgePosBehind.x, dodger.getY(), dodgePosBehind.z);
            if (serverLevel.isEmptyBlock(teleportPos) || serverLevel.isEmptyBlock(teleportPos.above())) {
                dodger.teleportTo(teleportPos.getX(), teleportPos.getY(), teleportPos.getZ());
            }
        } else {
            dodgeAwayFromAttacker(dodger, attacker);
        }

        ChangedSounds.broadcastSound(dodger, ChangedSounds.BOW2, 2.5f, 1);
    }

    public void executeDodgeEffects(Player dodger, LivingEntity attacker) {
        if (dodger.getLevel() instanceof ServerLevel serverLevel) {
            this.executeDodgeEffects(serverLevel, attacker, dodger, null);
        }
    }

    private static void dodgeAwayFromAttacker(Entity dodger, Entity attacker) {
        Vec3 motion = attacker.position().subtract(dodger.position()).scale(-0.25);
        dodger.setDeltaMovement(motion.x, dodger.getDeltaMovement().y, motion.z);
    }

    private void spawnDodgeParticles(ServerLevel level, Entity entity, float middle, float xV, float yV, float zV, int count, float speed) {
        level.sendParticles(ParticleTypes.POOF,
                entity.getX(), entity.getY() + middle, entity.getZ(), count, xV, yV, zV, speed);
    }
    public int getMaxDodgeAmount() {
        return maxDodgeAmount;
    }

    public void setMaxDodgeAmount(int max) {
        maxDodgeAmount = max;
        dodgeAmount = Math.min(dodgeAmount, max); // Adjust current amount if needed
    }

    public float getDodgeStaminaRatio() {
        return ((float) dodgeAmount / maxDodgeAmount) * 100f;
    }

    public static boolean isSpectator(Entity entity) {
        return entity instanceof Player player && player.isSpectator();
    }

    @Override
    public boolean canUse() {
        return dodgeAmount > 0 && !isSpectator(entity.getEntity());
    }

    @Override
    public boolean canKeepUsing() {
        return dodgeAmount > 0 && !isSpectator(entity.getEntity());
    }

    @Override
    public void startUsing() {
        if (entity.getEntity() instanceof Player player && this.getController().getHoldTicks() == 0) {
            player.displayClientMessage(
                    new TranslatableComponent("changed_addon.ability.dodge.dodge_amount", getDodgeStaminaRatio()),
                    true
            );
        }
    }

    @Override
    public void tick() {
        //super.tick();
        if (entity.getEntity() instanceof Player player) {
            if (!(player.getLevel().isClientSide())) {
                player.displayClientMessage(
                        new TranslatableComponent("changed_addon.ability.dodge.dodge_amount", getDodgeStaminaRatio()), true);
            }
        }
        setDodgeActivate(canUse());
    }

    @Override
    public void stopUsing() {
        //super.stopUsing();
        setDodgeActivate(false);
    }

    @Override
    public void tickIdle() {
        super.tickIdle();
        boolean nonHurtFrame = entity.getEntity().hurtTime <= 10 && entity.getEntity().invulnerableTime <= 10;
        if (nonHurtFrame && !isDodgeActive() && dodgeAmount < maxDodgeAmount) {
            if (dodgeRegenCooldown <= 0) {
                addDodgeAmount();
                dodgeRegenCooldown = 5;

                if (entity.getEntity() instanceof Player player) {
                    player.displayClientMessage(
                            new TranslatableComponent("changed_addon.ability.dodge.dodge_amount", getDodgeStaminaRatio()),
                            true
                    );
                }
            } else {
                dodgeRegenCooldown--;
            }
        }
    }

    @Override
    public void readData(CompoundTag tag) {
        super.readData(tag);
        if (tag.contains("DodgeAmount")) dodgeAmount = tag.getInt("DodgeAmount");
        if (tag.contains("MaxDodgeAmount")) maxDodgeAmount = tag.getInt("MaxDodgeAmount");
        if (tag.contains("DodgeRegenCooldown")) dodgeRegenCooldown = tag.getInt("DodgeRegenCooldown");
        if (tag.contains("DodgeActivate")) dodgeActive = tag.getBoolean("DodgeActivate");
    }

    @Override
    public void saveData(CompoundTag tag) {
        super.saveData(tag);
        tag.putInt("DodgeAmount", dodgeAmount);
        tag.putInt("MaxDodgeAmount", maxDodgeAmount);
        tag.putInt("DodgeRegenCooldown", dodgeRegenCooldown);
        tag.putBoolean("DodgeActivate", dodgeActive);
    }
}
