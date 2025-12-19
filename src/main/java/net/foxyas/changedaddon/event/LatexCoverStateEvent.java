package net.foxyas.changedaddon.event;

import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

/**
 * Events related to LatexCoverState lifecycle and world logic.
 */
public abstract class LatexCoverStateEvent extends Event {

    protected final LatexCoverState latexState;
    protected final LatexType latexType;
    protected final Level level;
    protected final BlockPos pos;
    protected final RandomSource random;

    protected LatexCoverStateEvent(
            LatexCoverState latexState,
            LatexType latexType,
            Level level,
            BlockPos pos,
            RandomSource random
    ) {
        this.latexState = latexState;
        this.latexType = latexType;
        this.level = level;
        this.pos = pos;
        this.random = random;
    }

    public LatexCoverState getLatexState() {
        return latexState;
    }

    public LatexType getLatexType() {
        return latexType;
    }

    public Level getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    public RandomSource getRandom() {
        return random;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    /* ------------------------------------------------------------ */
    /* Random tick                                                   */
    /* ------------------------------------------------------------ */

    public static class RandomTick extends LatexCoverStateEvent {

        public RandomTick(
                LatexCoverState latexState,
                LatexType latexType,
                Level level,
                BlockPos pos,
                RandomSource random
        ) {
            super(latexState, latexType, level, pos, random);
        }
    }

    /* ------------------------------------------------------------ */
    /* On place                                                      */
    /* ------------------------------------------------------------ */

    public static class OnPlace extends LatexCoverStateEvent {

        public final boolean flag;
        public final LatexCoverState oldState;

        public OnPlace(
                LatexCoverState latexState,
                LatexType latexType,
                Level level,
                BlockPos pos,
                LatexCoverState oldState,
                boolean flag
        ) {
            super(latexState, latexType, level, pos, level.getRandom());
            this.oldState = oldState;
            this.flag = flag;
        }
    }

    /* ------------------------------------------------------------ */
    /* On remove                                                     */
    /* ------------------------------------------------------------ */

    public static class OnRemove extends LatexCoverStateEvent {

        public final boolean flag;
        public final LatexCoverState oldState;

        @Nullable
        private final Player breaker;

        public OnRemove(
                LatexCoverState latexState,
                LatexType latexType,
                Level level,
                BlockPos pos,
                LatexCoverState oldState,
                boolean flag,
                @Nullable Player breaker
        ) {
            super(latexState, latexType, level, pos, level.getRandom());
            this.breaker = breaker;
            this.oldState = oldState;
            this.flag = flag;
        }

        @Nullable
        public Player getBreaker() {
            return breaker;
        }
    }


    public static class UpdateInPlace extends Event {

        private final LatexCoverState state;
        private final LatexType type;
        private final BlockState oldState;
        private final BlockState newState;
        private final LevelAccessor level;
        private final BlockPos pos;
        private final LatexCoverState defaultValue;
        public LatexCoverState modReturnValue = ChangedLatexTypes.NONE.get().defaultCoverState();

        public UpdateInPlace(LatexCoverState state, LatexType type, BlockState oldState, BlockState newState, LevelAccessor level, BlockPos pos, LatexCoverState defaultValue) {
            this.state = state;
            this.type = type;
            this.oldState = oldState;
            this.newState = newState;
            this.level = level;
            this.pos = pos;
            this.defaultValue = defaultValue;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }

        public LatexCoverState getDefaultValue() {
            return defaultValue;
        }

        public BlockPos getPos() {
            return pos;
        }

        public LevelAccessor getLevel() {
            return level;
        }

        public BlockState getNewState() {
            return newState;
        }

        public BlockState getOldState() {
            return oldState;
        }

        public LatexType getType() {
            return type;
        }

        public LatexCoverState getState() {
            return state;
        }
    }
}
