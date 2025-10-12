package net.foxyas.changedaddon.datagen;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ItemModelProvider extends net.minecraftforge.client.model.generators.ItemModelProvider {

    public ItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ChangedAddonMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicSpawnEgg(ChangedAddonItems.PROTOGEN_0SENIA0_SPAWN_EGG);
    }

    public ItemModelBuilder basicSpawnEgg(RegistryObject<? extends Item> item) {
        return basicSpawnEgg(item.getId());
    }

    public ItemModelBuilder basicSpawnEgg(ResourceLocation item) {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/template_spawn_egg"));
    }
}
