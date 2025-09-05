package net.foxyas.changedaddon.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractGenericParticleProjectile extends AbstractArrow {
    protected ParticleOptions particleOptions;

    public AbstractGenericParticleProjectile(EntityType<? extends AbstractGenericParticleProjectile> type, Level level) {
        super(type, level);
    }

    public AbstractGenericParticleProjectile(EntityType<? extends AbstractGenericParticleProjectile> type, Level level, LivingEntity shooter, ParticleOptions particle) {
        super(type, shooter, level);
        this.particleOptions = particle;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide && particleOptions != null) {
            spawnParticle();
        }
    }

    protected void spawnParticle() {
        if (this.getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particleOptions, this.getX(), this.getY() + 0.1D, this.getZ(), 0, 0, 0, 0, 1);
        }
    }

    @Override
    protected boolean updateInWaterStateAndDoFluidPushing() {
        return false;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        float f = (float)this.getDeltaMovement().length();
        int i = Mth.ceil(Mth.clamp((double)f * this.getBaseDamage(), 0.0F, Integer.MAX_VALUE));

        if (this.isCritArrow()) {
            long j = this.random.nextInt(i / 2 + 2);
            i = (int)Math.min(j + (long)i, 2147483647L);
        }

        Entity entity1 = this.getOwner();
        DamageSource damagesource;
        if (entity1 == null) {
            damagesource = DamageSource.arrow(this, this);
        } else {
            damagesource = DamageSource.arrow(this, entity1);
            if (entity1 instanceof LivingEntity) {
                ((LivingEntity)entity1).setLastHurtMob(entity);
            }
        }

        boolean flag = entity.getType() == EntityType.ENDERMAN;
        int k = entity.getRemainingFireTicks();
        if (this.isOnFire() && !flag) {
            entity.setSecondsOnFire(5);
        }

        if (entity.hurt(damagesource, (float)i)) {
            if (flag) {
                return;
            }

            if (entity instanceof LivingEntity livingentity) {
                if (this.getKnockback() > 0) {
                    Vec3 vec3 = this.getDeltaMovement().multiply(1.0F, 0.0F, 1.0F).normalize().scale((double) this.getKnockback() * 0.6);
                    if (vec3.lengthSqr() > (double)0.0F) {
                        livingentity.push(vec3.x, 0.1, vec3.z);
                    }
                }

                if (!this.level.isClientSide && entity1 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingentity, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)entity1, livingentity);
                }

                this.doPostHurtEffects(livingentity);
                if (livingentity != entity1 && livingentity instanceof Player && entity1 instanceof ServerPlayer && !this.isSilent()) {
                    ((ServerPlayer)entity1).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                }
            }

            this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            if (this.getPierceLevel() <= 0) {
                this.discard();
            }
        } else {
            entity.setRemainingFireTicks(k);
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1));
            this.setYRot(this.getYRot() + 180.0F);
            this.yRotO += 180.0F;
            if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7) {
                if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.discard();
            }
        }
    }

    @Override
    protected void onHit(@NotNull HitResult pResult) {
        super.onHit(pResult);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);
    }
}
