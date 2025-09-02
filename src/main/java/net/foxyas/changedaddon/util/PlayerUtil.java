package net.foxyas.changedaddon.util;

import com.mojang.math.Vector3f;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonSounds;
import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.ltxprogrammer.changed.effect.particle.ColoredParticleOption;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.beast.AbstractLatexWolf;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PlayerUtil {

    public static void TransfurPlayer(Entity entity, TransfurVariant<?> latexVariant) {
        LivingEntity livingEntity = (LivingEntity) entity;
        ProcessTransfur.transfur(livingEntity, entity.getLevel(), latexVariant, true);
    }

    public static void TransfurPlayer(Player player, String id, float progress) {
        ResourceLocation form = ResourceLocation.tryParse(id);
        TransfurVariant<?> latexVariant = form == null ? null : ChangedRegistry.TRANSFUR_VARIANT.get().getValue(form);
        if (latexVariant == null) return;

        ProcessTransfur.setPlayerTransfurVariant(player, latexVariant, TransfurContext.hazard(TransfurCause.GRAB_REPLICATE), progress);
    }

    public static void TransfurPlayerAndLoadData(Player player, String id, CompoundTag data, float progress) {
        ResourceLocation form = ResourceLocation.tryParse(id);
        TransfurVariant<?> latexVariant = form == null ? null : ChangedRegistry.TRANSFUR_VARIANT.get().getValue(form);
        if (latexVariant == null) return;

        var tf = ProcessTransfur.setPlayerTransfurVariant(player, latexVariant, TransfurContext.hazard(TransfurCause.GRAB_REPLICATE), progress);
        if (tf != null) {
            tf.load(data);
        }
    }

    public static void UnTransfurPlayer(Player player) {
        ProcessTransfur.ifPlayerTransfurred(player, (variant) -> {
            variant.unhookAll(player);
            ProcessTransfur.removePlayerTransfurVariant(player);
            ProcessTransfur.setPlayerTransfurProgress(player, 0.0f);
        });
    }

    public static void UnTransfurPlayer(Player player, boolean shouldApplyEffects) {
        ProcessTransfur.ifPlayerTransfurred(player, (variant) -> {
            variant.unhookAll(player);
            ProcessTransfur.removePlayerTransfurVariant(player);
            ProcessTransfur.setPlayerTransfurProgress(player, 0.0f);
            if (shouldApplyEffects && !player.getLevel().isClientSide()) {
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false));
            }
        });
    }

    public static void UnTransfurPlayerAndPlaySound(Player player, boolean shouldApplyEffects) {
        ProcessTransfur.ifPlayerTransfurred(player, (variant) -> {
            variant.unhookAll(player);
            ProcessTransfur.removePlayerTransfurVariant(player);
            ProcessTransfur.setPlayerTransfurProgress(player, 0.0f);
            if (shouldApplyEffects && !player.getLevel().isClientSide()) {
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false));
                if (player.getLevel() instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(null, player.getX(), player.getEyeY(), player.getZ(), ChangedAddonSounds.UNTRANSFUR, SoundSource.PLAYERS, 1, 1);
                }
            }
        });
    }

    public static boolean IsCatTransfur(Player player) {
        TransfurVariant<?> variant = ProcessTransfur.getPlayerTransfurVariant(player).getParent();
        return variant.is(ChangedAddonTags.TransfurTypes.CAT_LIKE) ||
                variant.is(ChangedAddonTags.TransfurTypes.LEOPARD_LIKE);
    }

    public static boolean IsWolfTransfur(Player player) {
        TransfurVariant<?> variant = Objects.requireNonNull(ProcessTransfur.getPlayerTransfurVariant(player)).getParent();
        ChangedEntity entity = Objects.requireNonNull(ProcessTransfur.getPlayerTransfurVariant(player)).getChangedEntity();
        return Objects.requireNonNull(entity.getType().getRegistryName()).toString().contains("dog") ||
                entity.getType().getRegistryName().toString().contains("wolf") ||
                entity instanceof AbstractLatexWolf ||
                variant.is(ChangedAddonTags.TransfurTypes.WOLF_LIKE);
    }

    @Nullable
    public static Entity getEntityPlayerLookingAt(Player player, double range) {
        Level world = player.level;
        Vec3 startVec = player.getEyePosition(1.0F); // Player's eye position
        Vec3 lookVec = player.getLookAngle(); // Player's look direction
        Vec3 endVec = startVec.add(lookVec.scale(range)); // End point of the line of sight

        Entity closestEntity = null;
        double closestDistance = range;

        // Iterate over all entities within range
        for (Entity entity : world.getEntities(player, player.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(1.0D))) {
            // Ignore entities in spectator mode
            if (entity.isSpectator()) {
                continue;
            }

            AABB entityBoundingBox = entity.getBoundingBox().inflate(entity.getPickRadius());

            // Check if the line of sight intersects the entity's bounding box
            if (entityBoundingBox.contains(startVec) || entityBoundingBox.clip(startVec, endVec).isPresent()) {
                double distanceToEntity = startVec.distanceTo(entity.position());

                if (distanceToEntity < closestDistance) {
                    closestEntity = entity;
                    closestDistance = distanceToEntity;
                }
            }
        }

        return closestEntity; // Return the closest entity the player is looking at
    }

    @Nullable
    public static Entity getEntityPlayerLookingAt(Entity player, double range) {
        Level world = player.level;
        Vec3 startVec = player.getEyePosition(1.0F); // Player's eye position
        Vec3 lookVec = player.getLookAngle(); // Player's look direction
        Vec3 endVec = startVec.add(lookVec.scale(range)); // End point of the line of sight

        Entity closestEntity = null;
        double closestDistance = range;

        // Iterate over all entities within range
        for (Entity entity : world.getEntities(player, player.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(1.0D))) {
            // Ignore entities in spectator mode
            if (entity.isSpectator()) {
                continue;
            }

            AABB entityBoundingBox = entity.getBoundingBox().inflate(entity.getPickRadius());

            // Check if the line of sight intersects the entity's bounding box
            if (entityBoundingBox.contains(startVec) || entityBoundingBox.clip(startVec, endVec).isPresent()) {
                double distanceToEntity = startVec.distanceTo(entity.position());

                if (distanceToEntity < closestDistance) {
                    closestEntity = entity;
                    closestDistance = distanceToEntity;
                }
            }
        }

        return closestEntity; // Return the closest entity the player is looking at
    }

    @Nullable
    public static Entity getEntityLookingAt(Entity entity, double reach) {
        double distance = reach * reach;
        Vec3 eyePos = entity.getEyePosition(1.0f);
        HitResult hitResult = entity.pick(reach, 1.0f, false);

        if (hitResult.getType() != HitResult.Type.MISS) {
            distance = hitResult.getLocation().distanceToSqr(eyePos);
        }

        Vec3 viewVec = entity.getViewVector(1.0F);
        Vec3 toVec = eyePos.add(viewVec.x * reach, viewVec.y * reach, viewVec.z * reach);
        AABB aabb = entity.getBoundingBox().expandTowards(viewVec.scale(reach)).inflate(1.0D, 1.0D, 1.0D);

        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(entity, eyePos, toVec, aabb, e -> !e.isSpectator(), distance);

        if (entityHitResult != null) {
            Entity hitEntity = entityHitResult.getEntity();
            if (eyePos.distanceToSqr(entityHitResult.getLocation()) <= reach * reach) {
                return hitEntity;
            }
        }
        return null;
    }

    @Nullable
    public static EntityHitResult getEntityHitLookingAt(Entity entity, float reach, boolean testLineOfSight) {
        return getEntityHitLookingAt(entity, reach, testLineOfSight, e -> !e.isSpectator());
    }

    @Nullable
    public static EntityHitResult getEntityHitLookingAt(Entity entity, float reach, boolean testLineOfSight, Predicate<Entity> targetPredicate) {
        double distance = reach * reach;
        Vec3 eyePos = entity.getEyePosition(1.0f);

        if (testLineOfSight) {
            HitResult hitResult = entity.pick(reach, 1.0f, false);

            if (hitResult.getType() != HitResult.Type.MISS) {
                distance = hitResult.getLocation().distanceToSqr(eyePos);
                reach = (float) Math.sqrt(distance);
            }
        }

        Vec3 viewVec = entity.getViewVector(1.0F);
        Vec3 toVec = eyePos.add(viewVec.x * reach, viewVec.y * reach, viewVec.z * reach);
        AABB aabb = entity.getBoundingBox().expandTowards(viewVec.scale(reach)).inflate(1.0D, 1.0D, 1.0D);

        return ProjectileUtil.getEntityHitResult(entity, eyePos, toVec, aabb, targetPredicate, distance);
    }

    public static HitResult getEntityBlockHitLookingAt(Entity entity, double reach, float deltaTicks, boolean affectByFluids) {
        return entity.pick(reach, deltaTicks, affectByFluids);
    }

    @Nullable
    public static Vec3 getRelativeHitPosition(LivingEntity entity, float distance) {
        EntityHitResult hitResult = PlayerUtil.getEntityHitLookingAt(entity, distance, true);
        if (hitResult != null) {
            Vec3 hitLocation = hitResult.getLocation();
            Vec3 entityPosition = hitResult.getEntity().getPosition(1);
            return hitLocation.subtract(entityPosition);
        }
        return null;
    }


    public static boolean isLineOfSightClear(Player player, Entity entity) {
        var level = player.getLevel();
        var playerEyePos = player.getEyePosition(1.0F); // Posição dos olhos do jogador
        var entityEyePos = entity.getBoundingBox().getCenter(); // Centro da entidade

        // Realiza o traçado de linha
        var result = level.clip(new ClipContext(
                playerEyePos,
                entityEyePos,
                ClipContext.Block.VISUAL, // Apenas blocos visuais são considerados
                ClipContext.Fluid.NONE, // Ignorar fluidos
                player
        ));

        // Retorna true se o resultado for MISS (nenhum bloco obstruindo)
        return result.getType() == HitResult.Type.MISS;
    }

    @Nullable
    public static Entity getEntityPlayerLookingAtType2(Entity entity, Entity player, double entityReach) {
        double distance = entityReach * entityReach;
        Vec3 eyePos = player.getEyePosition(1.0f);
        HitResult hitResult = entity.pick(entityReach, 1.0f, false);

        if (hitResult.getType() != HitResult.Type.MISS) {
            distance = hitResult.getLocation().distanceToSqr(eyePos);
            double blockReach = 5;

            if (distance > blockReach * blockReach) {
                Vec3 pos = hitResult.getLocation();
                hitResult = BlockHitResult.miss(pos, Direction.getNearest(eyePos.x, eyePos.y, eyePos.z), new BlockPos(pos));
            }
        }

        Vec3 viewVec = player.getViewVector(1.0F);
        Vec3 toVec = eyePos.add(viewVec.x * entityReach, viewVec.y * entityReach, viewVec.z * entityReach);
        AABB aabb = entity.getBoundingBox().expandTowards(viewVec.scale(entityReach)).inflate(1.0D, 1.0D, 1.0D);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(player, eyePos, toVec, aabb, (p_234237_) -> !p_234237_.isSpectator(), distance);

        if (entityHitResult != null) {
            Entity targetEntity = entityHitResult.getEntity();
            Vec3 targetPos = entityHitResult.getLocation();
            double distanceToTarget = eyePos.distanceToSqr(targetPos);

            if (distanceToTarget > distance || distanceToTarget > entityReach * entityReach) {
                hitResult = BlockHitResult.miss(targetPos, Direction.getNearest(viewVec.x, viewVec.y, viewVec.z), new BlockPos(targetPos));
            } else if (distanceToTarget < distance) {
                hitResult = entityHitResult;
            }
        }

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult) hitResult).getEntity();
        }

        return null;
    }

    public static boolean isProjectileMovingTowardsPlayer(Player player, Entity projectile) {
        Vec3 projectilePosition = projectile.position();
        Vec3 projectileMotion = projectile.getDeltaMovement();

        Vec3 directionToPlayer = player.position().subtract(projectilePosition).normalize();

        return projectileMotion.normalize().dot(directionToPlayer) > 0;
    }

    public static void shootDynamicLaser(ServerLevel world, Player player, int maxRange, int horizontalRadius, int verticalRadius) {
        Vec3 eyePosition = player.getEyePosition(1.0F); // Posição dos olhos do jogador
        Vec3 lookDirection = player.getLookAngle();    // Direção para onde o jogador está olhando

        for (int i = 0; i <= maxRange; i++) {
            // Calcula a posição do bloco na trajetória do laser
            Vec3 targetVec = eyePosition.add(lookDirection.scale(i));
            BlockPos targetPos = new BlockPos(targetVec);

            // Verifica se o bloco é ar; se for, ignora essa fileira
            if (world.getBlockState(targetPos).isAir()) {
                continue;
            }

            // Afeta os blocos ao redor do ponto atual
            affectSurroundingBlocks(world, targetPos, horizontalRadius, verticalRadius);
        }
    }

    private static void affectSurroundingBlocks(Level world, BlockPos center, int horizontalRadius, int verticalRadius) {
        int horizontalRadiusSphere = horizontalRadius - 1;
        int verticalRadiusSphere = verticalRadius - 1;

        for (int y = -verticalRadiusSphere; y <= verticalRadiusSphere; y++) {
            for (int x = -horizontalRadiusSphere; x <= horizontalRadiusSphere; x++) {
                for (int z = -horizontalRadiusSphere; z <= horizontalRadiusSphere; z++) {
                    // Calcula a distância ao centro para uma forma esférica
                    double distanceSq = (x * x) / (double) (horizontalRadiusSphere * horizontalRadiusSphere) +
                            (y * y) / (double) (verticalRadiusSphere * verticalRadiusSphere) +
                            (z * z) / (double) (horizontalRadiusSphere * horizontalRadiusSphere);

                    if (distanceSq <= 1.0) { // Dentro da área de efeito
                        BlockPos affectedPos = center.offset(x, y, z);
                        if (world.getBlockState(affectedPos).isAir()) {
                            break;
                        }
                        // Insira a lógica para afetar os blocos
                        affectBlock(world, affectedPos);
                    }
                }
            }
        }
    }

    private static void affectBlock(Level world, BlockPos pos) {
        // Exemplo de lógica personalizada para afetar blocos
        if (!world.getBlockState(pos).isAir()) {
            // Substituir bloco por vidro como exemplo
            world.setBlock(pos, Blocks.GLASS.defaultBlockState(), 3);

            // Adicionar partículas no bloco afetado
            world.addParticle(ParticleTypes.FLAME,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0.1, 0);
        }
    }


    public static class GlobalEntityUtil {
        @Nullable
        public static Entity getEntityByUUID(LevelAccessor world, String uuid) {
            try {
                Stream<Entity> entities;

                if (world instanceof ServerLevel serverLevel) {
                    entities = StreamSupport.stream(serverLevel.getAllEntities().spliterator(), false);
                } else if (world instanceof ClientLevel clientLevel) {
                    entities = StreamSupport.stream(clientLevel.entitiesForRendering().spliterator(), false);
                } else {
                    return null;
                }

                return entities.filter(entity -> entity.getStringUUID().equals(uuid)).findFirst().orElse(null);
            } catch (Exception e) {
                ChangedAddonMod.LOGGER.error(e.getMessage()); // Log the exception for debugging purposes
                return null;
            }
        }


        @Nullable
        public static Entity getEntityByUUID(ServerLevel serverLevel, String uuid) {
            try {
                Stream<Entity> entities;
                entities = StreamSupport.stream(serverLevel.getAllEntities().spliterator(), false);
                return entities.filter(entity -> entity.getStringUUID().equals(uuid)).findFirst().orElse(null);
            } catch (Exception e) {
                ChangedAddonMod.LOGGER.error(e.getMessage()); // Log the exception for debugging purposes
                return null;
            }
        }

        @Nullable
        public static Entity getEntityByName(LevelAccessor world, String name) {
            try {
                Stream<Entity> entities;

                if (world instanceof ClientLevel clientLevel) {
                    entities = StreamSupport.stream(clientLevel.entitiesForRendering().spliterator(), false);
                } else if (world instanceof ServerLevel serverLevel) {
                    entities = StreamSupport.stream(serverLevel.getAllEntities().spliterator(), false);
                } else {
                    return null;
                }

                return entities
                        .filter(entity -> {
                            String entityName = entity.getName().getString();
                            return entityName.equalsIgnoreCase(name);
                        })
                        .findFirst()
                        .orElse(null);

            } catch (Exception e) {
                ChangedAddonMod.LOGGER.error("Error getting entity by name: " + e.getMessage());
                return null;
            }
        }

    }
}
