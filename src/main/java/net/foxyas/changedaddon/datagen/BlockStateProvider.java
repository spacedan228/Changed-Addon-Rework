package net.foxyas.changedaddon.datagen;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

import static net.foxyas.changedaddon.init.ChangedAddonBlocks.WOLF_CRYSTAL_PILLAR;

public class BlockStateProvider extends net.minecraftforge.client.model.generators.BlockStateProvider {

    public BlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ChangedAddonMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        pillarBlockWithVariants(WOLF_CRYSTAL_PILLAR, 2, 0);
    }

    private ResourceLocation blockLoc(ResourceLocation loc){
        return new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath());
    }

    private ResourceLocation withSuffix(ResourceLocation loc, String suffix){
        return new ResourceLocation(loc.getNamespace(), loc.getPath() + suffix);
    }

    private void simpleWithVariants(RegistryObject<? extends Block> block, int variants, int itemModelIndex){
        Block b = block.get();
        ResourceLocation loc = withSuffix(blockLoc(block.getId()), "/variant");

        ModelFile[] models = new ModelFile[variants];
        for(int i = 0; i < variants; i++){
            models[i] = models().getExistingFile(withSuffix(loc, "_" + i));
        }

        ConfiguredModel[] confModels = configure(models, ConfiguredModel::new);

        simpleBlock(b, confModels);
        simpleBlockItem(b, models[itemModelIndex]);
    }

    private void pillarBlockWithVariants(RegistryObject<? extends RotatedPillarBlock> pillar, int variants, int itemModelIndex){
        RotatedPillarBlock block = pillar.get();
        ResourceLocation loc = withSuffix(blockLoc(pillar.getId()), "/variant");

        ModelFile[] models = new ModelFile[variants];
        for(int i = 0; i < variants; i++){
            models[i] = models().getExistingFile(withSuffix(loc, "_" + i));
        }

        getVariantBuilder(block).forAllStates(state ->
            switch (state.getValue(BlockStateProperties.AXIS)){
                case Y -> configure(models, ConfiguredModel::new);
                case Z -> configure(models, model -> new ConfiguredModel(model, 90, 0, false));
                case X -> configure(models, model -> new ConfiguredModel(model, 90, 90, false));
            }
        );

        simpleBlockItem(block, models[itemModelIndex]);
    }

    private ConfiguredModel[] configure(ModelFile[] models, Function<ModelFile, ConfiguredModel> config){
        ConfiguredModel[] out = new ConfiguredModel[models.length];
        for(int i = 0; i < models.length; i++){
            out[i] = config.apply(models[i]);
        }
        return out;
    }
}
