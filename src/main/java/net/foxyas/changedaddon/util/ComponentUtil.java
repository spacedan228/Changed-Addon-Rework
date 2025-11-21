package net.foxyas.changedaddon.util;



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
