package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.init.ChangedAddonMobEffects;
import net.foxyas.changedaddon.init.ChangedAddonSoundEvents;
import net.foxyas.changedaddon.network.ChangedAddonModVariables;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class SyringeWithLitixCammoniaPlayerFinishesUsingItem {
    public static void run(LevelAccessor world, Entity entity) {
        if (!(entity instanceof Player player)) return;

        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        DamageSource untransfurFail = new DamageSource("untransfur_fail").bypassArmor();

        if (ProcessTransfur.isPlayerTransfurred(player)) {
            if (entity.getLevel().random.nextFloat() >= 0.35) {
                handleUntransfurSuccess(world, player, x, y, z);
            } else {
                entity.hurt(untransfurFail, 15);
                sendMessage(player, "changedaddon.untransfur.fail");
            }
        } else {
            if (getVars(player).showWarns) {
                sendMessage(player, "changedaddon.untransfur.no_effect");
            }
        }

        /*
        if (!isCreativeOrSpectator(player)) {
            ItemStack syringe = new ItemStack(ChangedItems.SYRINGE.get());
            syringe.setCount(1);
            ItemHandlerHelper.giveItemToPlayer(player, syringe);
        }*/
    }

    private static void handleUntransfurSuccess(LevelAccessor world, Player player, double x, double y, double z) {
        if (ProcessTransfur.isPlayerNotLatex(player)) {
            if (!player.level.isClientSide()) {
                player.addEffect(new MobEffectInstance(ChangedAddonMobEffects.UNTRANSFUR.get(), 1000, 0, false, false));
            }
            if (getVars(player).showWarns) {
                sendMessage(player, "changedaddon.untransfur.slow_effect");
            }
        } else {
            SummonDripParticlesProcedure.execute(player);
            PlayerUtil.UnTransfurPlayer(player);

            if (getVars(player).resetTransfurAdvancements && player instanceof ServerPlayer sp) {
                resetAdvancement(sp, "minecraft:changed/transfur");
            }

            if (isSurvivalOrAdventure(player)) {
                if (!player.level.isClientSide()) {
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false));
                }
            }

            grantAdvancement(player, "changed_addon:untransfur_advancement_2");

            playSound(world, x, y, z, ChangedAddonSoundEvents.UNTRANSFUR);
        }
    }

    private static void resetAdvancement(ServerPlayer player, String id) {
        Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(id));
        if (adv == null) return;

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
        for (String criteria : progress.getCompletedCriteria()) {
            player.getAdvancements().revoke(adv, criteria);
        }
    }

    private static void grantAdvancement(Player player, String id) {
        if (!(player instanceof ServerPlayer sp)) return;

        Advancement adv = sp.server.getAdvancements().getAdvancement(new ResourceLocation(id));
        if (adv == null) return;

        AdvancementProgress progress = sp.getAdvancements().getOrStartProgress(adv);
        if (!progress.isDone()) {
            for (String criteria : progress.getRemainingCriteria()) {
                sp.getAdvancements().award(adv, criteria);
            }
        }
    }

    private static void playSound(LevelAccessor world, double x, double y, double z, SoundEvent soundEvent) {
        if (world instanceof Level level) {
            if (!level.isClientSide()) {
                level.playSound(null, new BlockPos(x, y, z),
                        soundEvent,
                        SoundSource.NEUTRAL, 1, 1);
            } else {
                level.playLocalSound(x, y, z,
                        soundEvent,
                        SoundSource.NEUTRAL, 1, 1, false);
            }
        }
    }

    private static void sendMessage(Player player, String key) {
        if (!player.level.isClientSide()) {
            player.displayClientMessage(new TranslatableComponent(key), true);
        }
    }

    private static ChangedAddonModVariables.PlayerVariables getVars(Player entity) {
        return ChangedAddonModVariables.PlayerVariables.ofOrDefault(entity);
    }

    private static boolean isSurvivalOrAdventure(Player player) {
        return getGameMode(player) == GameType.SURVIVAL || getGameMode(player) == GameType.ADVENTURE;
    }

    private static boolean isCreativeOrSpectator(Player player) {
        return getGameMode(player) == GameType.CREATIVE || getGameMode(player) == GameType.SPECTATOR;
    }

    private static GameType getGameMode(Player player) {
        if (player instanceof ServerPlayer sp) {
            return sp.gameMode.getGameModeForPlayer();
        }
        if (player.level.isClientSide()) {
            var connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                var info = connection.getPlayerInfo(player.getGameProfile().getId());
                if (info != null) return info.getGameMode();
            }
        }
        return GameType.SURVIVAL; // default fallback
    }
}
