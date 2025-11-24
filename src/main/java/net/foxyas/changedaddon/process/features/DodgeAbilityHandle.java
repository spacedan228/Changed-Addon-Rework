package net.foxyas.changedaddon.process.features;

import net.foxyas.changedaddon.ability.DodgeAbility;
import net.foxyas.changedaddon.ability.DodgeAbilityInstance;
import net.foxyas.changedaddon.init.ChangedAddonAbilities;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mod.EventBusSubscriber
public class DodgeAbilityHandle {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile self = event.getProjectile();
        HitResult hitResult = event.getRayTraceResult();
        if (!(hitResult instanceof EntityHitResult entityHitResult)) {
            return;
        }

        /*if (entity.invulnerableTime <= 0) {
        }*/

        Entity pTarget = entityHitResult.getEntity();
        if (!pTarget.level().isClientSide()) {
            Entity owner = self.getOwner();
            Entity attacker;
            attacker = Objects.requireNonNullElse(owner, self);
            if (pTarget instanceof ChangedEntity changedEntity) {
                List<AbstractAbility<?>> dodgeAbilities = ChangedRegistry.ABILITY.get().getValues().stream().filter((abstractAbility -> abstractAbility instanceof DodgeAbility)).toList();
                for (AbstractAbility<?> ability : dodgeAbilities) {
                    if (!(ability instanceof DodgeAbility dodgeAbility)) continue;
                    DodgeAbilityInstance dodgeAbilityInstance = changedEntity.getAbilityInstance(dodgeAbility);
                    if (dodgeAbilityInstance == null) continue;
                    if (dodgeAbilityInstance.projectilesImmuneTicks > 0) {
                        event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                    }

                    if (dodgeAbilityInstance.canUse() && dodgeAbilityInstance.canKeepUsing() && dodgeAbilityInstance.isDodgeActive()) {
                        event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                        dodgeAbilityInstance.executeDodgeEffects(changedEntity, attacker);
                        dodgeAbilityInstance.executeDodgeHandle(changedEntity, attacker);
                        break;
                    }
                    return;
                }

//                DodgeAbilityInstance dodgeAbilityInstance = changedEntity.getAbilityInstance(ChangedAddonAbilities.DODGE.get());
//                DodgeAbilityInstance teleportDodgeAbilityInstance = changedEntity.getAbilityInstance(ChangedAddonAbilities.TELEPORT_DODGE.get());
//                if (dodgeAbilityInstance != null
//                        && dodgeAbilityInstance.canUse()
//                        && dodgeAbilityInstance.canKeepUsing()
//                        && dodgeAbilityInstance.isDodgeActive()) {
//                    event.setCanceled(false);
//                    dodgeAbilityInstance.executeDodgeEffects(changedEntity, attacker);
//                    dodgeAbilityInstance.executeDodgeHandle(changedEntity, attacker);
//                } else if (teleportDodgeAbilityInstance != null
//                        && teleportDodgeAbilityInstance.canUse()
//                        && teleportDodgeAbilityInstance.canKeepUsing()
//                        && teleportDodgeAbilityInstance.isDodgeActive()) {
//                    event.setCanceled(false);
//                    teleportDodgeAbilityInstance.executeDodgeEffects(changedEntity, attacker);
//                    teleportDodgeAbilityInstance.executeDodgeHandle(changedEntity, attacker);
//                }
            }

            if (pTarget instanceof Player player) {
                TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
                if (instance != null) {
                    List<Map.Entry<AbstractAbility<?>, AbstractAbilityInstance>> dodgeAbilityInstances = instance.abilityInstances.entrySet().stream().filter((entrySet) -> (entrySet.getKey() instanceof DodgeAbility && entrySet.getValue() instanceof DodgeAbilityInstance)).toList();
                    if (!dodgeAbilityInstances.isEmpty()) {
                        for (Map.Entry<AbstractAbility<?>, AbstractAbilityInstance> dodgeAbilities : dodgeAbilityInstances) {
                            AbstractAbility<?> key = dodgeAbilities.getKey();
                            AbstractAbilityInstance value = dodgeAbilities.getValue();
                            if (key instanceof DodgeAbility && value instanceof DodgeAbilityInstance dodgeInstance) {
                                if (dodgeInstance.projectilesImmuneTicks > 0) {
                                    event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                                }

                                if (dodgeInstance.canUse() && dodgeInstance.canKeepUsing() && dodgeInstance.isDodgeActive()) {
                                    event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                                    dodgeInstance.executeDodgeEffects(player, attacker);
                                    dodgeInstance.executeDodgeHandle(player, attacker);
                                    break;
                                }
                            }
                        }
                        return;
                    }

//                    DodgeAbilityInstance dodgeAbilityInstance = instance.getAbilityInstance(ChangedAddonAbilities.DODGE.get());
//                    DodgeAbilityInstance teleportDodgeAbilityInstance = instance.getAbilityInstance(ChangedAddonAbilities.TELEPORT_DODGE.get());
//                    if (dodgeAbilityInstance != null
//                            && dodgeAbilityInstance.canUse()
//                            && dodgeAbilityInstance.canKeepUsing()
//                            && dodgeAbilityInstance.isDodgeActive()) {
//                        if (dodgeAbilityInstance.getDodgeType() == DodgeAbilityInstance.DodgeType.WEAVE) {
//                            event.setCanceled(false);
//                            dodgeAbilityInstance.executeDodgeEffects(player, attacker);
//                            dodgeAbilityInstance.executeDodgeHandle(player, attacker);
//                        }
//                    } else if (teleportDodgeAbilityInstance != null
//                            && teleportDodgeAbilityInstance.canUse()
//                            && teleportDodgeAbilityInstance.canKeepUsing()
//                            && teleportDodgeAbilityInstance.isDodgeActive()) {
//                        if (teleportDodgeAbilityInstance.getDodgeType() == DodgeAbilityInstance.DodgeType.TELEPORT) {
//                            event.setCanceled(false);
//                            teleportDodgeAbilityInstance.executeDodgeEffects(player, attacker);
//                            teleportDodgeAbilityInstance.executeDodgeHandle(player, attacker);
//                        }
//                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityAttacked(LivingAttackEvent event) {
        LivingEntity target = event.getEntity();
        Entity attacker = event.getSource().getEntity();

        if (!(target instanceof Player player) || attacker == null)
            return;

        Level world = target.level;

        TransfurVariantInstance<?> variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (variant == null)
            return;

        DodgeAbilityInstance dodge = variant.getAbilityInstance(ChangedAddonAbilities.DODGE.get());
        if (dodge == null) {
            AbstractAbilityInstance teleportDodge = variant.abilityInstances.get(ChangedAddonAbilities.TELEPORT_DODGE.get());
            if (teleportDodge == null) {
                DodgeAbilityInstance counterDodge = variant.getAbilityInstance(ChangedAddonAbilities.COUNTER_DODGE.get());
                if (counterDodge != null) {
                    dodge = counterDodge;
                }
            } else {
                dodge = variant.getAbilityInstance(ChangedAddonAbilities.TELEPORT_DODGE.get());
            }
        }

        if (dodge == null) {
            List<Map.Entry<AbstractAbility<?>, AbstractAbilityInstance>> dodgeAbilityInstances = variant.abilityInstances.entrySet().stream().filter((entrySet) -> (entrySet.getKey() instanceof DodgeAbility && entrySet.getValue() instanceof DodgeAbilityInstance)).toList();
            if (!dodgeAbilityInstances.isEmpty()) {
                for (Map.Entry<AbstractAbility<?>, AbstractAbilityInstance> dodgeAbilities : dodgeAbilityInstances) {
                    AbstractAbility<?> key = dodgeAbilities.getKey();
                    AbstractAbilityInstance value = dodgeAbilities.getValue();
                    if (key instanceof DodgeAbility && value instanceof DodgeAbilityInstance dodgeInstance) {
                        if (dodgeInstance.canUse() && dodgeInstance.canKeepUsing() && dodgeInstance.isDodgeActive()) {
                            dodge = dodgeInstance;
                            break;
                        }
                    }
                }
            }
            if (dodge == null) {
                return;
            }
        }

        if (!dodge.isDodgeActive())
            return;

        if (dodge.getDodgeAmount() <= 0) {
            dodge.getController().deactivateAbility();
            return;
        }

        if (!dodge.canUse() && !dodge.canKeepUsing()) {
            return;
        }

        if (attacker instanceof Projectile projectile) {
            return;
        }
        if (event.getSource().getDirectEntity() instanceof Projectile projectile) {
            return;
        }

        if (attacker instanceof LivingEntity livingAttacker) {
            applyDodgeEffects(player, livingAttacker, dodge, world, event);
            applyDodgeHandle(player, livingAttacker, dodge, world, event);
        }
    }

    //Keep this method for mixins
    private static void applyDodgeEffects(Player player, LivingEntity attacker, DodgeAbilityInstance dodge, LevelAccessor levelAccessor, LivingAttackEvent event) {
        dodge.executeDodgeEffects(levelAccessor, attacker, player, event);
    }

    private static void applyDodgeHandle(Player player, LivingEntity attacker, DodgeAbilityInstance dodge, LevelAccessor levelAccessor, LivingAttackEvent event) {
        dodge.executeDodgeHandle(levelAccessor, attacker, player, event, true);
    }


    public static void dashBackwards(Player target, boolean includeY) {
        Vec3 look = target.getLookAngle().normalize();
        Vec3 motion = look.scale(1.25);
        Vec3 finalMotion = includeY ?
                new Vec3(-motion.x, target.getDeltaMovement().y, -motion.z) :
                target.getDeltaMovement().add(-motion.x, 0, -motion.z);

        target.setDeltaMovement(finalMotion);
    }

    public static void dashInFacingDirection(LivingEntity target) {
        double yaw = Math.toRadians(target.getYRot());
        double pitch = Math.toRadians(target.getXRot());
        double x = -Math.sin(yaw);
        double y = -Math.sin(pitch);
        double z = Math.cos(yaw);
        double speed = 1.05;

        Vec3 motion = new Vec3(x * speed, y * speed, z * speed);
        target.setDeltaMovement(target.getDeltaMovement().add(motion));
    }

    private static void dodgeAwayFromAttacker(Entity dodger, Entity attacker) {
        Vec3 motion = attacker.position().subtract(dodger.position()).scale(-0.25);
        dodger.setDeltaMovement(motion.x, dodger.getDeltaMovement().y, motion.z);
    }
}
