package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.entity.advanced.LuminaraFlowerBeastEntity;
import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonMobEffects;
import net.foxyas.changedaddon.util.FoxyasUtils;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class PottedLuminaraBloomFlowerBlock extends FlowerPotBlock {
    public PottedLuminaraBloomFlowerBlock() {
        super(() -> (FlowerPotBlock) Blocks.FLOWER_POT, ChangedAddonBlocks.LUMINARA_BLOOM,
                Properties.copy(Blocks.POTTED_DANDELION).noOcclusion()
                        .emissiveRendering((state, blockGetter, blockPos) -> true)
                        .hasPostProcess((state, blockGetter, blockPos) -> true)
        );
    }

    public static void registerRenderLayer() {
        ItemBlockRenderTypes.setRenderLayer(ChangedAddonBlocks.POTTED_LUMINARA_BLOOM.get(), renderType -> renderType == RenderType.cutout());
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightBlock(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos) {
        return 3;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pOldState, boolean pIsMoving) {
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
        pLevel.scheduleTick(pPos, this, 10);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(@NotNull BlockState pState, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull Random pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);
        tryToPacifyNearbyEntities(pLevel, pPos, 64);
        pLevel.scheduleTick(pPos, this, 10);
    }

    public void tryToPacifyNearbyEntities(@NotNull ServerLevel pLevel, BlockPos pPos, double range) {
        List<LivingEntity> nearChangedBeasts = pLevel.getEntitiesOfClass(LivingEntity.class,
                new AABB(pPos, pPos).inflate(range),
                (entity) -> FoxyasUtils.canEntitySeePosIgnoreGlass(entity, Vec3.atCenterOf(pPos), 90));
        for (LivingEntity livingEntity : nearChangedBeasts) {
            if (livingEntity instanceof ChangedEntity changedEntity) {
                if (changedEntity instanceof LuminaraFlowerBeastEntity) {
                    continue;
                }

                if (changedEntity.getType().is(ChangedTags.EntityTypes.LATEX)) {
                    if (!changedEntity.hasEffect(ChangedAddonMobEffects.PACIFIED.get())) {
                        changedEntity.addEffect(new MobEffectInstance(ChangedAddonMobEffects.PACIFIED.get(), 60 * 20, 0, true, false, true));
                    }
                }
            } else if (livingEntity instanceof Player player) {
                TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
                if (instance != null) {
                    if ((instance.getChangedEntity() instanceof LuminaraFlowerBeastEntity)) {
                        continue;
                    }

                    if (instance.getParent().getEntityType().is(ChangedTags.EntityTypes.LATEX)) {
                        if (!player.hasEffect(ChangedAddonMobEffects.PACIFIED.get())) {
                            player.addEffect(new MobEffectInstance(ChangedAddonMobEffects.PACIFIED.get(), 60 * 20, 0, true, false, true));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Random random) {
        if (random.nextFloat() >= 0.25) return;

        Vec3 offset = state.getOffset(level, pos);
        float x = (float) offset.x + pos.getX() + 0.5f + random.nextFloat(-0.3f, 0.3f);
        float y = (float) offset.y + pos.getY() + 0.5625f;
        float z = (float) offset.z + pos.getZ() + 0.5f + random.nextFloat(-0.3f, 0.3f);

        level.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, x, y, z, 0, 0.01D, 0);
        if (level instanceof ClientLevel clientLevel) {
            if (random.nextFloat() >= 0.5f) return;
            clientLevel.playLocalSound(pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1, random.nextFloat(), true);
        }
    }

}
