package net.foxyas.changedaddon.variants;

import net.foxyas.changedaddon.init.ChangedAddonAbilities;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class VariantsDetails {

    @SubscribeEvent
    public static void entityJump(LivingEvent.LivingJumpEvent livingJumpEvent) {
        if (livingJumpEvent.getEntityLiving() instanceof Player player) {
            TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
            if (instance != null) {
                if (instance.hasAbility(ChangedAddonAbilities.WIND_CONTROL.get()) || instance.hasAbility(ChangedAddonAbilities.WIND_PASSIVE.get())) {
                    if (!player.isOnGround()) {
                        if (player.getLevel().isClientSide() && player.getLevel() instanceof ClientLevel clientLevel) {
                            Vec3 motion = player.getDeltaMovement();
                            clientLevel.addParticle(ParticleTypes.POOF,
                                    player.position().x(),
                                    player.position().y(),
                                    player.position().x(),
                                    (float) motion.x(),
                                    (float) motion.y(),
                                    (float) motion.z()
                            );
                        }
                    }
                }
            }
        }
    }
}
