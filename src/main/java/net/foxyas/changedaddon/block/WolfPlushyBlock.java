package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.block.entity.WolfPlushyBlockEntity;
import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class WolfPlushyBlock extends AbstractPlushyBlock {

    public WolfPlushyBlock() {
        super(BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(0.5f, 5f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderLayer() {
        ItemBlockRenderTypes.setRenderLayer(ChangedAddonBlocks.WOLF_PLUSH.get(), renderType -> renderType == RenderType.cutoutMipped());
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> box(4, 0, 3.5, 12, 18, 12);
            case EAST -> box(4, 0, 4, 12.5, 18, 12);
            case WEST -> box(3.5, 0, 4, 12, 18, 12);
            default -> box(4, 0, 4, 12, 18, 12.5);
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WolfPlushyBlockEntity(pos, state);
    }
}
