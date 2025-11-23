package net.foxyas.changedaddon.datagen;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static net.foxyas.changedaddon.init.ChangedAddonFluids.FLOWING_LITIX_CAMONIA_FLUID;
import static net.foxyas.changedaddon.init.ChangedAddonFluids.LITIX_CAMONIA_FLUID;

public class FluidTagsProvider extends net.minecraft.data.tags.FluidTagsProvider {

    public FluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookup, ChangedAddonMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        tag(FluidTags.WATER).add(LITIX_CAMONIA_FLUID.get(), FLOWING_LITIX_CAMONIA_FLUID.get());
    }
}
