package net.foxyas.changedaddon.util;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class ComponentUtil {

    public static TextComponent literal(String text) {
        return new TextComponent(text);
    }

    public static TranslatableComponent translatable(String text) {
        return new TranslatableComponent(text);
    }

    public static TranslatableComponent translatable(String text, Object... args) {
        return new TranslatableComponent(text, args);
    }
}
