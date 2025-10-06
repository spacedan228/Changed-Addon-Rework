package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.block.entity.DarkLatexWolfPlushyBlockEntity;
import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonCriteriaTriggers;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.util.DynamicClipContext;
import net.foxyas.changedaddon.util.FoxyasUtils;
import net.foxyas.changedaddon.variants.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.Util;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class DarkLatexWolfPlushyBlock extends AbstractPlushyBlock {
    public DarkLatexWolfPlushyBlock() {
        super(BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL)
                .strength(0.5f, 5f)
                .noOcclusion()
                .isRedstoneConductor((bs, br, bp) -> false));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Mod.EventBusSubscriber
    public static class EventHandle {

        public static TransfurVariant<?> getTransfurVariant(Level world) {
            List<TransfurVariant<?>> list = new ArrayList<>();
            list.add(ChangedTransfurVariants.DARK_LATEX_WOLF_MALE.get());
            list.add(ChangedTransfurVariants.DARK_LATEX_WOLF_FEMALE.get());
            list.add(ChangedAddonTransfurVariants.PURO_KIND_MALE.get());
            list.add(ChangedAddonTransfurVariants.PURO_KIND_FEMALE.get());

            return Util.getRandom(list, world.getRandom());
        }

        @SubscribeEvent
        public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
            if (event.getPlayer().getLevel().isClientSide) return;
            Player player = event.getPlayer();
            Level world = player.getLevel();
            Random random = player.getRandom();
            BlockPos playerBlockPos = player.blockPosition();
            Stream<BlockPos> posStream = FoxyasUtils.betweenClosedStreamSphere(playerBlockPos, 3, 2);
            float intensity = 1 + (player.getInventory().items.stream().filter((itemStack -> itemStack.is(ChangedAddonItems.DARK_LATEX_WOLF_PLUSH.get()))).count() / 100f);
            posStream.forEach((pos) -> {
                if ((world.getBlockState(pos).is(ChangedAddonBlocks.DARK_LATEX_WOLF_PLUSH.get())
                        && canSeePlayer(pos, world, playerBlockPos, player))) {
                    if (!event.updateWorld()) {
                        if (!ProcessTransfur.isPlayerTransfurred(player)) {
                            if (random.nextFloat() <= (0.25f + (player.getLuck() / 100))
                                    / ((player.position().distanceTo(Vec3.atCenterOf(pos))) * intensity)) {
                                ProcessTransfur.transfur(player, world, getTransfurVariant(world), false, TransfurContext.hazard(TransfurCause.FACE_HAZARD));
                                if (player instanceof ServerPlayer serverPlayer) {
                                    ChangedAddonCriteriaTriggers.SLEEP_NEXT_A_PLUSHY_TRIGGER.trigger(serverPlayer, ProcessTransfur.getPlayerTransfurVariant(serverPlayer), "dark_latex_plushy", true);
                                }
                            }
                        }
                    }
                }
            });
        }

        private static boolean canSeePlayer(BlockPos pos, Level world, BlockPos playerBlockPos, Player player) {
            return world.clip(getClipContext(pos, playerBlockPos, player)).getType() == HitResult.Type.MISS;
        }

        private static @NotNull DynamicClipContext getClipContext(BlockPos pos, BlockPos playerBlockPos, Player player) {
            return new DynamicClipContext(Vec3.atCenterOf(pos), Vec3.atCenterOf(playerBlockPos), ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE::canPick, CollisionContext.of(player));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING, WATERLOGGED);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderLayer() {
        ItemBlockRenderTypes.setRenderLayer(ChangedAddonBlocks.DARK_LATEX_WOLF_PLUSH.get(), renderType -> renderType == RenderType.cutoutMipped());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new DarkLatexWolfPlushyBlockEntity(pPos, pState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return super.getTicker(pLevel, pState, pBlockEntityType);
    }
}
