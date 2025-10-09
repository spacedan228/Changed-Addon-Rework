package net.foxyas.changedaddon.event;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.item.tooltip.ClientTransfurTotemTooltipComponent;
import net.foxyas.changedaddon.item.tooltip.TransfurTotemTooltipComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = ChangedAddonMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvent {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerToolTips(FMLConstructModEvent event) {
        MinecraftForgeClient.registerTooltipComponentFactory(TransfurTotemTooltipComponent.class, ClientTransfurTotemTooltipComponent::new);
    }
}
