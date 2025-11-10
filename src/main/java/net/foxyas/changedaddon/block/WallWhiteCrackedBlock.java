package net.foxyas.changedaddon.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.TierSortingRegistry;
import org.jetbrains.annotations.NotNull;

public class WallWhiteCrackedBlock extends Block {

    public WallWhiteCrackedBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ).sound(SoundType.STONE).strength(1.5f, 4.5f).requiresCorrectToolForDrops());
    }

    @Override
    public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos) {
        return 15;
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
        if (player.getInventory().getSelected().getItem() instanceof PickaxeItem tieredItem)
            return TierSortingRegistry.isCorrectTierForDrops(Tiers.WOOD, state) || tieredItem.getTier().getLevel() >= 0;
        return super.canHarvestBlock(state, world, pos, player);
    }
}
