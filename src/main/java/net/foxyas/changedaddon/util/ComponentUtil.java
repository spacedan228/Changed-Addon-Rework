package net.foxyas.changedaddon.util;



public class ComponentUtil {

    public static TextComponent literal(String text) {
        return Component.literal(text);
    }

    public static TranslatableComponent translatable(String text) {
        return Component.translatable(text);
    }

    public static TranslatableComponent translatable(String text, Object... args) {
        return Component.translatable(text, args);
    }
}
