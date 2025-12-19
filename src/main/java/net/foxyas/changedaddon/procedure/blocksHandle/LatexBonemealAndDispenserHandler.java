package net.foxyas.changedaddon.procedure.blocksHandle;

import com.mojang.datafixers.util.Pair;
import net.foxyas.changedaddon.event.LatexTypePlayerEvent;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.latex.SpreadingLatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

public class LatexBonemealAndDispenserHandler {

    /* -------------------------------------------------------------------------
     *  Core – proper latex application on a block FACE
     * ------------------------------------------------------------------------- */
    public static boolean tryApplyLatexAnyFace(
            ServerLevel level,
            BlockPos pos,
            LatexType latexType
    ) {
        // Only spreading latex types are supported
        if (!(latexType instanceof SpreadingLatexType spreading))
            return false;

        // Latex must be applied into air
        if (!level.getBlockState(pos).isAir())
            return false;

        // Try all possible faces
        for (Direction face : Direction.values()) {
            if (tryApplyLatex(level, pos, face, spreading)) {
                return true; // Stop on first valid face
            }
        }

        return false;
    }


    private static boolean tryApplyLatex(
            ServerLevel level,
            BlockPos pos,
            Direction face,
            SpreadingLatexType spreading
    ) {
        BlockState state = level.getBlockState(pos);

        // Latex is applied INTO air
        if (!state.isAir())
            return false;

        // Get the supporting block
        BlockPos supportPos = pos.relative(face.getOpposite());
        BlockState supportState = level.getBlockState(supportPos);

        // Check if latex can exist on that support face
        if (!SpreadingLatexType.canExistOnSurface(
                level,
                supportPos,
                supportState,
                face
        )) {
            return false;
        }

        LatexCoverState originalCover = LatexCoverState.getAt(level, pos);

        var plannedCover = spreading.spreadState(
                level,
                pos,
                spreading.sourceCoverState()
        );

        var event = new SpreadingLatexType.CoveringBlockEvent(
                spreading,
                state,
                state,
                plannedCover,
                pos,
                level
        );

        spreading.defaultCoverBehavior(event);

        if (Changed.postModEvent(event))
            return false;

        if (event.originalState == event.getPlannedState()
                && event.plannedCoverState == originalCover)
            return false;

        level.setBlockAndUpdate(pos, event.getPlannedState());
        LatexCoverState.setAtAndUpdate(level, pos, event.plannedCoverState);

        level.levelEvent(1505, pos, 1);
        return true;
    }


    /* -------------------------------------------------------------------------
     *  Player – Bonemeal interaction
     * ------------------------------------------------------------------------- */
    @Mod.EventBusSubscriber
    public static class PlayerBonemealHandler {

        @SubscribeEvent
        public static void onRightClickBlock(LatexTypePlayerEvent.RightClick event) {
            Level level = event.getLevel();
            Player player = event.getPlayer();
            InteractionHand hand = event.getHand();
            ItemStack stack = player.getItemInHand(hand);

            if (level.isClientSide)
                return;

            if (!stack.is(Items.BONE_MEAL))
                return;

            BlockHitResult hitResult = event.getHitResult();

            BlockPos pos = hitResult.getBlockPos();

            // Determine latex type present on the clicked surface
            LatexCoverState latexState = event.getLatexState();
            boolean spread = trySpread(latexState, event.getRandom(), pos, level);
            if (spread) {
                if (!player.getAbilities().instabuild)
                    stack.shrink(1);

                // Cancel vanilla bonemeal behavior
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
                event.setCanceled(true);
                player.swing(hand, true);
            }
        }
    }

    /* -------------------------------------------------------------------------
     *  Dispenser – Bonemeal behavior
     * ------------------------------------------------------------------------- */
    public static void registerBonemealDispenser() {
        DispenserBlock.registerBehavior(Items.BONE_MEAL, (source, stack) -> {
            ServerLevel level = source.getLevel();
            Direction facing = source.getBlockState().getValue(DispenserBlock.FACING);

            BlockPos supportPos = source.getPos().relative(facing);
            boolean success = spreadFromSource(level, supportPos, 5, level.getRandom(), 0.75f);

            if (success && !stack.isEmpty())
                stack.shrink(1);

            return stack;
        });
    }

