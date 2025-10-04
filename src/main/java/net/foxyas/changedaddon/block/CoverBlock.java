package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.ltxprogrammer.changed.block.NonLatexCoverableBlock;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CoverBlock extends MultifaceBlock implements NonLatexCoverableBlock {

    public CoverBlock(Properties pProperties) {
        super(pProperties);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderLayer() {
        ItemBlockRenderTypes.setRenderLayer(ChangedAddonBlocks.COVER_BLOCK.get(), renderType -> renderType == RenderType.translucent());
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return true;
    }

    private static final Direction[] DIRECTIONS = Direction.values();

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull Random random) {
        BooleanProperty prop, propRelative;
        BlockPos posRelative, attachToPos;
        BlockState stateRelative, attachToState, old, toPlace;
        for(Direction dir : DIRECTIONS){
            prop = PipeBlock.PROPERTY_BY_DIRECTION.get(dir);
            if(!state.getValue(prop)) continue;

            for(Direction relative : DIRECTIONS){
                if(relative == dir || relative == dir.getOpposite()) continue;
                propRelative = PipeBlock.PROPERTY_BY_DIRECTION.get(relative);
                if(state.getValue(propRelative)) continue;

                posRelative = pos.relative(relative);
                stateRelative = level.getBlockState(posRelative);

                if(cantReplace(stateRelative)){
                    //try cover Direction relative of this
                    if(!canAttachTo(level, posRelative, stateRelative, relative.getOpposite())) continue;

                    level.setBlockAndUpdate(pos, state.setValue(propRelative, true));
                } else {
                    //try to create a new cover block on BlockPos posRelative
                    attachToPos = pos.relative(dir).relative(relative);
                    attachToState = level.getBlockState(attachToPos);

                    if (cantReplace(attachToState)){
                        if(!canAttachTo(level, attachToPos, attachToState, dir.getOpposite())) continue;

                        old = level.getBlockState(posRelative);//make sure that if there is a cover already, it is taken into account
                        toPlace = (old.is(this) ? old : defaultBlockState()).setValue(prop, true);
                        if(old == toPlace) continue;

                        level.setBlockAndUpdate(posRelative, toPlace);
                        return;
                    }

                    attachToPos = pos.relative(dir);
                    if(!canAttachTo(level, attachToPos, level.getBlockState(attachToPos), relative)) continue;

                    posRelative = pos.relative(dir).relative(relative);
                    old = level.getBlockState(posRelative);//make sure that if there is a cover already, it is taken into account
                    toPlace = (old.is(this) ? old : defaultBlockState()).setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(relative.getOpposite()), true);
                    if(old == toPlace) continue;

                    level.setBlockAndUpdate(posRelative, toPlace);
                }

                return;
            }
        }
    }

    protected boolean cantReplace(BlockState state){
        return !state.getMaterial().isReplaceable();
    }
}
