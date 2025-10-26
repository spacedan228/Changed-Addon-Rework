package net.foxyas.changedaddon.init;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.foxyas.changedaddon.network.LeapKeyMessage;
import net.foxyas.changedaddon.network.OpenExtraDetailsMessage;
import net.foxyas.changedaddon.network.PatKeyMessage;
import net.foxyas.changedaddon.network.TurnOffTransfurMessage;
import net.foxyas.changedaddon.network.packets.VariantSecondAbilityActivate;
import net.foxyas.changedaddon.variants.TransfurVariantInstanceExtensor;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedKeyMappings;
import net.ltxprogrammer.changed.network.ExtraJumpKeybind;
import net.ltxprogrammer.changed.network.VariantAbilityActivate;
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
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new OpenExtraDetailsMessage(0, 0));
                //OpenExtraDetailsMessage.pressAction(Minecraft.getInstance().player, 0, 0);
            }
            isDownOld = isDown;
        }
    };
    public static final KeyMapping LEAP_KEY = new KeyMapping("key.changed_addon.leap_key", GLFW.GLFW_KEY_UNKNOWN, "key.categories.changed_addon") {
        private boolean isDownOld = false;

        @Override
        public void setDown(boolean isDown) {
            super.setDown(isDown);
            if (isDownOld != isDown && isDown) {
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new LeapKeyMessage(0, 0));
                LeapKeyMessage.pressAction(Minecraft.getInstance().player, 0, 0);
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
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new TurnOffTransfurMessage(0, 0));
                TurnOffTransfurMessage.pressAction(Minecraft.getInstance().player, 0, 0);
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
                ChangedAddonMod.PACKET_HANDLER.sendToServer(new PatKeyMessage(0, 0));
                PatKeyMessage.pressAction(Minecraft.getInstance().player, 0, 0);
            }
            //isDownOld = isDown;
        }
    };

    public static final KeyMapping USE_SECOND_ABILITY = new KeyMapping("key.changed_addon.use_second_ability", GLFW.GLFW_KEY_X, "key.categories.movement");

    @SubscribeEvent
    public static void registerKeyBindings(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(OPEN_EXTRA_DETAILS);
        ClientRegistry.registerKeyBinding(LEAP_KEY);
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
                LEAP_KEY.consumeClick();
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
                                        Changed.PACKET_HANDLER.sendToServer(new VariantSecondAbilityActivate(local, newState, transfurVariantInstanceExtensor.getSecondSelectedAbility()));
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
