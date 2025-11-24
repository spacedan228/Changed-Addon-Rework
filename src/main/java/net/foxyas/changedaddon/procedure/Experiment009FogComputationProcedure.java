package net.foxyas.changedaddon.procedure;

import com.mojang.blaze3d.shaders.FogShape;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = {Dist.CLIENT})
public class Experiment009FogComputationProcedure {

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if(event.getMode() != FogRenderer.FogMode.FOG_TERRAIN) return;

        try {
            ClientLevel clientLevel = Minecraft.getInstance().level;
            Entity entity = event.getCamera().getEntity();
            if(!(entity instanceof LivingEntity living)) return;
            if (clientLevel != null) {
                applyFogEffect(living, event);
                event.setCanceled(true);
            }
        } catch (Exception ignored) {
            // You can log the error here if needed
        }
    }

    private static void applyFogEffect(LivingEntity entity, ViewportEvent viewport) {
        if (entity == null || !(viewport instanceof ViewportEvent.RenderFog fogEvent)) return;
        if (entity.isSpectator() || entity instanceof Player player && player.isCreative()) return;

        if (isHoldingItem(entity, ChangedAddonItems.EXPERIMENT_009_DNA.get()) ||
                isHoldingItem(entity, ChangedAddonItems.EXPERIMENT_10_DNA.get())) {

            fogEvent.setFogShape(FogShape.SPHERE);
            fogEvent.setNearPlaneDistance(1);
            fogEvent.setFarPlaneDistance(10);
        }
    }

    private static boolean isHoldingItem(LivingEntity entity, Item item) {
        return entity.getMainHandItem().getItem() == item ||
                entity.getOffhandItem().getItem() == item;
    }
}
