package net.foxyas.changedaddon.block.advanced;

import net.foxyas.changedaddon.init.ChangedAddonBlockEntities;
import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.ltxprogrammer.changed.block.KeypadBlock;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class TimedKeypadBlock extends KeypadBlock {

    public static final VoxelShape BUTTON_LEFT = Block.box(
            12, 3, 15,
            13, 4, 15.2
    );

    public static final VoxelShape BUTTON_RIGHT = Block.box(
            14, 3, 15,
            15, 4, 15.2
    );

    public static final VoxelShape BUTTON_CENTER = Block.box(
            13, 3, 15,
            14, 4, 15.2
    );

    public static final VoxelShape EXTRA_BUTTONS = Shapes.or(
            BUTTON_LEFT,
            BUTTON_CENTER,
            BUTTON_RIGHT
    );

    public TimedKeypadBlock() {
        super();
        drops = BuiltInLootTables.EMPTY;
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderLayer() {
        ItemBlockRenderTypes.setRenderLayer(ChangedAddonBlocks.TIMED_KEYPAD.get(), renderType -> renderType == RenderType.cutout());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TimedKeypadBlockEntity(blockPos, blockState);
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        VoxelShape main = calculateShapes(blockState.getValue(FACING), SHAPE_WHOLE);
        VoxelShape buttons = calculateShapes(blockState.getValue(FACING).getClockWise(), Shapes.or(BUTTON_CENTER, BUTTON_LEFT, BUTTON_RIGHT));
        return Shapes.or(main, buttons);
    }

    public @NotNull VoxelShape getOcclusionShape(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        return this.getInteractionShape(blockState, level, blockPos);
    }

    public @NotNull VoxelShape getCollisionShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return this.getInteractionShape(blockState, level, blockPos);
    }

    public @NotNull VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return this.getInteractionShape(blockState, level, blockPos);
    }

    public enum KeypadButton {
        LEFT,
        CENTER,
        RIGHT;
    }

    public VoxelShape getButtonsInteractionShape(KeypadButton button, BlockState blockState) {
        return calculateShapes(
                blockState.getValue(FACING).getClockWise(),
                switch (button) {
                    case LEFT -> BUTTON_LEFT;
                    case CENTER -> BUTTON_CENTER;
                    case RIGHT -> BUTTON_RIGHT;
                }
        );
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (player.isShiftKeyDown() && !state.getValue(KeypadBlock.POWERED)) {
            /*player.displayClientMessage(new TextComponent("Pos:" + (hitResult.getLocation().subtract(hitResult.getBlockPos().getX(),
                            hitResult.getBlockPos().getY(),
                            hitResult.getBlockPos().getZ()))),
                    true);*/
            Vec3 relative = (hitResult.getLocation().subtract(hitResult.getBlockPos().getX(),
                    hitResult.getBlockPos().getY(),
                    hitResult.getBlockPos().getZ()));
            Vec3 location = hitResult.getLocation();
            Vec3 localLocation = location.subtract(pos.getX(), pos.getY(), pos.getZ());

            for (KeypadButton keypadButton : KeypadButton.values()) {
                VoxelShape interactionShape = getButtonsInteractionShape(keypadButton, state);
                player.displayClientMessage(new TextComponent("HEY -> " + interactionShape.bounds()), false);
                player.displayClientMessage(new TextComponent("HEY2 -> " + localLocation), false);


                if (interactionShape.bounds().contains(localLocation)) {
                    player.displayClientMessage(new TextComponent("HEY IT IS WORKING -> " + keypadButton.name()), false);
                    switch (keypadButton) {
                        case LEFT -> {
                            BlockEntity blockEntity = level.getBlockEntity(pos);
                            if (blockEntity instanceof TimedKeypadBlockEntity keypad) {
                                keypad.addTimer(1);
                                keypad.playTimerAdjust(true);
                            }
                            return InteractionResult.SUCCESS;
                        }
                        case RIGHT -> {
                            BlockEntity blockEntity = level.getBlockEntity(pos);
                            if (blockEntity instanceof TimedKeypadBlockEntity keypad) {
                                keypad.addTimer(-1);
                                keypad.playTimerAdjust(false);
                            }
                            return InteractionResult.SUCCESS;
                        }
                        case CENTER -> {
                            BlockEntity blockEntity = level.getBlockEntity(pos);
                            if (blockEntity instanceof TimedKeypadBlockEntity keypad) {
                                if (keypad.getTimer() > 0) {
                                    keypad.setTimer(0);
                                    keypad.playTimerAdjust(true);
                                }
                            }
                            return InteractionResult.SUCCESS;
                        }
                    }
                }

            }
            /*
            if (direction == Direction.NORTH) {
                if (isInside(relative, 0.0624f, 0.0626f, 0.185f, 0.25f, 0.75f, 0.814f)) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof TimedKeypadBlockEntity keypad) {
                        keypad.addTimer(1);
                        keypad.playTimerAdjust(true);
                    }
                    return InteractionResult.SUCCESS;
                }
                if (isInside(relative, 0.0624f, 0.0626f, 0.185f, 0.25f, 0.814f, 0.8772f)) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof TimedKeypadBlockEntity keypad) {
                        if (keypad.getTimer() > 0) {
                            keypad.setTimer(0);
                            keypad.playTimerAdjust(true);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
                if (isInside(relative, 0.0624f, 0.0626f, 0.185f, 0.25f, 0.8772f, 0.9404f)) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof TimedKeypadBlockEntity keypad) {
                        keypad.addTimer(-1);
                        keypad.playTimerAdjust(false);
                    }
                    return InteractionResult.SUCCESS;
                }
            } else if (direction == Direction.SOUTH) {
                if (isInside(relative, 0.9374f, 0.9376f, 0.185f, 0.25f, 0.186f, 0.25f)) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof TimedKeypadBlockEntity keypad) {
                        keypad.addTimer(1);
                        keypad.playTimerAdjust(true);
                    }
                    return InteractionResult.SUCCESS;
                }
                if (isInside(relative, 0.9374f, 0.9376f, 0.185f, 0.25f, 0.1228f, 0.186f)) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof TimedKeypadBlockEntity keypad) {
                        if (keypad.getTimer() > 0) {
                            keypad.setTimer(0);
                            keypad.playTimerAdjust(true);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
                if (isInside(relative, 0.9374f, 0.9376f, 0.185f, 0.25f, 0.0596f, 0.1228f)) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof TimedKeypadBlockEntity keypad) {
                        keypad.addTimer(-1);
                        keypad.playTimerAdjust(false);
                    }
                    return InteractionResult.SUCCESS;
                }
            } else if (direction == Direction.WEST) {
                if (isInsideWithDirection(relative, direction, 0.9374f, 0.9376f, 0.185f, 0.25f, 0.75f, 0.814f)) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof TimedKeypadBlockEntity keypad) {
                        keypad.addTimer(1);
                        keypad.playTimerAdjust(true);
                    }
                    return InteractionResult.SUCCESS;
                }
                if (isInsideWithDirection(relative, direction, 0.9374f, 0.9376f, 0.185f, 0.25f, 0.814f, 0.880f)) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof TimedKeypadBlockEntity keypad) {
                        if (keypad.getTimer() > 0) {
                            keypad.setTimer(0);
                            keypad.playTimerAdjust(true);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
                if (isInsideWithDirection(relative, direction, 0.9374f, 0.9376f, 0.185f, 0.25f, 0.880f, 0.9432f)) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof TimedKeypadBlockEntity keypad) {
                        keypad.addTimer(-1);
                        keypad.playTimerAdjust(false);
                    }
                    return InteractionResult.SUCCESS;
                }
            } else if (direction == Direction.EAST) {
                if (isInsideWithDirection(relative, direction, 0.0624f, 0.0626f, 0.185f, 0.25f, 0.186f, 0.25f)) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof TimedKeypadBlockEntity keypad) {
                        keypad.addTimer(1);
                        keypad.playTimerAdjust(true);
                    }
                    return InteractionResult.SUCCESS;
                }
                if (isInsideWithDirection(relative, direction, 0.0624f, 0.0626f, 0.185f, 0.25f, 0.1228f, 0.186f)) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof TimedKeypadBlockEntity keypad) {
                        if (keypad.getTimer() > 0) {
                            keypad.setTimer(0);
                            keypad.playTimerAdjust(true);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
                if (isInsideWithDirection(relative, direction, 0.0624f, 0.0626f, 0.185f, 0.25f, 0.0596f, 0.1228f)) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof TimedKeypadBlockEntity keypad) {
                        keypad.addTimer(-1);
                        keypad.playTimerAdjust(false);
                    }
                    return InteractionResult.SUCCESS;
                }
            }*/

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TimedKeypadBlockEntity keypad) {
                if (player instanceof ServerPlayer serverPlayer) {
                    NetworkHooks.openGui(serverPlayer, keypad, keypad.getBlockPos());
                }
                return InteractionResult.SUCCESS;
            }

            return super.use(state, level, pos, player, hand, hitResult);
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public void tick(BlockState blockState, ServerLevel level, BlockPos blockPos, Random random) {
        super.tick(blockState, level, blockPos, random);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        CompoundTag tag = stack.getOrCreateTag();
        if (!level.isClientSide && stack.hasTag() && tag.contains("TimerValue")) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TimedKeypadBlockEntity keypad) {
                keypad.addTimer(tag.getInt("TimerValue"));
                keypad.setCanTick(true);
                keypad.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(
                type,
                ChangedAddonBlockEntities.TIMED_KEYPAD_BLOCK_ENTITY.get(), // Certifique-se que esse seja o registry correto
                (lvl, pos, blockState, be) -> {
                    be.tick(lvl, pos);
                    lvl.sendBlockUpdated(pos, blockState, blockState, 3);
                }
        );
    }

    // pixelX, pixelY, pixelZ vão de 0 a 15 (inclusive)
    private boolean isInsidePixel(Vec3 relative, int px, int py, int pz) {
        final float pixelSize = 1.0f / 16.0f;
        float minX = px * pixelSize;
        float maxX = minX + pixelSize;

        float minY = py * pixelSize;
        float maxY = minY + pixelSize;

        float minZ = pz * pixelSize;
        float maxZ = minZ + pixelSize;

        return relative.x >= minX && relative.x < maxX &&
                relative.y >= minY && relative.y < maxY &&
                relative.z >= minZ && relative.z < maxZ;
    }


    private boolean isInside(Vec3 rel, double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
        return rel.x >= minX && rel.x < maxX &&
                rel.y >= minY && rel.y < maxY &&
                rel.z >= minZ && rel.z < maxZ;
    }

    private boolean isInsideWithDirection(Vec3 rel, Direction direction, double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            return isInside(rel, minX, maxX, minY, maxY, minZ, maxZ);
        } else if (direction == Direction.WEST || direction == Direction.EAST) {
            return isInside(rel, minZ, maxZ, minY, maxY, minX, maxX);
        }

        return isInside(rel, minX, maxX, minY, maxY, minZ, maxZ);
    }
}
