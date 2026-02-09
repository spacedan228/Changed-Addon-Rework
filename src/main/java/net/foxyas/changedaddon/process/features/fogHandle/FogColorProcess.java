package net.foxyas.changedaddon.process.features.fogHandle;

import net.foxyas.changedaddon.entity.bosses.Experiment009BossEntity;
import net.foxyas.changedaddon.entity.bosses.Experiment10BossEntity;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = {Dist.CLIENT})
public class FogColorProcess {

    private static final float[] COLOR_WHITE = {1f, 1f, 1f};
    private static final float[] COLOR_10 = {61 / 255.0f, 0f, 0f};
    private static final float[] COLOR_009 = {0f, 194 / 255.0f, 219 / 255.0f};
    private static final float[] COLOR_MIX = {126 / 255.0f, 0f, 217 / 255.0f};

    @SubscribeEvent
    public static void computeFogColor(EntityViewRenderEvent.FogColors event) {
        try {
            ClientLevel clientLevel = Minecraft.getInstance().level;
            Entity entity = event.getCamera().getEntity();
            if (entity != null) {
                applyFogColor(clientLevel, entity.position(), entity, event);
            }
        } catch (Exception ignored) {
        }
    }

    protected static void applyFogColor(LevelAccessor world, Vec3 pos, Entity entity, EntityViewRenderEvent.FogColors viewport) {
        if (!(entity instanceof LivingEntity living)) return;
        if (isInCreativeOrSpectator(entity)) return;
        if (!ClientFogData.FOG.isActive()) return;

        boolean has10DNA = hasItem(living, ChangedAddonItems.EXPERIMENT_10_DNA.get());
        boolean has009DNA = hasItem(living, ChangedAddonItems.EXPERIMENT_009_DNA.get());

        // Fog by item
        if (has10DNA && !has009DNA) {
            setFogColor(COLOR_10);
        } else if (has009DNA && !has10DNA) {
            setFogColor(COLOR_009);
        } else if (has10DNA && has009DNA) {
            setFogColor(COLOR_MIX);
        } else {
            setFogColor(COLOR_WHITE);
        }

        // Fog by nearby boss entities
        if (!entity.getPersistentData().getBoolean("NoAI")) {
            if (isEntityNearby(world, pos, Experiment10BossEntity.class, 50)) {
                setFogColor(COLOR_10);
            }
            if (isEntityNearby(world, pos, Experiment009BossEntity.class, 50)) {
                setFogColor(COLOR_009);
            }
        }

        lerpFogColor(viewport);
    }

    protected static boolean hasItem(LivingEntity entity, net.minecraft.world.item.Item item) {
        return entity.getMainHandItem().is(item) || entity.getOffhandItem().is(item);
    }

    protected static boolean isEntityNearby(LevelAccessor world, Vec3 pos, Class<? extends Entity> clazz, double range) {
        AABB box = AABB.ofSize(pos, range, range, range);
        return world.getEntitiesOfClass(clazz, box, e -> true).stream().findAny().isPresent();
    }

    protected static void setFogColor(float[] rgb) {
        if (!sameColor(ClientFogData.FOG.targetColorRgb, rgb)) {
            ClientFogData.FOG.targetColorRgb0 = ClientFogData.FOG.targetColorRgb;
            ClientFogData.FOG.setTargetColor(rgb);
        }
    }

    private static boolean sameColor(float[] a, float[] b) {
        if (a == null || b == null) return false;
        return Math.abs(a[0] - b[0]) < 0.001f
                && Math.abs(a[1] - b[1]) < 0.001f
                && Math.abs(a[2] - b[2]) < 0.001f;
    }

    protected static void lerpFogColor(EntityViewRenderEvent.FogColors fog) {
//        float partialTicks = ClientFogData.FOG.get();
//        float[] rgb = ClientFogData.FOG.getLerpColor();
//
//        float r = rgb[0]; //lerp(fog.getRed(), rgb[0], partialTicks);
//        float g = rgb[1]; //lerp(fog.getGreen(), rgb[1], partialTicks);
//        float b = rgb[2]; //lerp(fog.getBlue(), rgb[2], partialTicks);


        float[] rgb = ClientFogData.FOG.targetColorRgb;
        float t = ClientFogData.FOG.getColor();

        float r = FogLerpState.lerp(fog.getRed(),   rgb[0], t);
        float g = FogLerpState.lerp(fog.getGreen(), rgb[1], t);
        float b = FogLerpState.lerp(fog.getBlue(),  rgb[2], t);

        fog.setRed(r);
        fog.setGreen(g);
        fog.setBlue(b);
    }

    protected static boolean isInCreativeOrSpectator(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            GameType type = serverPlayer.gameMode.getGameModeForPlayer();
            return type == GameType.CREATIVE || type == GameType.SPECTATOR;
        } else if (entity.level.isClientSide() && entity instanceof Player player) {
            var info = Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId());
            if (info != null) {
                GameType type = info.getGameMode();
                return type == GameType.CREATIVE || type == GameType.SPECTATOR;
            }
        }
        return false;
    }
}