    public static boolean trySpread(LatexCoverState state, RandomSource random, BlockPos blockPos, Level level) {
        boolean success = false;
        if (!(state.getType() instanceof SpreadingLatexType latexType)) return false;
        if (!latexType.canSpread(state)) return false;

        for (Direction checkDir : Direction.values()) {
            BlockPos.MutableBlockPos checkPos = blockPos.relative(checkDir).mutable();

            BlockState checkState = level.getBlockState(checkPos);
            LatexCoverState checkCoverState = LatexCoverState.getAt(level, checkPos);

            boolean isAirOrLessThanSpread = checkCoverState.isAir() ||
                    (checkCoverState.is(latexType) && checkCoverState.getValue(SpreadingLatexType.SATURATION) > state.getValue(SpreadingLatexType.SATURATION) + 1);

            if (!checkState.is(ChangedTags.Blocks.DENY_LATEX_COVER) && !checkState.isCollisionShapeFullBlock(level, checkPos) && isAirOrLessThanSpread) {
                if (checkPos.subtract(blockPos).getY() > 0 && random.nextInt(3) > 0)
                    continue;

                if (Arrays.stream(Direction.values()).noneMatch(direction -> SpreadingLatexType.canExistOnSurface(level, checkPos, level.getBlockState(checkPos.relative(direction)), direction.getOpposite())))
                    continue;

                var event = new SpreadingLatexType.CoveringBlockEvent(latexType,
                        checkState, checkState, latexType.spreadState(level, checkPos, state), checkPos, level);
                latexType.defaultCoverBehavior(event);
                if (Changed.postModEvent(event))
                    continue;

                level.setBlockAndUpdate(checkPos, event.getPlannedState());
                LatexCoverState.setAtAndUpdate(level, checkPos, event.plannedCoverState);
                level.levelEvent(1505, checkPos, 1); // particles

                event.getPostProcess().accept(level, checkPos);
                success = true;
            }
        }
        return success;
    }

    public static boolean spreadFromSource(
            ServerLevel level,
            BlockPos source,
            int maxDepth,
            RandomSource random,
            float chance
    ) {
        boolean spread = false;

        Set<BlockPos> visited = new HashSet<>();
        Queue<Pair<BlockPos, Integer>> queue = new ArrayDeque<>();

        queue.add(Pair.of(source, 0));
        visited.add(source);

        while (!queue.isEmpty()) {
            var current = queue.poll();
            BlockPos pos = current.getFirst();
            int depth = current.getSecond();

            if (depth > maxDepth)
                continue;

            LatexCoverState state = LatexCoverState.getAt(level, pos);
            if (state.isAir())
                continue;

            if (!(state.getType() instanceof SpreadingLatexType spreading))
                continue;

            // Chance check
            if (random.nextFloat() > chance)
                continue;

            /* -------------------------------------------------
             * 2. Try spreading to neighbors
             * ------------------------------------------------- */
            spread = trySpread(state, random, pos, level);

            if (spread) level.levelEvent(1505, pos, 1); // particles

            /* -------------------------------------------------
             * BFS expansion
             * ------------------------------------------------- */
            if (depth < maxDepth) {
                for (Direction dir : Direction.values()) {
                    BlockPos next = pos.relative(dir);
                    if (!visited.contains(next)) {
                        visited.add(next);
                        queue.add(Pair.of(next, depth + 1));
                    }
                }
            }
        }

        return spread;
    }


    /* -------------------------------------------------------------------------
     *  Dispenser – Latex goo application
     * ------------------------------------------------------------------------- */
    public static void registerGooDispenser(LatexType latexType, ItemStack gooItem) {
        DispenserBlock.registerBehavior(gooItem.getItem(), (source, stack) -> {
            ServerLevel level = source.getLevel();
            Direction facing = source.getBlockState()
                    .getValue(DispenserBlock.FACING);

            BlockPos targetPos = source.getPos().relative(facing);

            boolean applied = tryApplyLatexAnyFace(
                    level,
                    targetPos,
                    latexType
            );

            if (applied && !stack.isEmpty()) {
                stack.shrink(1);
            }

            return stack;
        });
    }

    /* -------------------------------------------------------------------------
     *  Dispenser – Anti-latex removal
     * ------------------------------------------------------------------------- */
    public static void registerAntiLatexDispenser(ItemStack item) {
        DispenserBlock.registerBehavior(item.getItem(), (source, stack) -> {
            ServerLevel level = source.getLevel();
            Direction facing = source.getBlockState()
                    .getValue(DispenserBlock.FACING);

            BlockPos pos = source.getPos().relative(facing);

            LatexCoverState cover = LatexCoverState.getAt(level, pos);

            // Remove any existing latex cover
            if (!cover.is(ChangedLatexTypes.NONE.get())) {
                LatexCoverState.setAtAndUpdate(
                        level,
                        pos,
                        ChangedLatexTypes.NONE.get().defaultCoverState()
                );

                level.levelEvent(1505, pos, 1);

                if (!stack.isEmpty())
                    stack.shrink(1);
            }

            return stack;
        });
    }
}
