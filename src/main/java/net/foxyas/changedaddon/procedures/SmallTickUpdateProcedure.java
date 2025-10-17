package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.entity.advanced.LatexSnowFoxFoxyasEntity;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber
public class SmallTickUpdateProcedure {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            execute(event, event.player.level, event.player);
        }
    }

    private static void execute(@Nullable Event ignoredEvent, LevelAccessor world, Entity entity) {
        if (entity == null)
            return;

        final Vec3 center = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        List<Entity> entityList = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(2), e -> true)
                .stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(center))).toList();
        for (Entity entityIterator : entityList) {
            if (entityIterator != entity && entityIterator instanceof LatexSnowFoxFoxyasEntity) {
                if (entity instanceof ServerPlayer _player) {
                    Advancement _adv = _player.server.getAdvancements().getAdvancement(ResourceLocation.parse("changed_addon:gooey_friend"));
                    AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(Objects.requireNonNull(_adv));
                    if (!_ap.isDone()) {
                        for (String s : _ap.getRemainingCriteria()) _player.getAdvancements().award(_adv, s);
                    }
                }
            }
        }

        if (entity instanceof Player player) {
            TransfurVariantInstance<?> variant = ProcessTransfur.getPlayerTransfurVariant(player);
            if (variant != null) {
                player.getCapability(ChangedAddonVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                    capability.areDarkLatex = variant.getFormId().toString().contains("dark_latex") || variant.getFormId().toString().contains("puro_kind");
                    capability.syncPlayerVariables(entity);
                });
            }
        }
    }
}
