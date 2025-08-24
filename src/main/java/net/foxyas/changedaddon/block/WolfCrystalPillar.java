package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.ltxprogrammer.changed.block.NonLatexCoverableBlock;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.NotNull;

public class WolfCrystalPillar extends RotatedPillarBlock implements NonLatexCoverableBlock {

    public WolfCrystalPillar() {
        super(Properties.of(Material.ICE_SOLID)
                .friction(0.98F)
                .sound(SoundType.AMETHYST)
                .strength(2.0F, 2.0F).noOcclusion().requiresCorrectToolForDrops());
    }

    @Override
    public boolean skipRendering(@NotNull BlockState pState, BlockState pAdjacentBlockState, @NotNull Direction pSide) {
        return pAdjacentBlockState.is(this) || super.skipRendering(pState, pAdjacentBlockState, pSide);
    }

    @Override
    public float getShadeBrightness(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState pState, @NotNull BlockGetter pReader, @NotNull BlockPos pPos) {
        return true;
    }

    @Override
    public int getLightBlock(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos) {
        return 1;
    }

    public static void registerRenderLayer(){
        ItemBlockRenderTypes.setRenderLayer(ChangedAddonBlocks.WOLF_CRYSTAL_PILLAR.get(), renderType -> renderType == RenderType.translucent());
    }
}
