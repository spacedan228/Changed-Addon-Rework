package net.foxyas.changedaddon.ability.handle;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.ability.PsychicGrab;
import net.foxyas.changedaddon.network.packet.KeyPressPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedAddonMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PsychicGrabKeyHandler {

    @SubscribeEvent
    public static void onKeyPressed(InputEvent.KeyInputEvent event) {
        if (PsychicGrab.Keys.contains(event.getKey()) && Minecraft.getInstance().screen == null) {
            ChangedAddonMod.PACKET_HANDLER.sendToServer(new KeyPressPacket(event.getKey()));
        }
    }
}
