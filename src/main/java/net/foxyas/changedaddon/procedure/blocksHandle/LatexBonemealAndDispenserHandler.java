package net.foxyas.changedaddon.procedure.blocksHandle;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.latex.SpreadingLatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.stream.Stream;

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

    public static boolean trySpreadCascade(
            ServerLevel level,
            BlockPos originPos,
            LatexCoverState originState,
            int maxDepth,
            boolean ignoreRandom
    ) {
        if (!(originState.getType() instanceof SpreadingLatexType))
            return false;

        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(originPos);
        visited.add(originPos);

        boolean spread = false;
        int depth = 0;

        while (!queue.isEmpty() && depth < maxDepth) {
            int layerSize = queue.size();

            for (int i = 0; i < layerSize; i++) {
                BlockPos current = queue.poll();
                LatexCoverState currentState = LatexCoverState.getAt(level, current);

                for (Direction dir : Direction.values()) {
                    BlockPos target = current.relative(dir);

                    if (!visited.add(target))
                        continue;

                    if (trySpread(
                            level,
                            current,
                            currentState,
                            1,
                            ignoreRandom
                    )) {
                        queue.add(target);
                        spread = true;
                    }
                }
            }

            depth++;
        }

        if (spread && ignoreRandom) {
            level.levelEvent(1505, originPos, 1);
        }

        return spread;
    }

    public static boolean trySpreadBurst(
            ServerLevel level,
            BlockPos originPos,
            LatexCoverState originState,
            int attempts
    ) {
        if (!(originState.getType() instanceof SpreadingLatexType))
            return false;

        boolean spread = false;
        BlockPos cursor = originPos;

        for (int i = 0; i < attempts; i++) {
            LatexCoverState state = LatexCoverState.getAt(level, cursor);

            if (!state.isAir()) {
                spread |= trySpread(level, cursor, state, 1, true);
            }

            // Walk randomly from current cursor
            cursor = cursor.relative(Direction.getRandom(level.random));
        }

        if (spread) {
            level.levelEvent(1505, originPos, 1);
        }

        return spread;
    }


    public static boolean trySpread(
            ServerLevel level,
            BlockPos originPos,
            LatexCoverState originState,
            int attempts,
            boolean ignoreRandom
    ) {
        if (!(originState.getType() instanceof SpreadingLatexType spreading))
            return false;

        boolean spread = false;

        for (int i = 0; i < attempts; i++) {
            Direction direction = ignoreRandom
                    ? Direction.getRandom(level.getRandom())
                    : Direction.getRandom(level.random);

            BlockPos targetPos = originPos.relative(direction);
            BlockState targetState = level.getBlockState(targetPos);
            LatexCoverState targetCover = LatexCoverState.getAt(level, targetPos);

            // Target must be air or weaker latex
            boolean validTarget =
                    targetCover.isAir()
                            || (targetCover.is(originState.getType())
                            && targetCover.getValue(SpreadingLatexType.SATURATION)
                            > originState.getValue(SpreadingLatexType.SATURATION) + 1);

            if (!validTarget)
                continue;

            if (targetState.is(ChangedTags.Blocks.DENY_LATEX_COVER))
                continue;

            if (targetState.isCollisionShapeFullBlock(level, targetPos))
                continue;

            // Prevent excessive upward spread unless forced
            if (!ignoreRandom && direction == Direction.UP && level.random.nextInt(3) > 0)
                continue;

            // Check if latex can exist on ANY adjacent face
            boolean hasValidSurface = Arrays.stream(Direction.values())
                    .anyMatch(d ->
                            SpreadingLatexType.canExistOnSurface(
                                    level,
                                    targetPos,
                                    level.getBlockState(targetPos.relative(d)),
                                    d.getOpposite()
                            )
                    );

            if (!hasValidSurface)
                continue;

            // Compute spread result
            LatexCoverState plannedCover =
                    spreading.spreadState(level, targetPos, originState);

            SpreadingLatexType.CoveringBlockEvent event =
                    new SpreadingLatexType.CoveringBlockEvent(
                            spreading,
                            targetState,
                            targetState,
                            plannedCover,
                            targetPos,
                            level
                    );

            spreading.defaultCoverBehavior(event);

            if (Changed.postModEvent(event))
                continue;

            level.setBlockAndUpdate(targetPos, event.getPlannedState());
            LatexCoverState.setAtAndUpdate(level, targetPos, event.plannedCoverState);
            event.getPostProcess().accept(level, targetPos);

            spread = true;
        }

        if (spread && ignoreRandom) {
            // Bonemeal-like particles
            level.levelEvent(1505, originPos, 1);
        }

        return spread;
    }


    /* -------------------------------------------------------------------------
     *  Player – Bonemeal interaction
     * ------------------------------------------------------------------------- */
    @Mod.EventBusSubscriber
    public static class PlayerBonemealHandler {

        @SubscribeEvent
        public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
            Level level = event.getLevel();
            Player player = event.getEntity();
            ItemStack stack = event.getItemStack();

            if (level.isClientSide)
                return;

            if (!stack.is(Items.BONE_MEAL))
                return;

            Direction face = event.getFace();
            if (face == null)
                return;

            BlockPos pos = event.getPos();

            // Determine latex type present on the clicked surface
            LatexType surfaceType =
                    AbstractLatexBlock.getSurfaceType(level, pos, face);

            if (!(surfaceType instanceof SpreadingLatexType latex))
                return;

            // Attempt to spread latex using the new system
            if (tryApplyLatex((ServerLevel) level, pos, face, latex)) {
                if (!player.getAbilities().instabuild)
                    stack.shrink(1);

                // Cancel vanilla bonemeal behavior
                event.setUseBlock(Event.Result.DENY);
                event.setUseItem(Event.Result.DENY);
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
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
            boolean success = spreadFromSourceTick(level, supportPos, 5, level.getRandom(), 0.75f);

            if (success && !stack.isEmpty())
                stack.shrink(1);

            return stack;
        });
    }

    public static LatexCoverState spreadState(LevelReader level, BlockPos blockPos, LatexCoverState state) {
        List<Property.Value<Integer>> list = SpreadingLatexType.SATURATION.getAllValues().toList();
        int pValue = Math.min(state.getValue(SpreadingLatexType.SATURATION) + 1, list.get(list.size() - 1).value());
        state = state.setValue(SpreadingLatexType.SATURATION, pValue);

        for (Direction direction : Direction.values()) {
            BooleanProperty face = SpreadingLatexType.FACES.get(direction);
            BlockPos checkPos = blockPos.relative(direction);
            BlockState checkState = level.getBlockState(checkPos);
            state = state.setValue(face, SpreadingLatexType.canExistOnSurface(level, checkPos, checkState, direction.getOpposite()));
        }

        Stream<BooleanProperty> stream = SpreadingLatexType.FACES.values().stream();
        Objects.requireNonNull(state);
        return stream.noneMatch(state::getValue) && level.getBlockState(blockPos).isAir() ? ChangedLatexTypes.NONE.get().defaultCoverState() : state;
    }

    public static boolean spread(LatexCoverState state, RandomSource random, BlockPos blockPos, Level level) {
        //Direction checkDir = Direction.getRandom(random);
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

                event.getPostProcess().accept(level, checkPos);
                success = true;
            }
        }
        return success;
    }

    public static boolean spreadFromSourceTick(
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

            LatexCoverState originState = LatexCoverState.getAt(level, pos);
            if (originState.isAir())
                continue;

            if (!(originState.getType() instanceof SpreadingLatexType spreading))
                continue;

            // Chance check
            if (random.nextFloat() > chance)
                continue;

            /* -------------------------------------------------
             * 1. Grow current block (saturation)
             * ------------------------------------------------- */
            originState.randomTick(level, pos, random);

            /* -------------------------------------------------
             * 2. Try spreading to neighbors
             * ------------------------------------------------- */
            for (Direction dir : Direction.values()) {
                BlockPos target = pos.relative(dir);
                BlockState targetBlock = level.getBlockState(target);
                LatexCoverState targetCover = LatexCoverState.getAt(level, target);

                boolean validTarget =
                        targetCover.isAir() || targetCover.is(originState.getType());

                if (!validTarget)
                    continue;

                if (!targetBlock.isAir() && targetBlock.isCollisionShapeFullBlock(level, target))
                    continue;


                if (targetCover.isAir()) {
                    LatexCoverState.setAtAndUpdate(level, target, originState);
                    LatexCoverState.getAt(level, target).randomTick(level, target, random);
                    spread = true;
                } else if (targetCover.is(originState.getType())) {
                    targetCover.randomTick(level, target, random);
                    spread = true;
                }
            }

            level.levelEvent(1505, pos, 1); // particles

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

            LatexCoverState originState = LatexCoverState.getAt(level, pos);
            if (originState.isAir())
                continue;

            if (!(originState.getType() instanceof SpreadingLatexType spreading))
                continue;

            // Chance check
            if (random.nextFloat() > chance)
                continue;

            /* -------------------------------------------------
             * 1. Grow current block (saturation)
             * ------------------------------------------------- */
            //LatexCoverState.setAtAndUpdate(level, pos, grown);

            /* -------------------------------------------------
             * 2. Try spreading to neighbors
             * ------------------------------------------------- */
            for (Direction dir : Direction.values()) {
                BlockPos target = pos.relative(dir);
                BlockState targetBlock = level.getBlockState(target);
                LatexCoverState targetCover = LatexCoverState.getAt(level, target);

                boolean validTarget =
                        targetCover.isAir()
                                || (targetCover.is(originState.getType())
                                && targetCover.getValue(SpreadingLatexType.SATURATION)
                                < originState.getValue(SpreadingLatexType.SATURATION));

                if (!validTarget)
                    continue;

                if (!targetBlock.isAir() && targetBlock.isCollisionShapeFullBlock(level, target))
                    continue;

                LatexCoverState grown = spreadState(level, target, originState);
                if (grown.isAir() || !grown.hasProperty(SpreadingLatexType.SATURATION)) continue;
                //LatexCoverState grownUpdated = grown.setValue(SpreadingLatexType.SATURATION, Mth.clamp(target.distManhattan(source), 0, 15));
                LatexCoverState.setAtAndUpdate(level, target, grown);
                spread = true;
            }

            level.levelEvent(1505, pos, 1); // particles

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
