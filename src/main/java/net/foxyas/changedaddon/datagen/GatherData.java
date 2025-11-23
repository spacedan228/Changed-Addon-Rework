package net.foxyas.changedaddon.datagen;

//import net.foxyas.changedaddon.datagen.lang.ENLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GatherData {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
//        DataGenerator generator = event.getGenerator();
//        ExistingFileHelper helper = event.getExistingFileHelper();
//        PackOutput packOutput = generator.getPackOutput();
//        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
//        CompletableFuture<HolderLookup.Provider> parentProvider = lookupProvider.get();
//
//        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
//
//        BlockTagsProvider blocks = new BlockTagsProvider(generator, lookupProvider, helper);
//        generator.addProvider(true, blocks);
//        generator.addProvider(true, new ItemTagsProvider(generator, lookupProvider, blocks));
//        generator.addProvider(true, new FluidTagsProvider(generator, helper));
//
//        generator.addProvider(true, new EntityTypeTagsProvider(generator, lookupProvider, helper));
//        generator.addProvider(true, new TFTagsProvider(generator, lookupProvider, helper));
//        generator.addProvider(true, new AccessoryEntityProvider(generator));
//
//        generator.addProvider(true, new RecipeProvider(generator));
//
//        generator.addProvider(true, new LootTableProvider(generator));
//
//        generator.addProvider(true, new BlockStateProvider(generator, helper));
//        generator.addProvider(true, new ItemModelProvider(generator, helper));
//        //generator.addProvider(new AdvancementProvider(generator, helper));
//
//        generator.addProvider(true, new ENLanguageProvider(generator));
    }
}
