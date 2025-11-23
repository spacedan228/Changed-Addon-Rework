package net.foxyas.changedaddon.datagen;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonTags;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static net.foxyas.changedaddon.variant.ChangedAddonTransfurVariants.*;

public class TFTagsProvider extends TagsProvider<TransfurVariant<?>> {

    public TFTagsProvider(DataGenerator generator, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), ChangedRegistry.TRANSFUR_VARIANT.get().getRegistryKey(), pLookupProvider, ChangedAddonMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ChangedAddonTags.TransfurTypes.ABLE_TO_CARRY).add(EXP6.get());
        tag(ChangedAddonTags.TransfurTypes.CAUSE_FREEZING).add(LUMINARCTIC_LEOPARD_MALE.get(), LUMINARCTIC_LEOPARD_FEMALE.get());
        tag(ChangedAddonTags.TransfurTypes.GLOWING_VARIANTS).add(EXPERIMENT_009.get(), EXPERIMENT_009_BOSS.get(), EXPERIMENT_10.get(), EXPERIMENT_10_BOSS.get());
        tag(ChangedAddonTags.TransfurTypes.HAS_CLAWS).add(
                LATEX_KITSUNE_FEMALE.get(),
                LATEX_KITSUNE_MALE.get(),
                FENGQI_WOLF.get(),
                LUMINARA_FLOWER_BEAST.get(),
                PROTOGEN_0SENIA0.get(),
                LATEX_KAYLA_SHARK.get()
        );


        tag(ChangedAddonTags.TransfurTypes.AQUATIC_LIKE).add(LATEX_DRAGON_SNEP_SHARK.get());

        addAllMatching(tag(ChangedAddonTags.TransfurTypes.SHARK_LIKE), var -> var.getRegistryName().getPath().contains("shark")).add(ChangedTransfurVariants.LATEX_ORCA.get(),
                ChangedTransfurVariants.LATEX_MANTA_RAY_FEMALE.get(),
                ChangedTransfurVariants.LATEX_MANTA_RAY_MALE.get());

        addAllMatching(tag(ChangedAddonTags.TransfurTypes.CAT_LIKE), var -> var.getRegistryName().getPath().contains("cat")).add(EXPERIMENT_10.get(),
                MIRROR_WHITE_TIGER.get(),
                LUMINARCTIC_LEOPARD_MALE.get(),
                LUMINARCTIC_LEOPARD_FEMALE.get(),
                LYNX.get(),
                SNEPSI_LEOPARD.get(),
                LATEX_CHEETAH_MALE.get(),
                LATEX_CHEETAH_FEMALE.get(),
                ChangedTransfurVariants.LATEX_STIGER.get(),
                ChangedTransfurVariants.LATEX_WHITE_TIGER.get());

        addAllMatching(tag(ChangedAddonTags.TransfurTypes.DRAGON_LIKE), var -> var.getRegistryName().getPath().contains("dragon")).add(LUMINARA_FLOWER_BEAST.get());

        addAllMatching(tag(ChangedAddonTags.TransfurTypes.FOX_LIKE), var -> var.getRegistryName().getPath().contains("fox")).add(EXP1_MALE.get(),
                EXP1_FEMALE.get(),
                EXPERIMENT_009.get(),
                EXPERIMENT_009_BOSS.get(),
                LATEX_KITSUNE_MALE.get(),
                LATEX_KITSUNE_FEMALE.get());

        addAllMatching(tag(ChangedAddonTags.TransfurTypes.LEOPARD_LIKE), var -> var.getRegistryName().getPath().contains("leopard"))
                .add(ChangedTransfurVariants.LATEX_RED_PANDA.get(),
                        ChangedTransfurVariants.LATEX_RACCOON.get(),
                        EXP2_FEMALE.get(),
                        EXP2_MALE.get(),
                        EXP6.get(),
                        LATEX_SNEP.get(),
                        LATEX_SNEP_FERAL_FORM.get(),
                        LYNX.get(),
                        LATEX_DRAGON_SNEP_SHARK.get(),
                        BOREALIS_MALE.get(),
                        BOREALIS_FEMALE.get())
        ;

        addAllMatching(tag(ChangedAddonTags.TransfurTypes.WOLF_LIKE), var -> {
            String path = var.getRegistryName().getPath();
            return path.contains("dog") || path.contains("wolf");
        }).add(ChangedTransfurVariants.LATEX_PURPLE_FOX.get(), LATEX_SQUID_TIGER_SHARK.get());


        tag(ChangedAddonTags.TransfurTypes.AQUATIC_DIET)
                .addTag(ChangedAddonTags.TransfurTypes.AQUATIC_LIKE);

        tag(ChangedAddonTags.TransfurTypes.SHARK_DIET)
                .addTag(ChangedAddonTags.TransfurTypes.SHARK_LIKE);

        tag(ChangedAddonTags.TransfurTypes.CAT_DIET)
                .addTags(ChangedAddonTags.TransfurTypes.CAT_LIKE, ChangedAddonTags.TransfurTypes.LEOPARD_LIKE);

        tag(ChangedAddonTags.TransfurTypes.DRAGON_DIET)
                .addTag(ChangedAddonTags.TransfurTypes.DRAGON_LIKE);

        tag(ChangedAddonTags.TransfurTypes.FOX_DIET)
                .addTag(ChangedAddonTags.TransfurTypes.FOX_LIKE);

        tag(ChangedAddonTags.TransfurTypes.SPECIAL_DIET)
                .add(EXP6.get(), WOLFY.get(), PURO_KIND_FEMALE.get(), PURO_KIND_MALE.get());

        tag(ChangedAddonTags.TransfurTypes.SWEET_DIET)
                .add(HAYDEN_FENNEC_FOX.get(), EXPERIMENT_009.get(), EXPERIMENT_009_BOSS.get());

        tag(ChangedAddonTags.TransfurTypes.WOLF_DIET)
                .addTag(ChangedAddonTags.TransfurTypes.WOLF_LIKE).add(BLUE_LIZARD.get());

        tag(ChangedAddonTags.TransfurTypes.NO_DIET).add(REYN.get());
    }

    protected TagAppender<TransfurVariant<?>> addAllMatching(TagAppender<TransfurVariant<?>> tag, Predicate<TransfurVariant<?>> predicate) {
        for (TransfurVariant<?> var : ChangedRegistry.TRANSFUR_VARIANT.get().getValues()) {
            if (predicate.test(var)) tag.add(var);
        }

        return tag;
    }

    @Override
    public @NotNull String getName() {
        return "Transfur Type Tags";
    }
}
