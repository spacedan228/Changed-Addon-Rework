package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.block.interfaces.ConditionalLatexCoverableBlock;
import net.foxyas.changedaddon.fluid.LitixCamoniaFluid;
import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.function.Supplier;

import static net.foxyas.changedaddon.block.interfaces.ConditionalLatexCoverableBlock.NonLatexCoverableBlock;

public class WhiteLatexCoverBlock extends LatexCoverBlock implements WhiteLatexTransportInterface {

    public WhiteLatexCoverBlock(Properties pProperties) {
        super(pProperties, ChangedLatexTypes.WHITE_LATEX::get);
    }
}
