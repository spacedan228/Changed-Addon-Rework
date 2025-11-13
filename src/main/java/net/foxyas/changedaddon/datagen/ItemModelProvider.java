package net.foxyas.changedaddon.datagen;

import com.mojang.authlib.yggdrasil.response.Response;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;

import static net.foxyas.changedaddon.init.ChangedAddonItems.*;

public class ItemModelProvider extends net.minecraftforge.client.model.generators.ItemModelProvider {

    public ItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ChangedAddonMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicSpawnEgg(PROTOGEN_0SENIA0_SPAWN_EGG);
        basicSpawnEgg(LATEX_KAYLA_SHARK_SPAWN_EGG);
        basicSpawnEgg(LATEX_BORDER_COLLIE_SPAWN_EGG);

        layeredItem(KEYCARD_ITEM.get(),
                List.of(
                        ResourceLocation.fromNamespaceAndPath(KEYCARD_ITEM.getId().getNamespace(), "item/" + KEYCARD_ITEM.getId().getPath() + "_base"),
                        ResourceLocation.fromNamespaceAndPath(KEYCARD_ITEM.getId().getNamespace(), "item/" + KEYCARD_ITEM.getId().getPath() + "_top"),
                        ResourceLocation.fromNamespaceAndPath(KEYCARD_ITEM.getId().getNamespace(), "item/" + KEYCARD_ITEM.getId().getPath() + "_bottom")
                )
        );
    }

    public ItemModelBuilder layeredItem(Item item, HashMap<Integer, ResourceLocation> layerTextures) {
        ItemModelBuilder builder = getBuilder(item.toString());

        if (!layerTextures.isEmpty()) {
            layerTextures.forEach(((layer, texture) -> builder.parent(new ModelFile.UncheckedModelFile("item/generated"))
                    .texture("layer" + layer, texture)));
        }

        return builder;
    }

    public ItemModelBuilder layeredItem(ResourceLocation item, List<ResourceLocation> layersTextures) {
        ItemModelBuilder builder = getBuilder(item.toString());

        if (!layersTextures.isEmpty()) {
            for (int i = 0; i < layersTextures.size(); i++) {
                ResourceLocation texture = layersTextures.get(i);
                builder.parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer" + i, texture)
                ;
            }
        }

        return builder;
    }

    public ItemModelBuilder layeredItem(Item item, List<ResourceLocation> layersTextures) {
        ItemModelBuilder builder = getBuilder(item.toString());

        if (!layersTextures.isEmpty()) {
            for (int i = 0; i < layersTextures.size(); i++) {
                ResourceLocation texture = layersTextures.get(i);
                builder.parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer" + i, texture)
                ;
            }
        }

        return builder;
    }

    public ItemModelBuilder basicSpawnEgg(RegistryObject<? extends Item> item) {
        return basicSpawnEgg(item.getId());
    }

    public ItemModelBuilder basicSpawnEgg(ResourceLocation item) {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/template_spawn_egg"));
    }
}
