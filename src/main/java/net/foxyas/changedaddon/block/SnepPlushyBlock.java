package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.block.entity.SnepPlushyBlockEntity;
import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonSoundEvents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class SnepPlushyBlock extends AbstractPlushyBlock {

    public static final EnumProperty<CansEnum> CANS = EnumProperty.create("cans", CansEnum.class);

    public SnepPlushyBlock() {
        super(BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(0.5f, 5f)
                .noOcclusion()
                .isRedstoneConductor((bs, br, bp) -> false));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(CANS, CansEnum.NONE));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderLayer() {
        ItemBlockRenderTypes.setRenderLayer(ChangedAddonBlocks.SNEP_PLUSH.get(), renderType -> renderType == RenderType.cutoutMipped());
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public @NotNull VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
        return BlockPathTypes.BLOCKED;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> box(4, 0, 3.5, 12, 16, 12);
            case EAST -> box(4, 0, 4, 12.5, 16, 12);
            case WEST -> box(3.5, 0, 4, 12, 16, 12);
            default -> box(4, 0, 4, 12, 16, 12.5);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, CANS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, flag)
                .setValue(CANS, CansEnum.NONE); // NONE by Default
    }

    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public @NotNull BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        if (state.getValue(WATERLOGGED)) {
            return 0;
        }
        return 20;
    }

    @Override
    public @NotNull InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        var retValue = super.use(blockstate, world, pos, player, hand, hit);

        ItemStack itemInHand = player.getItemInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!world.isClientSide() && blockEntity instanceof SnepPlushyBlockEntity snepPlushyBlockEntity) {
            if (itemInHand.is(Items.GLOW_INK_SAC) && !snepPlushyBlockEntity.glowingEyes) {
                if (!player.isCreative()) {
                    itemInHand.shrink(1);
                }

                snepPlushyBlockEntity.glowingEyes = true;
                world.sendBlockUpdated(pos, blockstate, blockstate, 1);
                snepPlushyBlockEntity.setChanged();

                world.playSound(null, hit.getBlockPos(), SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1, 1);
                if (player instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, itemInHand);
                }

                return InteractionResult.SUCCESS;
            }
        }

        double hitX = hit.getLocation().x;
        double hitY = hit.getLocation().y;
        double hitZ = hit.getLocation().z;
        if (blockEntity instanceof SnepPlushyBlockEntity plushBlockEntity) {
            if (!plushBlockEntity.isSqueezed()) {
                if (!world.isClientSide()) {
                    //plushBlockEntity.squeezedTicks = 4;
                    world.playSound(null, hitX, hitY, hitZ, ChangedAddonSoundEvents.PLUSHY_SOUND, SoundSource.BLOCKS, 1f, 1);
                }
                return InteractionResult.sidedSuccess(world.isClientSide());
            }
        }
        return retValue;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SnepPlushyBlockEntity(pos, state);
    }

    @Override
    public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(blockstate, world, pos, oldState, moving);

        // Chance muito pequena (ex: 45 em 100)
        Random random = new Random();
        if (random.nextInt(100) <= 25) {  // 25% de chance
            // Gerar um valor aleatório para CANS, ignorando NONE
            CansEnum[] possibleValues = {CansEnum.RIGHT, CansEnum.LEFT, CansEnum.HUG, CansEnum.BOTH};
            CansEnum randomCans = possibleValues[random.nextInt(possibleValues.length)];

            // Criar um novo estado de bloco com o valor alterado de CANS
            BlockState newBlockState = blockstate.setValue(CANS, randomCans);

            // Atualizar o estado do bloco no mundo
            world.setBlock(pos, newBlockState, 3);  // Muda o estado do bloco
        }

        world.scheduleTick(pos, this, 10);  // Continua o tick
    }


    @Override
    public void tick(BlockState state, ServerLevel serverLevel, BlockPos pos, Random p_60465_) {
        super.tick(state, serverLevel, pos, p_60465_);
        BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
        if (blockEntity instanceof SnepPlushyBlockEntity snepPlushyBlockEntity && snepPlushyBlockEntity.isSqueezed()) {
            snepPlushyBlockEntity.subSqueezedTicks(1);
        }
        serverLevel.scheduleTick(pos, this, 10);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
    }

    public enum CansEnum implements StringRepresentable {
        NONE("none"),
        RIGHT("right"),
        LEFT("left"),
        HUG("hug"),
        BOTH("both");

        private final String name;

        CansEnum(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
