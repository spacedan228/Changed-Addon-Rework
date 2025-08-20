package net.foxyas.changedaddon.process.features;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.entity.bosses.Experiment009BossEntity;
import net.foxyas.changedaddon.entity.bosses.Experiment009Entity;
import net.foxyas.changedaddon.entity.bosses.Experiment10BossEntity;
import net.foxyas.changedaddon.entity.bosses.Experiment10Entity;
import net.foxyas.changedaddon.entity.interfaces.CustomPatReaction;
import net.foxyas.changedaddon.entity.interfaces.SpecialPatLatex;
import net.foxyas.changedaddon.init.ChangedAddonCriteriaTriggers;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.entity.beast.AbstractDarkLatexWolf;
import net.ltxprogrammer.changed.entity.beast.SpecialLatex;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class PatFeatureHandle {

    public static boolean isPossibleToPat(Player player) {
        var variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (variant != null) {
            var ability = variant.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (ability != null
                    && ability.suited
                    && ability.grabbedHasControl) {
                return false;
            }
        }


        return GrabEntityAbility.getGrabber(player) == null;
    }

    public static void run(LevelAccessor world, Player player) {
        if (player == null) return;

        EntityHitResult targetEntityResult = getEntityHitResultLookingAt(player, player.getReachDistance());
        if (targetEntityResult == null) return;

        Entity targetEntity = targetEntityResult.getEntity();
        if (!(targetEntity instanceof LivingEntity)) return;

        if (isInSpectatorMode(player)) return;

        if (!(isPossibleToPat(player))) return;

        if (targetEntity instanceof SpecialPatLatex) {
            handleSpecialEntities(player, targetEntityResult);
        } else {
            if (targetEntity instanceof ChangedEntity) {
                handleLatexEntity(player, targetEntityResult, world);
            } else if (targetEntity instanceof Player) {
                handlePlayerEntity(player, targetEntityResult, world);
            } else if (targetEntity.getType().is(ChangedAddonTags.EntityTypes.PATABLE)) {
                handlePatableEntity(player, targetEntityResult, world);
            }
        }
    }

    private static EntityHitResult getEntityHitResultLookingAt(Entity entity, double reach) {
        double distance = reach * reach;
        Vec3 eyePos = entity.getEyePosition(1.0f);
        HitResult hitResult = entity.pick(reach, 1.0f, false);

        if (hitResult.getType() != HitResult.Type.MISS) {
            distance = hitResult.getLocation().distanceToSqr(eyePos);
        }

        Vec3 viewVec = entity.getViewVector(1.0F);
        Vec3 toVec = eyePos.add(viewVec.x * reach, viewVec.y * reach, viewVec.z * reach);
        AABB aabb = entity.getBoundingBox().expandTowards(viewVec.scale(reach)).inflate(1.0D, 1.0D, 1.0D);

        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(entity, eyePos, toVec, aabb, e -> {
            if (e.isSpectator()) return false;
            if (!(e instanceof LivingEntity le)) return false;
            if (GrabEntityAbility.getGrabber(le) == null) return true;
            LivingEntity livingEntity = Objects.requireNonNull(GrabEntityAbility.getGrabber(le)).getEntity();
            return livingEntity != entity;
        }, distance);

        if (entityHitResult != null) {
            if (eyePos.distanceToSqr(entityHitResult.getLocation()) <= reach * reach) {
                return entityHitResult;
            }
        }
        return null;
    }

    private static boolean isInSpectatorMode(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
        } else if (entity.level.isClientSide() && entity instanceof Player player) {
            return Objects.requireNonNull(Objects.requireNonNull(Minecraft.getInstance().getConnection()).getPlayerInfo(player.getGameProfile().getId())).getGameMode() == GameType.SPECTATOR;
        }
        return false;
    }

    private static void handleSpecialEntities(Entity player, EntityHitResult entityHitResult) {
        Entity target = entityHitResult.getEntity();

        if (isHandEmpty(player, InteractionHand.MAIN_HAND) || isHandEmpty(player, InteractionHand.OFF_HAND)) {
            if (player instanceof Player p) {
                p.swing(getSwingHand(p));
                if (target instanceof CustomPatReaction pat) {
                    InteractionHand hand = getSwingHand(player);
                    if (target instanceof LivingEntity livingEntity) {
                        ProcessPatFeature.GlobalPatReaction globalPatReactionEvent = new ProcessPatFeature.GlobalPatReaction(player.getLevel(), p, hand, livingEntity, entityHitResult.getLocation());
                        if (ChangedAddonMod.postEvent(globalPatReactionEvent)) {
                            return;
                        }
                    }
                    pat.WhenPattedReaction(p, hand, entityHitResult.getLocation());
                    pat.WhenPattedReaction(p, hand);
                    pat.WhenPattedReaction();
                }
            }

        }
    }

    private static void handleLatexEntity(Entity player, EntityHitResult entityHitResult, LevelAccessor world) {
        Entity target = entityHitResult.getEntity();

        if (isHandEmpty(player, InteractionHand.MAIN_HAND)
                || isHandEmpty(player, InteractionHand.OFF_HAND)) {
            Player _player = (Player) player;
            _player.swing(getSwingHand(player), true);

            if (target instanceof LivingEntity targetLiving) {
                InteractionHand hand = getSwingHand(_player);
                ProcessPatFeature.GlobalPatReaction globalPatReactionEvent = new ProcessPatFeature.GlobalPatReaction(world, _player, hand, targetLiving, entityHitResult.getLocation());
                if (ChangedAddonMod.postEvent(globalPatReactionEvent)) {
                    return;
                }
            }

            if (ProcessTransfur.getPlayerTransfurVariant(_player) != null && ProcessTransfur.getPlayerTransfurVariant(_player).getChangedEntity() instanceof CustomPatReaction playerPat) {
                if (target instanceof ChangedEntity changedEntity) {
                    InteractionHand hand = getSwingHand(_player);
                    playerPat.WhenPatEvent(_player, hand, changedEntity);
                }
                if (target instanceof CustomPatReaction e) {
                    InteractionHand hand = getSwingHand(_player);
                    e.WhenPattedReaction(_player, hand, entityHitResult.getLocation());
                    e.WhenPattedReaction(_player, hand);
                    e.WhenPattedReaction();
                }
            } else {
                if (target instanceof CustomPatReaction e) {
                    InteractionHand hand = getSwingHand(_player);
                    e.WhenPattedReaction(_player, hand, entityHitResult.getLocation());
                    e.WhenPattedReaction(_player, hand);
                    e.WhenPattedReaction();
                }
            }
            if (world instanceof ServerLevel) {
                _player.swing(getSwingHand(player), true);

                if (_player instanceof ServerPlayer sp) {
                    GiveStealthPatAdvancement(sp, target);
                }
            }
        }
    }

    private static void handlePlayerEntity(Player player, EntityHitResult entityHitResult, LevelAccessor world) {
        Player target = (Player) entityHitResult.getEntity();
        boolean isPlayerTransfur = (ProcessTransfur.getPlayerTransfurVariant(player) != null);
        boolean isTargetTransfur = (ProcessTransfur.getPlayerTransfurVariant(target) != null);


        if (isHandEmpty(player, InteractionHand.MAIN_HAND) || isHandEmpty(player, InteractionHand.OFF_HAND)) {
            if (!isPlayerTransfur && !isTargetTransfur) {
                return;
            }//Don't Be Able to Pet if at lest one is Transfur :P

            player.swing(getSwingHand(player), true);

            if (ProcessTransfur.getPlayerTransfurVariant(player) != null
                    && ProcessTransfur.getPlayerTransfurVariant(player).getChangedEntity() instanceof CustomPatReaction playerPat) {
                InteractionHand hand = getSwingHand(player);
                playerPat.WhenPatEvent(player, hand, target);
                if (ProcessTransfur.getPlayerTransfurVariant(target).getChangedEntity() instanceof CustomPatReaction TargetPat) {
                    TargetPat.WhenPattedReaction(player, hand, entityHitResult.getLocation());
                    TargetPat.WhenPattedReaction(player, hand);
                    TargetPat.WhenPattedReaction();
                    //p.displayClientMessage(new TextComponent("pat_message:" + target.getDisplayName().getString()), false);
                }
            } else {
                if (ProcessTransfur.getPlayerTransfurVariant(target) != null
                        && ProcessTransfur.getPlayerTransfurVariant(target).getChangedEntity() instanceof CustomPatReaction e) {
                    InteractionHand hand = getSwingHand(player);
                    e.WhenPattedReaction(player, hand, entityHitResult.getLocation());
                    e.WhenPattedReaction(player, hand);
                    e.WhenPattedReaction();
                    //p.displayClientMessage(new TextComponent("pat_message:" + target.getDisplayName().getString()), false);
                }
            }

            InteractionHand hand = getSwingHand(player);
            ProcessPatFeature.GlobalPatReaction globalPatReactionEvent = new ProcessPatFeature.GlobalPatReaction(world, player, hand, target, entityHitResult.getLocation());
            if (ChangedAddonMod.postEvent(globalPatReactionEvent)) {
                return;
            }
            if (isTargetTransfur && world instanceof ServerLevel serverLevel) {
                //serverLevel.sendParticles(ParticleTypes.HEART, target.getX(), target.getY() + 1, target.getZ(), 7, 0.3, 0.3, 0.3, 1);
                if (serverLevel.random.nextFloat() <= (0.025f + (player.getLuck() * 0.01f))) {
                    target.heal(6f);
                    GivePatAdvancement(player, target);
                }
            }
        }
    }


    private static void handlePatableEntity(Entity player, EntityHitResult entityHitResult, LevelAccessor world) {
        Entity target = entityHitResult.getEntity();
        if (isHandEmpty(player, InteractionHand.MAIN_HAND) || isHandEmpty(player, InteractionHand.OFF_HAND)) {
            if (player instanceof Player) {
                ((Player) player).swing(getSwingHand(player), true);
            }
            if (world instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HEART, target.getX(), target.getY() + 1, target.getZ(), 7, 0.3, 0.3, 0.3, 1);
            }
            if (player instanceof Player p && !p.level.isClientSide()) {
                p.displayClientMessage(new TranslatableComponent("key.changed_addon.pat_message", target.getDisplayName().getString()), true);
            }
        }
    }

    private static boolean isHandEmpty(Entity entity, InteractionHand hand) {
        return entity instanceof LivingEntity livingEntity && livingEntity.getItemInHand(hand).getItem() == Blocks.AIR.asItem();
    }

    private static InteractionHand getSwingHand(Entity entity) {
        return isHandEmpty(entity, InteractionHand.MAIN_HAND) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    public static void GivePatAdvancement(Entity player, Entity target) {
        if (player instanceof ServerPlayer _player) {
            ChangedAddonCriteriaTriggers.PAT_ENTITY_TRIGGER.Trigger(_player, target, "chance");
        }
    }

    public static void SpawnEmote(Player player, Entity target) {
        if (target instanceof ChangedEntity changedEntity) {
            if (changedEntity.getTarget() == player) {
                return;
            }
            if (shouldBeConfused(player, target)) {
                player.getLevel().addParticle(
                        ChangedParticles.emote(changedEntity, Emote.CONFUSED),
                        target.getX(),
                        target.getY() + (double) target.getDimensions(target.getPose()).height + 0.65,
                        target.getZ(),
                        0.0f,
                        0.0f,
                        0.0f);
            }
        }
    }

    private static boolean shouldBeConfused(Player player, Entity entity) {
        if (entity instanceof AbstractDarkLatexWolf) {
            // Verificando se o jogador usa a armadura correta
            return player.getItemBySlot(EquipmentSlot.HEAD).is(ChangedAddonItems.DARK_LATEX_HEAD_CAP.get())
                    && player.getItemBySlot(EquipmentSlot.CHEST).is(ChangedAddonItems.DARK_LATEX_COAT.get());
        }
        return false;
    }

    public static void GiveStealthPatAdvancement(Entity entity, Entity target) {
        if (entity instanceof ServerPlayer _player) {
            ChangedAddonCriteriaTriggers.PAT_ENTITY_TRIGGER.Trigger(_player, target, "stealth");
        }

    }
}
