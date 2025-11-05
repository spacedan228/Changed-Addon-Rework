package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.init.ChangedAddonFluids;
import net.foxyas.changedaddon.init.ChangedAddonMobEffects;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class LitixCamoniaFluidBlock extends LiquidBlock {

    public LitixCamoniaFluidBlock() {
        super(() -> (FlowingFluid) ChangedAddonFluids.LITIX_CAMONIA_FLUID.get(),
                BlockBehaviour.Properties.of(Material.WATER, MaterialColor.SNOW).strength(100f)
        );
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(blockstate, world, pos, oldState, moving);
        world.scheduleTick(pos, this, 10);
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
        tick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public void tick(BlockState blockstate, ServerLevel level, BlockPos origin, Random random) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        pos.set(origin);

        BlockState bs;
        for(Direction dir : Direction.values()){
            pos.set(origin).move(dir);
            bs = level.getBlockState(pos);
            if (bs.hasProperty(AbstractLatexBlock.COVERED) && bs.getValue(AbstractLatexBlock.COVERED) != LatexType.NEUTRAL)
                level.setBlockAndUpdate(pos, bs.setValue(AbstractLatexBlock.COVERED, LatexType.NEUTRAL));
        }

        level.scheduleTick(pos, this, 10);
    }

    @Override
    public void entityInside(BlockState blockstate, Level world, BlockPos pos, Entity entity) {
        if (entity instanceof Player player && ProcessTransfur.isPlayerTransfurred(player)) {
            if (!ProcessTransfur.isPlayerNotLatex(player)) {
                if (!player.level.isClientSide())
                    player.addEffect(new MobEffectInstance(ChangedAddonMobEffects.LATEX_SOLVENT.get(), 120, 0, false, false));
            } else {
                if ((entity.getCapability(ChangedAddonVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new ChangedAddonVariables.PlayerVariables())).showWarns) {
                    if (entity instanceof Player _player && !_player.level.isClientSide())
                        _player.displayClientMessage(new TextComponent((new TranslatableComponent("changedaddon.untransfur.Immune.fluid").getString())), true);
                }
            }
        } else {
            if (entity instanceof LivingEntity _entity && !_entity.level.isClientSide())
                _entity.addEffect(new MobEffectInstance(ChangedAddonMobEffects.LATEX_SOLVENT.get(), 120, 0, false, false));
        }
        if (entity.getType().is(ChangedTags.EntityTypes.LATEX)) {
            if (entity instanceof LivingEntity _entity && !_entity.level.isClientSide())
                _entity.addEffect(new MobEffectInstance(ChangedAddonMobEffects.LATEX_SOLVENT.get(), 200, 0, false, false));
        }
    }
}
