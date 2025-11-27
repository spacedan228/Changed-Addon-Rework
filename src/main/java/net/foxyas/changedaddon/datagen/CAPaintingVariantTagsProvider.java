package net.foxyas.changedaddon.datagen;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonPaintingVariants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CAPaintingVariantTagsProvider extends TagsProvider<PaintingVariant> {

    public CAPaintingVariantTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> provider,
            @Nullable ExistingFileHelper fileHelper
    ) {
        super(output, Registries.PAINTING_VARIANT, provider, ChangedAddonMod.MODID, fileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {

        // Pega todos os ResourceKey dos registros do mod
        List<ResourceKey<PaintingVariant>> addonPaintings =
                ChangedAddonPaintingVariants.PAINTING_TYPES.getEntries()
                        .stream()
                        .map(RegistryObject::getKey)
                        .toList();

        this.tag(PaintingVariantTags.PLACEABLE)
                .add(addonPaintings.toArray(ResourceKey[]::new));
    }
}
