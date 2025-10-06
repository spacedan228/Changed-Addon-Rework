package net.foxyas.changedaddon.datagen;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.block.advanced.TimedKeypad;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

import static net.foxyas.changedaddon.init.ChangedAddonBlocks.*;

public class BlockStateProvider extends net.minecraftforge.client.model.generators.BlockStateProvider {

    public BlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ChangedAddonMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlock(ADVANCED_CATALYZER);
        horizontalBlock(ADVANCED_UNIFUSER);
        horizontalBlock(CATALYZER);
        horizontalBlock(UNIFUSER);
        simpleBlock(BLUE_WOLF_CRYSTAL_BLOCK);
        simpleBlock(BLUE_WOLF_CRYSTAL_SMALL);
        simpleBlock(CONTAINMENT_CONTAINER, BlockStateProperties.WATERLOGGED);
        horizontalBlock(DARK_LATEX_PUDDLE);
        simpleBlock(PAINITE_BLOCK);
        simpleBlock(DEEPSLATE_PAINITE_ORE);
        simpleBlock(DORMANT_DARK_LATEX);
        simpleBlock(DORMANT_WHITE_LATEX);
        horizontalBlock(FOXTA_CAN, BlockStateProperties.WATERLOGGED);
        simpleBlock(GENERATOR);
        simpleBlock(GOO_CORE);
        horizontalBlock(INFORMANT_BLOCK);
        simpleBlock(IRIDIUM_BLOCK);
        simpleBlock(IRIDIUM_ORE);
        simpleBlock(LATEX_INSULATOR);
        simpleBlock(LITIX_CAMONIA_FLUID, BlockStateProperties.LEVEL);
        simpleBlock(LUMINARA_BLOOM);
        simpleBlock(ORANGE_WOLF_CRYSTAL_BLOCK);
        simpleBlock(ORANGE_WOLF_CRYSTAL_SMALL);
        simpleBlock(REINFORCED_CROSS_BLOCK);
        simpleBlock(REINFORCED_WALL);
        simpleBlock(REINFORCED_WALL_CAUTION);
        simpleBlock(REINFORCED_WALL_SILVER_STRIPED);
        simpleBlock(REINFORCED_WALL_SILVER_TILED);
        horizontalBlock(SNEPSI_CAN, BlockStateProperties.WATERLOGGED);
        simpleBlock(WALL_WHITE_CRACKED);
        simpleBlock(WHITE_WOLF_CRYSTAL_BLOCK);
        simpleBlock(WHITE_WOLF_CRYSTAL_SMALL);
        horizontalBlock(WOLF_PLUSH);
        horizontalBlock(DARK_LATEX_WOLF_PLUSH);
        simpleBlock(YELLOW_WOLF_CRYSTAL_BLOCK);
        simpleBlock(YELLOW_WOLF_CRYSTAL_SMALL);
        simpleBlock(POTTED_LUMINARA_BLOOM);

        timedKeypad();

        pillarBlockWithVariants(WOLF_CRYSTAL_PILLAR, 2, 0);
        createMultiface(COVER_BLOCK);
        createMultiface(DARK_LATEX_COVER_BLOCK);
    }

    private final Property<?>[] IGNORE_LATEX = new Property[]{AbstractLatexBlock.COVERED};

    private Property<?>[] makeIgnore(Property<?>... ignore){
        Property<?>[] ignore1;
        if(ignore == null || ignore.length == 0){
            ignore1 = IGNORE_LATEX;
        } else {
            ignore1 = new Property[ignore.length + 1];
            System.arraycopy(ignore, 0, ignore1, 0, ignore.length);
            ignore1[ignore1.length - 1] = AbstractLatexBlock.COVERED;
        }

        return ignore1;
    }

    private void timedKeypad(){
        ResourceLocation loc = blockLoc(TIMED_KEYPAD.getId());
        ModelFile file = models().getExistingFile(loc);
        ModelFile locked = models().getExistingFile(withSuffix(loc, "_locked"));

        getVariantBuilder(TIMED_KEYPAD.get()).forAllStatesExcept(state ->
            new ConfiguredModel[]{new ConfiguredModel(state.getValue(TimedKeypad.POWERED) ? file : locked, 0,
                    (int) ((state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 270) % 360), false)},
        IGNORE_LATEX);
    }

    private ResourceLocation blockLoc(ResourceLocation loc){
        return new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath());
    }

    private ResourceLocation withSuffix(ResourceLocation loc, String suffix){
        return new ResourceLocation(loc.getNamespace(), loc.getPath() + suffix);
    }

    private void simpleBlock(RegistryObject<? extends Block> block, Property<?>... ignore){
        ConfiguredModel[] model = new ConfiguredModel[]{new ConfiguredModel(models().getExistingFile(blockLoc(block.getId())))};
        ignore = makeIgnore(ignore);

        getVariantBuilder(block.get()).forAllStatesExcept(state -> model, ignore);
    }

    private void horizontalBlock(RegistryObject<? extends HorizontalDirectionalBlock> block, Property<?>... ignore){
        ResourceLocation loc = blockLoc(block.getId());
        Block bl = block.get();
        ModelFile file = models().getExistingFile(loc);
        ignore = makeIgnore(ignore);

        getVariantBuilder(bl).forAllStatesExcept(state ->
                        ConfiguredModel.builder().modelFile(file)
                        .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                        .build(),
        ignore);
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

        getVariantBuilder(block).forAllStatesExcept(state ->
            switch (state.getValue(BlockStateProperties.AXIS)){
                case Y -> configure(models, ConfiguredModel::new);
                case Z -> configure(models, model -> new ConfiguredModel(model, 90, 0, false));
                case X -> configure(models, model -> new ConfiguredModel(model, 90, 90, false));
            },
        AbstractLatexBlock.COVERED);

        simpleBlockItem(block, models[itemModelIndex]);
    }

    private ConfiguredModel[] configure(ModelFile[] models, Function<ModelFile, ConfiguredModel> config){
        ConfiguredModel[] out = new ConfiguredModel[models.length];
        for(int i = 0; i < models.length; i++){
            out[i] = config.apply(models[i]);
        }
        return out;
    }

    private void createMultiface(RegistryObject<? extends Block> block) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block.get());
        ResourceLocation loc = blockLoc(block.getId());

        BlockState state = block.get().defaultBlockState();
        ModelFile model = models().getExistingFile(loc);
        for (Direction dir : Direction.values()) {
            BooleanProperty prop = PipeBlock.PROPERTY_BY_DIRECTION.get(dir);
            if (!state.hasProperty(prop)) continue;

            builder.part()
                    .modelFile(model)
                    .rotationX(getXRotation(dir))
                    .rotationY(getYRotation(dir))
                    .addModel()
                    .condition(prop, true);
        }

        itemModels().getBuilder(block.get().asItem().getRegistryName().getPath()).parent(model);
    }

    private static int getXRotation(Direction dir) {
        return switch (dir) {
            case DOWN -> -90;
            case UP -> 90;
            default -> 0;
        };
    }

    private static int getYRotation(Direction dir) {
        return switch (dir) {
            case NORTH -> 180;
            case EAST -> 270;
            case WEST -> 90;
            default -> 0;
        };
    }
}
