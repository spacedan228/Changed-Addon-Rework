package net.foxyas.changedaddon.client.gui;

import net.foxyas.changedaddon.util.ComponentUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber
public class TimedKeypadScreen extends Screen {

    private final EditBox textBox;

    public TimedKeypadScreen() {
        super(ComponentUtil.translatable("placeholder"));

        textBox = new EditBox(Minecraft.getInstance().font, 0, 0, 100, 20, ComponentUtil.translatable("placeholder"));
    }

    @Override
    protected void init() {
        textBox.x = width / 2 - textBox.getWidth() / 2;
        textBox.y = (int)(height * .66f) - textBox.getHeight() / 2;
        addRenderableWidget(textBox);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if(!('0' <= pCodePoint && pCodePoint <= '9')) return false;
        return super.charTyped(pCodePoint, pModifiers);
    }

    @SubscribeEvent
    public static void input(InputEvent.KeyInputEvent event){
        if(event.getKey() == GLFW.GLFW_KEY_J && Minecraft.getInstance().screen == null) {
            Minecraft.getInstance().setScreen(new TimedKeypadScreen());
        }
    }
}
