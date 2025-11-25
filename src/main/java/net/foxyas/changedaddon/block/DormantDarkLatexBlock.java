package net.foxyas.changedaddon.block;

import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.block.TransfurCrystalBlock;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.IPlantable;
import org.jetbrains.annotations.NotNull;

public class DormantDarkLatexBlock extends AbstractLatexBlock {

    public DormantDarkLatexBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.CLAY).mapColor(MapColor.COLOR_GRAY).sound(SoundType.SLIME_BLOCK).noLootTable()
                .strength(1.0F, 4.0F), ChangedLatexTypes.DARK_LATEX, ChangedItems.DARK_LATEX_GOO);
    }

    public @NotNull LatexCoverState getLatexCoverState(BlockState blockState, BlockPos blockPos) {
        return ChangedLatexTypes.NONE.get().sourceCoverState();
    }

    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        BlockState plant = plantable.getPlant(world, pos.relative(facing));
        return plant.getBlock() instanceof TransfurCrystalBlock;
    }

}
