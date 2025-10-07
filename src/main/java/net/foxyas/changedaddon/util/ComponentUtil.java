package net.foxyas.changedaddon.util;

import net.minecraft.network.chat.TextComponent;

public class ComponentUtil {

    public static TextComponent literal(String text) {
        return new TextComponent(text);
    }
}
