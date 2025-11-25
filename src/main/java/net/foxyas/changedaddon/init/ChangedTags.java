package net.foxyas.changedaddon.init;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ChangedTags extends net.ltxprogrammer.changed.init.ChangedTags {

    //Accessories Tags [is a ITEM TAG]
    public static class AccessoryItems {
        public static final TagKey<Item> FULL_BODY = create("full_body");
        public static final TagKey<Item> BODY = create("body");
        public static final TagKey<Item> LEGS = create("legs");

        private static TagKey<Item> create(String name) {
            return TagKey.create(BuiltInRegistries.ITEM.key(), Changed.modResource(name));
        }
    }

    // Accessories Tags
    public static class AccessoryEntities {
        public static final String HUMANOIDS = "humanoids";
        public static final String HEADLESS_TAURS = "headless_taurs";
        public static final String MER = "mer";
        public static final String NAGA = "naga";
        public static final String TAURS = "taurs";
    }
}
