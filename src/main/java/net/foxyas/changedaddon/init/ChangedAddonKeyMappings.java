package net.foxyas.changedaddon.init;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.foxyas.changedaddon.network.packet.OpenExtraDetailsPacket;
import net.foxyas.changedaddon.network.packet.PatKeyPacket;
import net.foxyas.changedaddon.network.packet.TurnOffTransfurPacket;
import net.foxyas.changedaddon.network.packet.VariantSecondAbilityActivate;
import net.foxyas.changedaddon.variant.TransfurVariantInstanceExtensor;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.tutorial.ChangedTutorial;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class ChangedAddonKeyMappings {

    public static final KeyMapping OPEN_EXTRA_DETAILS = new KeyMapping("key.changed_addon.open_extra_details", GLFW.GLFW_KEY_UNKNOWN, "key.categories.changed_addon") {
        private boolean isDownOld = false;

        @Override
        public void setDown(boolean isDown) {
            super.setDown(isDown);
            if (isDownOld != isDown && isDown) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new OpenExtraDetailsPacket(0, 0));
                //OpenExtraDetailsMessage.pressAction(Minecraft.getInstance().player, 0, 0);
            }
            isDownOld = isDown;
        }
    };

    public static final KeyMapping TURN_OFF_TRANSFUR = new KeyMapping("key.changed_addon.turn_off_transfur", GLFW.GLFW_KEY_UNKNOWN, "key.categories.changed_addon") {
        private boolean isDownOld = false;

        @Override
        public void setDown(boolean isDown) {
            super.setDown(isDown);
            if (isDownOld != isDown && isDown) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new TurnOffTransfurPacket(0, 0));
                TurnOffTransfurPacket.pressAction(Minecraft.getInstance().player, 0);
            }
            isDownOld = isDown;
        }
    };

    public static final KeyMapping PAT_KEY = new KeyMapping("key.changed_addon.pat_key", GLFW.GLFW_KEY_UNKNOWN, "key.categories.changed_addon") {
        //private boolean isDownOld = false;
        // Foxyas here.. i'm going to allow the player to hold the key to Spam Pats, the packet is too small to cause any harm
        @Override
        public void setDown(boolean isDown) {
            super.setDown(isDown);
            if (isDown) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new PatKeyPacket(0, 0));
                PatKeyPacket.pressAction(Minecraft.getInstance().player, 0);
            }
            //isDownOld = isDown;
        }
    };

    public static final KeyMapping USE_SECOND_ABILITY = new KeyMapping("key.changed_addon.use_second_ability", GLFW.GLFW_KEY_X, "key.categories.movement");

    @SubscribeEvent
    public static void registerKeyBindings(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(OPEN_EXTRA_DETAILS);
        ClientRegistry.registerKeyBinding(TURN_OFF_TRANSFUR);
        ClientRegistry.registerKeyBinding(PAT_KEY);
        ClientRegistry.registerKeyBinding(USE_SECOND_ABILITY);
    }

    @Mod.EventBusSubscriber({Dist.CLIENT})
    public static class KeyEventListener {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (Minecraft.getInstance().screen == null) {
                OPEN_EXTRA_DETAILS.consumeClick();
                TURN_OFF_TRANSFUR.consumeClick();
                PAT_KEY.consumeClick();
            }
        }

        @SubscribeEvent
        public static void onKeyInput(InputEvent.KeyInputEvent event) {
            LocalPlayer local = Minecraft.getInstance().player;
            Options options = Minecraft.getInstance().options;
            if (local != null) {
                if (Minecraft.getInstance().screen == null) {
                    if (ChangedAddonServerConfiguration.ALLOW_SECOND_ABILITY_USE.get()) {
                        if (event.getKey() == USE_SECOND_ABILITY.getKey().getValue()) {
                            USE_SECOND_ABILITY.consumeClick();
                            ProcessTransfur.ifPlayerTransfurred(local, (variant) -> {
                                if (!variant.isTemporaryFromSuit() && variant instanceof TransfurVariantInstanceExtensor transfurVariantInstanceExtensor) {
                                    boolean newState = event.getAction() != 0;
                                    if (newState != transfurVariantInstanceExtensor.getSecondAbilityKeyState()) {
                                        ChangedTutorial.triggerOnUseAbility(variant.getSelectedAbility());
                                        transfurVariantInstanceExtensor.setSecondAbilityKeyState(newState);
                                        ChangedAddonMod.PACKET_HANDLER.sendToServer(new VariantSecondAbilityActivate(local, newState, transfurVariantInstanceExtensor.getSecondSelectedAbility()));
                                    }

                                }
                            });
                        }
                    }
                }
            }
        }
    }
}
