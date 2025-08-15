package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonMobEffects;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LuminaraBloomBlock extends FlowerBlock implements BonemealableBlock {
    public LuminaraBloomBlock() {
        super(ChangedAddonMobEffects.UNTRANSFUR, 60,
                BlockBehaviour.Properties.of(Material.PLANT)
                        .emissiveRendering((state, blockGetter, blockPos) -> true)
                        .hasPostProcess((state, blockGetter, blockPos) -> true)
                        .noCollission().dynamicShape().instabreak().sound(SoundType.GRASS));
    }

    public static void registerRenderLayer() {
        ItemBlockRenderTypes.setRenderLayer(ChangedAddonBlocks.LUMINARA_BLOOM.get(), renderType -> renderType == RenderType.cutout());
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightBlock(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos) {
        return 3;
    }

    @Override
    public boolean canSurvive(@NotNull BlockState pState, @NotNull LevelReader pLevel, @NotNull BlockPos pPos) {
        BlockState below = pLevel.getBlockState(pPos.below());
        if (below.getBlock() instanceof AbstractLatexBlock) {
            return true;
        }
        return super.canSurvive(pState, pLevel, pPos);
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, BlockPos pos, Random random) {
        AABB aabb = this.getShape(state, level, pos, CollisionContext.empty()).bounds();
        Vec3 center = aabb.getCenter().add(0.0625F, 0.0625F, 0.0625F);
        double x = center.x + (random.nextDouble(-0.5, 0.5));
        double y = center.y + 0.3D;
        double z = center.z + (random.nextDouble(-0.5, 0.5));

        if (random.nextFloat() < 0.05F) {
            level.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, x, y, z, 0, 0.01D, 0);
        }
    }


    @Override
    protected boolean mayPlaceOn(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos) {
        if (pState.getBlock() instanceof AbstractLatexBlock) {
            return true;
        }
        return super.mayPlaceOn(pState, pLevel, pPos);
    }

//    @Override
//    public boolean isValidBonemealTarget(@NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull BlockState blockState, boolean pIsClient) {
//        BlockState below = blockGetter.getBlockState(blockPos.below());
//        return below.getBlock() instanceof AbstractLatexBlock || below.is(BlockTags.DIRT) || below.is(Blocks.FARMLAND);
//    }

    @Override
    public boolean isValidBonemealTarget(@NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull BlockState blockState, boolean pIsClient) {
        return BlockPos.betweenClosedStream(new AABB(blockPos, blockPos).inflate(3, 1, 3))
                .anyMatch(pos -> {
                    BlockState normal = blockGetter.getBlockState(pos);
                    BlockState below = blockGetter.getBlockState(pos.below());
                    return normal.isAir() && (below.getBlock() instanceof AbstractLatexBlock || below.is(BlockTags.DIRT) || below.is(Blocks.FARMLAND));
                });
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull Random random, @NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return true;
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel serverLevel, @NotNull Random random, @NotNull BlockPos pos, @NotNull BlockState state) {

        //Vanilla like behavior
        int tries = 16;
        for (int i = 0; i < tries; i++) {
            BlockPos targetPos = pos.offset(
                    random.nextInt(7) - 3, // X: -2 to +2
                    random.nextInt(3) - 1, // Y: -1 to +1
                    random.nextInt(7) - 3  // Z: -2 to +2
            );
            if (serverLevel.isEmptyBlock(targetPos)) {
                BlockState below = serverLevel.getBlockState(targetPos.below());
                if (below.getBlock() instanceof AbstractLatexBlock || below.is(BlockTags.DIRT) || below.is(Blocks.FARMLAND)) {
                    serverLevel.setBlock(targetPos, state, 3);
                }
            }
        }

        /*
        * less vanilla like behavior
        List<BlockPos> list = FoxyasUtils.betweenClosedStreamSphere(pos, 3,1, 1f).toList();
        for (BlockPos blockPos : list) {
            if (serverLevel.getRandom().nextFloat() >= 0.5f && serverLevel.isEmptyBlock(blockPos)) {
                BlockState below = serverLevel.getBlockState(blockPos.below());
                if (below.getBlock() instanceof AbstractLatexBlock || below.is(BlockTags.DIRT) || below.is(Blocks.FARMLAND)) {
                    serverLevel.setBlock(blockPos, state, 3);
                }
            }
        }*/
    }

}
