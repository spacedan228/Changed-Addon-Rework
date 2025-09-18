package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.block.entity.DarkLatexPuddleBlockEntity;
import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonSounds;
import net.foxyas.changedaddon.network.PacketUtil;
import net.ltxprogrammer.changed.block.NonLatexCoverableBlock;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class DarkLatexPuddleBlock extends HorizontalDirectionalBlock implements EntityBlock, NonLatexCoverableBlock {

    public DarkLatexPuddleBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.SLIME_BLOCK).strength(1f, 10f).speedFactor(0.8f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderLayer() {
        ItemBlockRenderTypes.setRenderLayer(ChangedAddonBlocks.DARK_LATEX_PUDDLE.get(), renderType -> renderType == RenderType.cutout());
    }

    private static boolean isPlayerDLOrPuro(Player player) {
        return ProcessTransfur.getPlayerTransfurVariantSafe(player).map(tf ->
                tf.getLatexType() == LatexType.DARK_LATEX || tf.getFormId().toString().contains("puro_kind")).orElse(false);
    }

    private static void alertNearbyDL(Level level, double x, double y, double z, Entity entity) {
        // I Want to allow the players to be able to use the armor stand as a "bait" in farms
        if (!(level instanceof ServerLevel sLevel)/* || entity instanceof ArmorStand */) return;
        if (entity instanceof ChangedEntity chEntity && chEntity.getLatexType() == LatexType.DARK_LATEX) return;
        if (entity instanceof Player player) {
            if (isPlayerDLOrPuro(player)) return;
            if (player.isSteppingCarefully()) return;
            if (player.isSpectator()) return;
        }


        BlockPos pos = new BlockPos(x, y, z);
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) return;
        if (!(be instanceof DarkLatexPuddleBlockEntity darkLatexPuddleBlockEntity)) return;

        byte cooldown = darkLatexPuddleBlockEntity.cooldown;

        Vec3 center = new Vec3(x, y, z);

        //if (be.getTileData().getByte("cooldown") > 0) return; fixme
        if (cooldown > 0) return;

        // Reproduz som para dark latex próximos
        PacketUtil.playSound(sLevel, DarkLatexPuddleBlock::isPlayerDLOrPuro,
                x, y, z, ChangedAddonSounds.WARN, SoundSource.BLOCKS, 1, 1);

        // Aplica cooldown no bloco
        darkLatexPuddleBlockEntity.cooldown = 30;
        darkLatexPuddleBlockEntity.setChanged();

        //be.getTileData().putByte("cooldown", (byte) 30); fixme
        //be.setChanged(); fixme

        // Atração de dark latex
        AABB area = AABB.ofSize(center, 20, 20, 20); // raio de 10 blocos
        level.getEntitiesOfClass(ChangedEntity.class, area, e ->
                        e != entity && e.getTransfurVariant().getLatexType() == LatexType.DARK_LATEX && !e.getTransfurVariant().getFormId().toString().contains("puro_kind"))
                .forEach(nearby -> nearby.getNavigation().moveTo(x, y, z, 0.3));
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
        return Shapes.empty();
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH ->
                    Shapes.or(box(6, 0.25, 1.5, 14.5, 1.5, 10), box(5.5, 0, 1, 15, 1, 10.5), box(0.75, 0, 10, 5.75, 1, 15), box(1.25, 0.25, 10.5, 5.25, 1.5, 14.5));
            case EAST ->
                    Shapes.or(box(6, 0.25, 6, 14.5, 1.5, 14.5), box(5.5, 0, 5.5, 15, 1, 15), box(1, 0, 0.75, 6, 1, 5.75), box(1.5, 0.25, 1.25, 5.5, 1.5, 5.25));
            case WEST ->
                    Shapes.or(box(1.5, 0.25, 1.5, 10, 1.5, 10), box(1, 0, 1, 10.5, 1, 10.5), box(10, 0, 10.25, 15, 1, 15.25), box(10.5, 0.25, 10.75, 14.5, 1.5, 14.75));
            default ->
                    Shapes.or(box(1.5, 0.25, 6, 10, 1.5, 14.5), box(1, 0, 5.5, 10.5, 1, 15), box(10.25, 0, 1, 15.25, 1, 6), box(10.75, 0.25, 1.5, 14.75, 1.5, 5.5));
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        if (level.isClientSide()) return;

        level.scheduleTick(pos, this, 1);

        //BlockEntity blockEntity = level.getBlockEntity(pos);  fixme
        //if (blockEntity != null)  fixme
        //    blockEntity.getTileData().putByte("cooldown", (byte) 0);   fixme
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) return;
        if (!(blockEntity instanceof DarkLatexPuddleBlockEntity darkLatexPuddleBlockEntity)) return;

        byte cooldown = darkLatexPuddleBlockEntity.cooldown;//blockEntity.getTileData().getByte("cooldown");
        if (cooldown > 0) {
            //blockEntity.getTileData().putByte("cooldown", (byte) Math.max(0, cooldown - 1)); fixme
            //blockEntity.setChanged(); fixme
            darkLatexPuddleBlockEntity.cooldown = (byte) Math.max(0, cooldown - 1);
            darkLatexPuddleBlockEntity.setChanged();
        }

        level.scheduleTick(pos, this, 1);
    }

    @Override
    public void attack(BlockState blockstate, Level world, BlockPos pos, Player entity) {
        alertNearbyDL(world, pos.getX(), pos.getY(), pos.getZ(), entity);
    }

    @Override
    public void entityInside(BlockState blockstate, Level world, BlockPos pos, Entity entity) {
        alertNearbyDL(world, pos.getX(), pos.getY(), pos.getZ(), entity);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DarkLatexPuddleBlockEntity(pos, state);
    }
}
