package net.foxyas.changedaddon.variant;

import net.foxyas.changedaddon.util.ComponentUtil;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TransfurVariantsInfo {

    public static void addNoOwnerName(ChangedAddonTransfurVariants.VariantWithOwnerMap map, TransfurVariant<?> variant) {
        map.put(variant, ComponentUtil.literal("Free For Use, No Owner"));
    }

    public static void addWithOwnerName(ChangedAddonTransfurVariants.VariantWithOwnerMap map, TransfurVariant<?> variant) {
        map.put(variant, ComponentUtil.literal("Free For Use, No Owner"));
    }

    public static void addUnknownOwnerName(ChangedAddonTransfurVariants.VariantWithOwnerMap map, TransfurVariant<?> variant) {
        map.put(variant, ComponentUtil.literal("Not free for use, Unknown owner"));
    }

    public static void addUnknownOwnerName(ChangedAddonTransfurVariants.VariantWithOwnerMap map, TransfurVariant<?>[] transfurVariants) {
        for (TransfurVariant<?> variant : transfurVariants) {
            map.put(variant, ComponentUtil.literal("Not free for use, Unknown owner"));
        }
    }

    public static void addUnknownOwnerName(ChangedAddonTransfurVariants.VariantWithOwnerMap map, ArrayList<TransfurVariant<?>> transfurVariants) {
        for (TransfurVariant<?> variant : transfurVariants) {
            map.put(variant, ComponentUtil.literal("Not free for use, Unknown owner"));
        }
    }

    public static void addWithOwnerName(ChangedAddonTransfurVariants.VariantWithOwnerMap variants, TransfurVariant<?>[] transfurVariants) {
        for (TransfurVariant<?> variant : transfurVariants) {
            variants.put(variant, ComponentUtil.literal("Free For Use, No Owner"));
        }
    }

    public static void addWithOwnerName(ChangedAddonTransfurVariants.VariantWithOwnerMap variants, ArrayList<TransfurVariant<?>> transfurVariants) {
        for (TransfurVariant<?> variant : transfurVariants) {
            variants.put(variant, ComponentUtil.literal("Free For Use, No Owner"));
        }
    }

    public static void addWithOwnerName(ChangedAddonTransfurVariants.VariantWithOwnerMap map, TransfurVariant<?> variant, Component component) {
        map.put(variant, component);
    }

    public static void addWithOwnerName(ChangedAddonTransfurVariants.VariantWithOwnerMap map, ArrayList<TransfurVariant<?>> variants, Component component) {
        for (TransfurVariant<?> variant : variants) {
            map.put(variant, component);
        }
    }

    public static void addWithOwnerName(ChangedAddonTransfurVariants.VariantWithOwnerMap map, TransfurVariant<?>[] variants, Component component) {
        for (TransfurVariant<?> variant : variants) {
            map.put(variant, component);
        }
    }

    public static void addWithOwnerName(ChangedAddonTransfurVariants.VariantWithOwnerMap map, TransfurVariant<?> variant, String component) {
        map.put(variant, ComponentUtil.literal(component));
    }

    public static void addWithOwnerName(ChangedAddonTransfurVariants.VariantWithOwnerMap map, ArrayList<TransfurVariant<?>> variants, String component) {
        for (TransfurVariant<?> variant : variants) {
            map.put(variant, ComponentUtil.literal(component));
        }
    }

    public static void addWithOwnerName(ChangedAddonTransfurVariants.VariantWithOwnerMap map, TransfurVariant<?>[] variants, String component) {
        for (TransfurVariant<?> variant : variants) {
            map.put(variant, ComponentUtil.literal(component));
        }
    }

    public static void addWithOwnerNameFrom(ChangedAddonTransfurVariants.VariantWithOwnerMap map, @NotNull TransfurVariant<?> from, @NotNull TransfurVariant<?> to) {
        Component component = map.get(from);
        if (component != null) {
            map.put(to, component);
        }
    }
}
