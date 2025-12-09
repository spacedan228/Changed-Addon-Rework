package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.block.entity.SignalBlockBlockEntity;
import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.init.ChangedAddonParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@ParametersAreNonnullByDefault
public class SignalBlockBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final EnumProperty<SignalVariant> VARIANT = EnumProperty.create("variant", SignalVariant.class);
    private boolean addedParticle = false;

    public SignalBlockBlock() {
        super(BlockBehaviour.Properties
                .of(Material.STONE)
                .sound(SoundType.STONE)
                .strength(1.5f, 20f)
                .lightLevel(s -> 8)
                .hasPostProcess((bs, br, bp) -> true)
                .emissiveRendering((bs, br, bp) -> true)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, SignalVariant.VARIANT_1).setValue(FACING, Direction.NORTH));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderLayer() {
        ItemBlockRenderTypes.setRenderLayer(ChangedAddonBlocks.SIGNAL_BLOCK.get(), renderType -> renderType == RenderType.cutoutMipped());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VARIANT);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(VARIANT, SignalVariant.VARIANT_1).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);
        LocalPlayer player = Minecraft.getInstance().player;
        if (!addedParticle && player != null) {
            if (player.getMainHandItem().is(ChangedAddonItems.SIGNAL_CATCHER.get()) || player.getOffhandItem().is(ChangedAddonItems.SIGNAL_CATCHER.get())) {
                pLevel.addParticle(ChangedAddonParticleTypes.signal(8, new ItemStack(ChangedAddonItems.SIGNAL_CATCHER.get())), pPos.getX(), pPos.getY(), pPos.getZ(), 0, 0, 0);
                addedParticle = !addedParticle;
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, world, pos, oldState, moving);

        if (!world.isClientSide) { // Evita executar no lado do cliente
            if (world.random.nextInt(100) < 25) {  // 25% de chance
                List<SignalVariant> variants = Arrays.stream(SignalVariant.values())
                        .toList();
                SignalVariant randomVariant = variants.get(world.random.nextInt(variants.size()));
                BlockState updated = state.setValue(VARIANT, randomVariant);
                world.setBlock(pos, updated, 3);
            }
        }

        world.scheduleTick(pos, this, 10); // Se quiser tick contínuo
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState below = level.getBlockState(belowPos);
        return below.isFaceSturdy(level, belowPos, Direction.UP);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true); // dropa o item ao quebrar
        }
        super.neighborChanged(state, level, pos, block, fromPos, moving);
    }


    @Override
    public @NotNull VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> box(3, 0, 3, 13, 3.5, 13);
            case EAST -> box(3, 0, 3, 13, 3.5, 13);
            case WEST -> box(3, 0, 3, 13, 3.5, 13);
            default -> box(3, 0, 3, 13, 3.5, 13);
        };
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SignalBlockBlockEntity(pos, state);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SignalBlockBlockEntity be) {
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    public enum SignalVariant implements StringRepresentable {
        DEFAULT("default"),
        VARIANT_1("variant_1"),
        VARIANT_2("variant_2"),
        VARIANT_3("variant_3"),
        VARIANT_4("variant_4"),
        VARIANT_5("variant_5");

        private final String name;

        SignalVariant(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}
