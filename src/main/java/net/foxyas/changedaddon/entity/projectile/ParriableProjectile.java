package net.foxyas.changedaddon.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.foxyas.changedaddon.util.DynamicClipContext;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public abstract class ParriableProjectile extends Projectile {

    private static final double ARROW_BASE_DAMAGE = 2.0D;
    private static final EntityDataAccessor<Byte> ID_FLAGS = SynchedEntityData.defineId(ParriableProjectile.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> PIERCE_LEVEL = SynchedEntityData.defineId(ParriableProjectile.class, EntityDataSerializers.BYTE);
    private static final int FLAG_CRIT = 1;
    private static final int FLAG_NO_PHYSICS = 2;
    private static final int FLAG_CROSSBOW = 4;

    private double baseDamage = ARROW_BASE_DAMAGE;
    private int knockback;
    private SoundEvent soundEvent = getDefaultHitGroundSoundEvent();
    private @Nullable IntOpenHashSet piercingIgnoreEntityIds;
    private @Nullable List<Entity> piercedAndKilledEntities;

    protected ParriableProjectile(EntityType<? extends ParriableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    protected ParriableProjectile(EntityType<? extends ParriableProjectile> entityType, double x, double y, double z, Level level) {
        this(entityType, level);
        setPos(x, y, z);
    }

    protected ParriableProjectile(EntityType<? extends ParriableProjectile> entityType, LivingEntity shooter, Level level) {
        this(entityType, shooter.getX(), shooter.getEyeY() - (double) 0.1F, shooter.getZ(), level);
        setOwner(shooter);
    }

    public void setSoundEvent(SoundEvent pSoundEvent) {
        soundEvent = pSoundEvent;
    }

    /**
     * Checks if the entity is in range to render.
     */
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        double d0 = getBoundingBox().getSize() * 10.0D;
        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }

        d0 *= 64.0D * getViewScale();
        return pDistance < d0 * d0;
    }

    protected void defineSynchedData() {
        entityData.define(ID_FLAGS, (byte) 0);
        entityData.define(PIERCE_LEVEL, (byte) 0);
    }

    /**
     * Sets a target for the client to interpolate towards over the next few ticks
     */
    public void lerpTo(double pX, double pY, double pZ, float pYaw, float pPitch, int pPosRotationIncrements, boolean pTeleport) {
        setPos(pX, pY, pZ);
        setRot(pYaw, pPitch);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        super.tick();
        boolean flag = isNoPhysics();
        Vec3 vec3 = getDeltaMovement();
        if (xRotO == 0.0F && yRotO == 0.0F) {
            double d0 = vec3.horizontalDistance();
            setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
            setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI)));
            yRotO = getYRot();
            xRotO = getXRot();
        }

        BlockPos blockpos = blockPosition();
        BlockState blockstate = level.getBlockState(blockpos);
        if (!blockstate.isAir() && !ignoreBlock(blockstate) && !flag) {
            VoxelShape voxelshape = blockstate.getCollisionShape(level, blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 vec31 = position();

                for (AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockpos).contains(vec31)) {
                        onHitBlock();
                        return;
                    }
                }
            }
        }

        if (isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW)) {
            clearFire();
        }

        Vec3 vec32 = position();
        Vec3 vec33 = vec32.add(vec3);
        HitResult hitresult = level.clip(new DynamicClipContext(vec32, vec33, (state, b, pos, context) -> {
            if (ignoreBlock(state)) return Shapes.empty();
            return ClipContext.Block.COLLIDER.get(state, b, pos, context);
        }, ClipContext.Fluid.NONE::canPick, CollisionContext.of(this)));

        if (hitresult.getType() != HitResult.Type.MISS) {
            vec33 = hitresult.getLocation();
        }

        while (!isRemoved()) {
            EntityHitResult entityhitresult = findHitEntity(vec32, vec33);
            if (entityhitresult != null) {
                hitresult = entityhitresult;
            }

            if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) hitresult).getEntity();
                Entity entity1 = getOwner();
                if (entity instanceof Player player && entity1 instanceof Player player1 && !player1.canHarmPlayer(player)) {
                    hitresult = null;
                    entityhitresult = null;
                }
            }

            if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !flag && !ForgeEventFactory.onProjectileImpact(this, hitresult)) {
                onHit(hitresult);
                hasImpulse = true;
            }

            if (entityhitresult == null || getPierceLevel() <= 0) {
                break;
            }

            hitresult = null;
        }

        vec3 = getDeltaMovement();
        double d5 = vec3.x;
        double d6 = vec3.y;
        double d1 = vec3.z;
        if (isCritArrow()) {
            for (int i = 0; i < 4; ++i) {
                level.addParticle(ParticleTypes.CRIT, getX() + d5 * (double) i / 4.0D, getY() + d6 * (double) i / 4.0D, getZ() + d1 * (double) i / 4.0D, -d5, -d6 + 0.2D, -d1);
            }
        }

        double d7 = getX() + d5;
        double d2 = getY() + d6;
        double d3 = getZ() + d1;
        double d4 = vec3.horizontalDistance();
        if (flag) {
            setYRot((float) (Mth.atan2(-d5, -d1) * (double) (180F / (float) Math.PI)));
        } else {
            setYRot((float) (Mth.atan2(d5, d1) * (double) (180F / (float) Math.PI)));
        }

        setXRot((float) (Mth.atan2(d6, d4) * (double) (180F / (float) Math.PI)));
        setXRot(lerpRotation(xRotO, getXRot()));
        setYRot(lerpRotation(yRotO, getYRot()));
        float f = 0.99F;
        if (isInWater()) {
            for (int j = 0; j < 4; ++j) {
                level.addParticle(ParticleTypes.BUBBLE, d7 - d5 * 0.25D, d2 - d6 * 0.25D, d3 - d1 * 0.25D, d5, d6, d1);
            }

            f = getWaterInertia();
        }

        setDeltaMovement(vec3.scale(f));
        if (!isNoGravity() && !flag) {
            Vec3 vec34 = getDeltaMovement();
            setDeltaMovement(vec34.x, vec34.y - (double) 0.05F, vec34.z);
        }

        setPos(d7, d2, d3);
        checkInsideBlocks();
    }

    protected DamageSource damageSource() {
        Entity owner = getOwner();
        return new IndirectEntityDamageSource("arrow", this, owner != null ? owner : this).setProjectile();
    }

    /**
     * Called when the arrow hits an entity
     */
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);
        Entity entity = pResult.getEntity();
        float f = (float) getDeltaMovement().length();
        int i = Mth.ceil(Mth.clamp((double) f * baseDamage, 0.0D, 2.147483647E9D));
        if (getPierceLevel() > 0) {
            if (piercingIgnoreEntityIds == null) {
                piercingIgnoreEntityIds = new IntOpenHashSet(5);
            }

            if (piercedAndKilledEntities == null) {
                piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
            }

            if (piercingIgnoreEntityIds.size() >= getPierceLevel() + 1) {
                discard();
                return;
            }

            piercingIgnoreEntityIds.add(entity.getId());
        }

        if (isCritArrow()) {
            long j = random.nextInt(i / 2 + 2);
            i = (int) Math.min(j + (long) i, 2147483647L);
        }

        Entity entity1 = getOwner();
        if (entity1 instanceof LivingEntity livingOwner) {
            livingOwner.setLastHurtMob(entity);
        }

        boolean flag = entity.getType() == EntityType.ENDERMAN;
        int k = entity.getRemainingFireTicks();
        if (isOnFire() && !flag) {
            entity.setSecondsOnFire(5);
        }

        if (entity.hurt(damageSource(), (float) i)) {
            if (flag) {
                return;
            }

            if (entity instanceof LivingEntity livingentity) {
                if (knockback > 0) {
                    Vec3 vec3 = getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double) knockback * 0.6D);
                    if (vec3.lengthSqr() > 0.0D) {
                        livingentity.push(vec3.x, 0.1D, vec3.z);
                    }
                }

                if (!level.isClientSide && entity1 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingentity, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) entity1, livingentity);
                }

                doPostHurtEffects(livingentity);
                if (livingentity != entity1 && livingentity instanceof Player && entity1 instanceof ServerPlayer && !isSilent()) {
                    ((ServerPlayer) entity1).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                }

                if (!entity.isAlive() && piercedAndKilledEntities != null) {
                    piercedAndKilledEntities.add(livingentity);
                }

                if (!level.isClientSide && entity1 instanceof ServerPlayer serverplayer) {
                    if (piercedAndKilledEntities != null && shotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayer, piercedAndKilledEntities);
                    } else if (!entity.isAlive() && shotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayer, Arrays.asList(entity));
                    }
                }
            }

            playSound(soundEvent, 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
            if (getPierceLevel() <= 0) {
                onDiscard();
                discard();
            }
        } else {
            if (discardOnNoDmgImpact()) {
                onDiscard();
                discard();
                return;
            }

            entity.setRemainingFireTicks(k);
            setDeltaMovement(getDeltaMovement().scale(-0.1D));
            setYRot(getYRot() + 180.0F);
            yRotO += 180.0F;

            if (!level.isClientSide && getDeltaMovement().lengthSqr() < 1.0E-7D) discard();
        }
    }

    protected abstract boolean ignoreBlock(@NotNull BlockState state);

    public boolean discardOnNoDmgImpact() {
        return false;
    }

    public void onDiscard() {
    }

    protected void onHitBlock() {
        if (!isSilent()) playSound(getHitGroundSoundEvent(), 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
        discard();
    }

    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        onHitBlock();
    }

    /**
     * The sound made when an entity is hit by this projectile
     */
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.ARROW_HIT;
    }

    protected final SoundEvent getHitGroundSoundEvent() {
        return soundEvent;
    }

    protected void doPostHurtEffects(LivingEntity pTarget) {
    }

    /**
     * Gets the EntityHitResult representing the entity hit
     */
    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(level, this, pStartVec, pEndVec, getBoundingBox().expandTowards(getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

    protected boolean canHitEntity(@NotNull Entity target) {
        return super.canHitEntity(target) && (piercingIgnoreEntityIds == null || !piercingIgnoreEntityIds.contains(target.getId()));
    }

    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putDouble("damage", baseDamage);
        pCompound.putBoolean("crit", isCritArrow());
        pCompound.putByte("PierceLevel", getPierceLevel());
        pCompound.putString("SoundEvent", Registry.SOUND_EVENT.getKey(soundEvent).toString());
        pCompound.putBoolean("ShotFromCrossbow", shotFromCrossbow());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.contains("damage", 99)) {
            baseDamage = pCompound.getDouble("damage");
        }

        setCritArrow(pCompound.getBoolean("crit"));
        setPierceLevel(pCompound.getByte("PierceLevel"));
        if (pCompound.contains("SoundEvent", 8)) {
            soundEvent = Registry.SOUND_EVENT.getOptional(new ResourceLocation(pCompound.getString("SoundEvent"))).orElse(getDefaultHitGroundSoundEvent());
        }

        setShotFromCrossbow(pCompound.getBoolean("ShotFromCrossbow"));
    }

    protected abstract ItemStack getPickupItem();

    protected Entity.@NotNull MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    public void setBaseDamage(double pBaseDamage) {
        baseDamage = pBaseDamage;
    }

    public double getBaseDamage() {
        return baseDamage;
    }

    /**
     * Sets the amount of knockback the arrow applies when it hits a mob.
     */
    public void setKnockback(int pKnockback) {
        knockback = pKnockback;
    }

    public int getKnockback() {
        return knockback;
    }

    /**
     * Returns true if it's possible to attack this entity with an item.
     */
    public boolean isAttackable() {
        return false;
    }

    protected float getEyeHeight(@NotNull Pose pPose, @NotNull EntityDimensions pSize) {
        return 0.13F;
    }

    /**
     * Whether the arrow has a stream of critical hit particles flying behind it.
     */
    public void setCritArrow(boolean pCritArrow) {
        setFlag(FLAG_CRIT, pCritArrow);
    }

    public void setPierceLevel(byte pPierceLevel) {
        entityData.set(PIERCE_LEVEL, pPierceLevel);
    }

    private void setFlag(int pId, boolean pValue) {
        byte b0 = entityData.get(ID_FLAGS);
        if (pValue) {
            entityData.set(ID_FLAGS, (byte) (b0 | pId));
        } else {
            entityData.set(ID_FLAGS, (byte) (b0 & ~pId));
        }

    }

    /**
     * Whether the arrow has a stream of critical hit particles flying behind it.
     */
    public boolean isCritArrow() {
        byte b0 = entityData.get(ID_FLAGS);
        return (b0 & FLAG_CRIT) != 0;
    }

    /**
     * Whether the arrow was shot from a crossbow.
     */
    public boolean shotFromCrossbow() {
        byte b0 = entityData.get(ID_FLAGS);
        return (b0 & FLAG_CROSSBOW) != 0;
    }

    public byte getPierceLevel() {
        return entityData.get(PIERCE_LEVEL);
    }

    public void setEnchantmentEffectsFromEntity(LivingEntity pShooter, float pVelocity) {
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, pShooter);
        int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH_ARROWS, pShooter);
        setBaseDamage((double) (pVelocity * 2.0F) + random.nextGaussian() * 0.25D + (double) ((float) level.getDifficulty().getId() * 0.11F));
        if (i > 0) {
            setBaseDamage(getBaseDamage() + (double) i * 0.5D + 0.5D);
        }

        if (j > 0) {
            setKnockback(j);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAMING_ARROWS, pShooter) > 0) {
            setSecondsOnFire(100);
        }

    }

    protected float getWaterInertia() {
        return 0.6F;
    }

    /**
     * Sets if this arrow can noClip
     */
    public void setNoPhysics(boolean pNoPhysics) {
        noPhysics = pNoPhysics;
        setFlag(FLAG_NO_PHYSICS, pNoPhysics);
    }

    /**
     * Whether the arrow can noClip
     */
    public boolean isNoPhysics() {
        if (!level.isClientSide) {
            return noPhysics;
        } else {
            return (entityData.get(ID_FLAGS) & FLAG_NO_PHYSICS) != 0;
        }
    }

    /**
     * Sets data about if this arrow entity was shot from a crossbow
     */
    public void setShotFromCrossbow(boolean pShotFromCrossbow) {
        setFlag(FLAG_CROSSBOW, pShotFromCrossbow);
    }
}
