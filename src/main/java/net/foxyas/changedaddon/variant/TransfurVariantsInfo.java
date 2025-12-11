package net.foxyas.changedaddon.variant;

import com.google.common.base.Suppliers;
import net.foxyas.changedaddon.util.ComponentUtil;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;

import static net.foxyas.changedaddon.variant.ChangedAddonTransfurVariants.*;

public class TransfurVariantsInfo {

    public static final Supplier<VariantWithOwnerMap> OCS = Suppliers.memoize(() -> {
        VariantWithOwnerMap variants = new VariantWithOwnerMap();
        TransfurVariantsInfo.addUnknownOwnerName(variants, new TransfurVariant<?>[]{BOREALIS_MALE.get(), BOREALIS_FEMALE.get()});
        TransfurVariantsInfo.addUnknownOwnerName(variants, new TransfurVariant<?>[]{HIMALAYAN_CRYSTAL_GAS_CAT_MALE.get(), HIMALAYAN_CRYSTAL_GAS_CAT_FEMALE.get()});

        TransfurVariantsInfo.addWithOwnerName(variants, new TransfurVariant<?>[]{LATEX_WIND_CAT_MALE.get(), LATEX_WIND_CAT_FEMALE.get()}, ComponentUtil.literal("Species by @BrownBakers"));

        TransfurVariantsInfo.addWithOwnerName(variants, MONGOOSE.get(), "@nopemom (PHOBOS)");
        TransfurVariantsInfo.addWithOwnerName(variants, BLUE_LIZARD.get(), "@V");

        TransfurVariantsInfo.addUnknownOwnerName(variants, FENGQI_WOLF.get());

        TransfurVariantsInfo.addWithOwnerName(variants, FOXTA_FOXY.get(), ComponentUtil.literal("Free for use but made By @Foxyas"));
        TransfurVariantsInfo.addWithOwnerName(variants, SNEPSI_LEOPARD.get(), ComponentUtil.literal("Free for use but made By @Foxyas"));

        TransfurVariantsInfo.addWithOwnerName(variants, HAYDEN_FENNEC_FOX.get(), ComponentUtil.literal("@haydenfencfoxo / @hayden_fencfoxo"));
        TransfurVariantsInfo.addWithOwnerName(variants, REYN.get(), ComponentUtil.literal("@reyn"));
        TransfurVariantsInfo.addWithOwnerName(variants, LYNX.get(), ComponentUtil.literal("@Smoopa"));

        TransfurVariantsInfo.addWithOwnerName(variants, EXPERIMENT_009.get(), ComponentUtil.literal("Free for use but made By @Foxyas"));
        TransfurVariantsInfo.addWithOwnerNameFrom(variants, EXPERIMENT_009.get(), EXPERIMENT_009_BOSS.get());
        TransfurVariantsInfo.addWithOwnerName(variants, EXPERIMENT_10.get(), ComponentUtil.literal("@SuperNovaDragon"));
        TransfurVariantsInfo.addWithOwnerNameFrom(variants, EXPERIMENT_10.get(), EXPERIMENT_10_BOSS.get());
        return variants;
    });

    // Just For organization.
    public static class VariantWithOwnerMap extends HashMap<TransfurVariant<?>, Component> {
    }

    public static void addNoOwnerName(VariantWithOwnerMap map, TransfurVariant<?> variant) {
        map.put(variant, ComponentUtil.literal("Free For Use, No Owner"));
    }

    public static void addUnknownOwnerName(VariantWithOwnerMap map, TransfurVariant<?> variant) {
        map.put(variant, ComponentUtil.literal("Not free for use, Unknown owner"));
    }

    public static void addUnknownOwnerName(VariantWithOwnerMap map, TransfurVariant<?>[] transfurVariants) {
        for (TransfurVariant<?> variant : transfurVariants) {
            map.put(variant, ComponentUtil.literal("Not free for use, Unknown owner"));
        }
    }

    public static void addUnknownOwnerName(VariantWithOwnerMap map, ArrayList<TransfurVariant<?>> transfurVariants) {
        for (TransfurVariant<?> variant : transfurVariants) {
            map.put(variant, ComponentUtil.literal("Not free for use, Unknown owner"));
        }
    }

    public static void addWithOwnerName(VariantWithOwnerMap variants, TransfurVariant<?>[] transfurVariants) {
        for (TransfurVariant<?> variant : transfurVariants) {
            variants.put(variant, ComponentUtil.literal("Free For Use, No Owner"));
        }
    }

    public static void addWithOwnerName(VariantWithOwnerMap variants, ArrayList<TransfurVariant<?>> transfurVariants) {
        for (TransfurVariant<?> variant : transfurVariants) {
            variants.put(variant, ComponentUtil.literal("Free For Use, No Owner"));
        }
    }

    public static void addWithOwnerName(VariantWithOwnerMap map, TransfurVariant<?> variant, Component component) {
        map.put(variant, component);
    }

    public static void addWithOwnerName(VariantWithOwnerMap map, ArrayList<TransfurVariant<?>> variants, Component component) {
        for (TransfurVariant<?> variant : variants) {
            map.put(variant, component);
        }
    }

    public static void addWithOwnerName(VariantWithOwnerMap map, TransfurVariant<?>[] variants, Component component) {
        for (TransfurVariant<?> variant : variants) {
            map.put(variant, component);
        }
    }

    public static void addWithOwnerName(VariantWithOwnerMap map, TransfurVariant<?> variant, String component) {
        map.put(variant, ComponentUtil.literal(component));
    }

    public static void addWithOwnerName(VariantWithOwnerMap map, ArrayList<TransfurVariant<?>> variants, String component) {
        for (TransfurVariant<?> variant : variants) {
            map.put(variant, ComponentUtil.literal(component));
        }
    }

    public static void addWithOwnerName(VariantWithOwnerMap map, TransfurVariant<?>[] variants, String component) {
        for (TransfurVariant<?> variant : variants) {
            map.put(variant, ComponentUtil.literal(component));
        }
    }

    public static void addWithOwnerNameFrom(VariantWithOwnerMap map, @NotNull TransfurVariant<?> from, @NotNull TransfurVariant<?> to) {
        Component component = map.get(from);
        if (component != null) {
            map.put(to, component);
        }
    }
}
