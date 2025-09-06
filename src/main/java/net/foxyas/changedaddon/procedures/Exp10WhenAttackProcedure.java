package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.entity.bosses.Experiment10BossEntity;
import net.foxyas.changedaddon.entity.bosses.Experiment10Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class Exp10WhenAttackProcedure {
    @SubscribeEvent
    public static void onEntityAttacked(LivingAttackEvent event) {
        Entity entity = event.getEntity();
        if (entity != null) {
            execute(event, entity.getLevel(), entity, event.getSource().getDirectEntity());
        }
    }

    public static void execute(LevelAccessor world, Entity entity, Entity attacker) {
        execute(null, world, entity, attacker);
    }

    private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, Entity attacker) {
        if (!(entity instanceof LivingEntity living)) return;
        if (!(attacker instanceof Experiment10Entity || attacker instanceof Experiment10BossEntity)) return;

        if (living.isBlocking()) {
            ItemStack shield = living.getUseItem(); // item sendo usado para bloquear

            // Força desativar o escudo
            if (living instanceof Player player) {
                player.disableShield(true);
            }

            // Aplica cooldown (se for player)
            if (living instanceof Player player && !shield.isEmpty()) {
                player.getCooldowns().addCooldown(shield.getItem(), 150);
            }

            // Sons
            if (world instanceof Level level) {
                if (!level.isClientSide()) {
                    level.playSound(null, attacker.blockPosition(),
                            SoundEvents.PLAYER_ATTACK_CRIT,
                            SoundSource.HOSTILE, 1.5f, 1f);

                    level.playSound(null, entity.blockPosition(),
                            SoundEvents.SHIELD_BREAK,
                            SoundSource.PLAYERS, 1.5f, 0.5f);
                }
            }
        }
    }
}
