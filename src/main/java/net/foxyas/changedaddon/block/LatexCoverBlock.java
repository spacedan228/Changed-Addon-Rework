package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.block.interfaces.RenderLayerProvider;
import net.foxyas.changedaddon.fluid.LitixCamoniaFluid;
import net.foxyas.changedaddon.init.ChangedAddonFluids;
import net.ltxprogrammer.changed.block.NonLatexCoverableBlock;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LatexCoverBlock extends MultifaceBlock implements NonLatexCoverableBlock, RenderLayerProvider {

    private final LatexType latexType;

    public LatexCoverBlock(Properties pProperties, LatexType latexType) {
        super(pProperties);
        this.latexType = latexType;
    }

    public LatexType getLatexType() {
        return latexType;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerRenderLayer() {
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return true;
    }

    private static final Direction[] DIRECTIONS = Direction.values();

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull Random random) {
        BooleanProperty prop, propRelative;
        BlockPos posRelative, attachToPos;
        BlockState stateRelative, attachToState, old, toPlace;
        for (Direction dir : DIRECTIONS) {
            prop = PipeBlock.PROPERTY_BY_DIRECTION.get(dir);
            if (!state.getValue(prop)) continue;

            for (Direction relative : DIRECTIONS) {
                if (relative == dir || relative == dir.getOpposite()) continue;
                propRelative = PipeBlock.PROPERTY_BY_DIRECTION.get(relative);
                if (state.getValue(propRelative)) continue;

                posRelative = pos.relative(relative);
                stateRelative = level.getBlockState(posRelative);

                if (cantReplace(stateRelative)) {
                    //try cover Direction relative of this
                    if (!canAttachTo(level, posRelative, stateRelative, relative.getOpposite())) continue;

                    level.setBlockAndUpdate(pos, state.setValue(propRelative, true));
                } else {
                    //try to create a new cover block on BlockPos posRelative
                    attachToPos = pos.relative(dir).relative(relative);
                    attachToState = level.getBlockState(attachToPos);

                    if (cantReplace(attachToState)) {
                        if (!canAttachTo(level, attachToPos, attachToState, dir.getOpposite())) continue;

                        old = level.getBlockState(posRelative);//make sure that if there is a cover already, it is taken into account
                        toPlace = (old.is(this) && old.getBlock() instanceof LatexCoverBlock latexCoverBlock && latexCoverBlock.getLatexType() == this.getLatexType() ? old : defaultBlockState()).setValue(prop, true);
                        if (old == toPlace) continue;

                        level.setBlockAndUpdate(posRelative, toPlace);
                        return;
                    }

                    attachToPos = pos.relative(dir);
                    if (!canAttachTo(level, attachToPos, level.getBlockState(attachToPos), relative)) continue;

                    posRelative = pos.relative(dir).relative(relative);
                    old = level.getBlockState(posRelative);//make sure that if there is a cover already, it is taken into account
                    toPlace = (old.is(this) && old.getBlock() instanceof LatexCoverBlock latexCoverBlock && latexCoverBlock.getLatexType() == this.getLatexType() ? old : defaultBlockState()).setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(relative.getOpposite()), true);
                    if (old == toPlace) continue;

                    level.setBlockAndUpdate(posRelative, toPlace);
                }

                return;
            }
        }
    }

    protected boolean cantReplace(BlockState state) {
        return !state.getMaterial().isReplaceable();
    }
}
