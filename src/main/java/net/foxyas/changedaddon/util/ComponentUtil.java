package net.foxyas.changedaddon.util;


import net.minecraft.network.chat.Component;

public class ComponentUtil {

    public static Component literal(String text) {
        return Component.literal(text);
    }

    public static Component translatable(String text) {
        return Component.translatable(text);
    }

    public static Component translatable(String text, Object... args) {
        return Component.translatable(text, args);
    }
}
