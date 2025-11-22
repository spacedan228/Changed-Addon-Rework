package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.block.entity.AdvancedCatalyzerBlockEntity;
import net.foxyas.changedaddon.init.ChangedAddonBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AdvancedCatalyzerBlock extends CatalyzerBlock {

    public AdvancedCatalyzerBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE)
                .sound(SoundType.NETHERITE_BLOCK).strength(5f, 25f).lightLevel(s -> 1));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ChangedAddonBlockEntities.ADVANCED_CATALYZER.get(), pLevel.isClientSide ? AdvancedCatalyzerBlockEntity::clientTick : AdvancedCatalyzerBlockEntity::serverTick);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdvancedCatalyzerBlockEntity(pos, state);
    }
}
