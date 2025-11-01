package net.foxyas.changedaddon.procedures;

//@Mod.EventBusSubscriber
public class SodaCanBlockAddProcedure {

    /*
    @SubscribeEvent
    public static void UseItem(LivingEntityUseItemEvent.Start event) {
        if(!event.getEntity().isCrouching()) return;

        if (event.getItem().is(ChangedAddonItems.SNEPSI.get()) || event.getItem().is(ChangedAddonItems.FOXTA.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getPlayer().isCrouching()) {
            return;
        }

        Direction direction = event.getFace();
        if(direction == null) return;

        DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
        BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

        Block block = event.getItemStack().is(ChangedAddonItems.SNEPSI.get()) ? ChangedAddonBlocks.SNEPSI_CAN.get() :
                event.getItemStack().is(ChangedAddonItems.FOXTA.get()) ? ChangedAddonBlocks.FOXTA_CAN.get() : Blocks.AIR;

        if (block == Blocks.AIR) {
            return;
        }

        BlockState blockState = block.defaultBlockState().setValue(FACING, event.getPlayer().getDirection().getOpposite());

        Level level = event.getWorld();
        BlockPos targetPos = event.getPos().relative(direction);

        if (!level.getBlockState(targetPos).isAir() && !level.getBlockState(targetPos).is(Blocks.WATER)) {
            return;
        }

        if (!level.getBlockState(targetPos.below()).isFaceSturdy(level, targetPos.below(), Direction.UP)) {
            return;
        }

        boolean isWater = level.getBlockState(targetPos).getFluidState().is(FluidTags.WATER) && level.getBlockState(targetPos).getFluidState().isSource();
        blockState = blockState.setValue(WATERLOGGED, isWater);

        if (level.setBlock(targetPos, blockState, 3)) {
            level.playSound(event.getPlayer(), targetPos, blockState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            event.getPlayer().swing(event.getHand());
            if (event.getPlayer() instanceof ServerPlayer serverPlayer && serverPlayer.gameMode.getGameModeForPlayer().isSurvival()) {
                event.getItemStack().shrink(1);
            }
        }
    }*/
}
