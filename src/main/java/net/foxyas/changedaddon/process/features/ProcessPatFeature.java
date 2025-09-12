package net.foxyas.changedaddon.process.features;

import net.foxyas.changedaddon.init.ChangedAddonCriteriaTriggers;
import net.foxyas.changedaddon.init.ChangedAddonMobEffects;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Random;

public class ProcessPatFeature {

    public static void SpawnEmote(Player player, LivingEntity target) {
        if (!(target instanceof ChangedEntity changedEntity) || changedEntity.getTarget() == player) return;

        if (PatFeatureHandle.shouldBeConfused(player, changedEntity)) {
            player.getLevel().addParticle(ChangedParticles.emote(changedEntity, Emote.CONFUSED),
                    target.getX(), target.getY() + (double) target.getDimensions(target.getPose()).height + 0.65, target.getZ(),
                    0.0f, 0.0f, 0.0f);
            return;
        }

        player.getLevel().addParticle(ChangedParticles.emote(changedEntity, Emote.HEART),
                target.getX(), target.getY() + (double) target.getDimensions(target.getPose()).height + 0.65, target.getZ(),
                0.0f, 0.0f, 0.0f);
    }

    public static class GlobalPatReactionEvent extends Event {
        public final Player player;
        public final LivingEntity target;
        public final LevelAccessor world;
        public final InteractionHand hand;
        @Nullable
        public final Vec3 pattedLocation;

        public GlobalPatReactionEvent(LevelAccessor world, Player player, InteractionHand hand, LivingEntity target, @Nullable Vec3 pattedLocation) {
            this.player = player;
            this.target = target;
            this.world = world;
            this.hand = hand;
            this.pattedLocation = pattedLocation;
        }

        @Nullable
        public Vec3 getPattedLocation() {
            return pattedLocation;
        }

        public boolean isCancelable() {
            return true;
        }
    }

    @Mod.EventBusSubscriber
    public static class HandleGlobalPatReaction {

        @SubscribeEvent
        public static void HandlePat(GlobalPatReactionEvent event) {
            Player player = event.player;
            LivingEntity target = event.target;

            if (player.getLevel() instanceof ServerLevel serverLevel) {
                if (target instanceof ChangedEntity changedEntity && !ProcessTransfur.isPlayerTransfurred(player)) {
                    if (!PatFeatureHandle.shouldBeConfused(player, changedEntity)) {
                        Random random = changedEntity.getRandom();
                        if (random.nextFloat() <= 0.0001f) {
                            changedEntity.addEffect(new MobEffectInstance(ChangedAddonMobEffects.PACIFIED.get(), 600, 0, true, false, true), player);
                            if (player instanceof ServerPlayer serverPlayer) {
                                ChangedAddonCriteriaTriggers.PAT_ENTITY_TRIGGER.Trigger(serverPlayer, changedEntity, "paticifier");
                            }
                        }
                    }
                }
            }

            if (!player.level.isClientSide()) {
                player.displayClientMessage(new TranslatableComponent("key.changed_addon.pat_message", target.getDisplayName().getString()), true);
                if (target instanceof Player targetPlayer) {
                    targetPlayer.displayClientMessage(new TranslatableComponent("key.changed_addon.pat_received", player.getDisplayName().getString()), true);
                }
            }

            if (event.world instanceof ServerLevel serverLevel) {
                //serverLevel.sendParticles(ParticleTypes.HEART, target.getX(), target.getY() + 1, target.getZ(), 4, 0.3, 0.3, 0.3, 1);
            } else {
                SpawnEmote(player, target);
            }

        }
    }

}
