package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonMobEffects;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import static net.foxyas.changedaddon.block.interfaces.ConditionalLatexCoverableBlock.*;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class LatexInsulatorBlock extends Block implements NonLatexCoverableBlock {

    public LatexInsulatorBlock() {
        super(BlockBehaviour.Properties.of(Material.CLAY).sound(SoundType.SLIME_BLOCK).strength(0.05f, 10f).speedFactor(0.5f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderLayer() {
        ItemBlockRenderTypes.setRenderLayer(ChangedAddonBlocks.LATEX_INSULATOR.get(), renderType -> renderType == RenderType.translucent());
    }

    @Override
    public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
        return new float[]{0.6f, 0.6f, 0.6f};
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.getBlock() == this || super.skipRendering(state, adjacentBlockState, side);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public @NotNull VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    public @NotNull VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return Shapes.block();
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState p_60572_, BlockGetter p_60573_, BlockPos p_60574_, CollisionContext p_60575_) {
        return Shapes.or(box(1, 1, 1, 15, 15, 15), box(4, 4, 4, 12, 12, 12));
    }

    @Override
    public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
        world.scheduleTick(pos, this, 10);
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
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
    public void attack(BlockState blockstate, Level world, BlockPos pos, Player entity) {
        execute(entity);
    }

    @Override
    public void entityInside(BlockState blockstate, Level world, BlockPos pos, Entity entity) {
        execute(entity);
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState blockstate, Entity entity) {
        super.stepOn(world, pos, blockstate, entity);
        execute(entity);
    }

    private static void execute(Entity entity) {
        entity.fallDistance = 0;
        if (entity.getType().is(ChangedTags.EntityTypes.LATEX)) {
            if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) > 1) {
                if (entity instanceof LivingEntity _entity && !_entity.level.isClientSide())
                    _entity.addEffect(new MobEffectInstance(ChangedAddonMobEffects.LATEX_SOLVENT.get(), 300, 0, false, false));
                if (entity instanceof LivingEntity _entity)
                    _entity.hurt(new DamageSource("latex_solvent").bypassArmor(), 1);
            }
        }
        if (entity instanceof Player player) {
            TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
            if (instance != null) {
                if (ProcessTransfur.isPlayerLatex(player)) {
                    if (!player.level.isClientSide())
                        player.addEffect(new MobEffectInstance(ChangedAddonMobEffects.LATEX_SOLVENT.get(), 120, 0, false, false));
                    if (player.getHealth() > 1) {
                        player.hurt(new DamageSource("latex_solvent").bypassArmor(), 1);
                    }
                } else {
                    if ((entity.getCapability(ChangedAddonVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new ChangedAddonVariables.PlayerVariables())).showWarns) {
                        if (entity instanceof Player _player && !_player.level.isClientSide())
                            _player.displayClientMessage(Component.literal((Component.translatable("changedaddon.untransfur.Immune").getString())), true);
                    }
                }
            }
        }
    }
}
