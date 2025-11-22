package net.foxyas.changedaddon.entity.goals.prototype;

import net.foxyas.changedaddon.entity.advanced.PrototypeEntity;
import net.foxyas.changedaddon.util.DelayedTask;
import net.foxyas.changedaddon.util.DynamicClipContext;
import net.foxyas.changedaddon.util.FoxyasUtils;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class PruningOrangeLeavesGoal extends Goal {

    private final PrototypeEntity prototypeEntity;

    private boolean lock;
    private BlockPos targetLeave;
    private int pruneCooldown;

    public PruningOrangeLeavesGoal(PrototypeEntity prototypeEntity) {
        this.prototypeEntity = prototypeEntity;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (lock) return false;

        return !findShears().isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
        if(targetLeave == null){
            lock = true;
            new DelayedTask(200, ()-> lock = false);
            return false;
        }

        return !findShears().isEmpty();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        targetLeave = findNearbyOrangeLeaves(prototypeEntity.blockPosition(), 10, prototypeEntity.getEyePosition());;
    }

    @Override
    public void tick() {
        Level level = prototypeEntity.level;
        if(targetLeave == null || isBlockInvalid(level.getBlockState(targetLeave))){
            targetLeave = findNearbyOrangeLeaves(prototypeEntity.blockPosition(), 10, prototypeEntity.getEyePosition());
            if(targetLeave == null) return;
        }

        prototypeEntity.getLookControl().setLookAt(targetLeave.getX(), targetLeave.getY(), targetLeave.getZ(),
                30, 30);
        prototypeEntity.getNavigation().moveTo(targetLeave.getX(), targetLeave.getY(), targetLeave.getZ(), 0.25f);

        if(pruneCooldown > 0){
            pruneCooldown--;
            return;
        }

        if (targetLeave.distSqr(prototypeEntity.blockPosition()) <= (3.5f * 3.5f)) {
            pruneOrangeLeaves();
            targetLeave = findNearbyOrangeLeaves(prototypeEntity.blockPosition(), 10, prototypeEntity.getEyePosition());
            pruneCooldown = 5;
        }
    }

    @Override
    public void stop() {
        prototypeEntity.getNavigation().stop();
        targetLeave = null;
        pruneCooldown = 0;
    }

    private ItemStack findShears(){
        ItemStack shears = prototypeEntity.getMainHandItem();
        if(!shears.isEmpty() && shears.is(Tags.Items.SHEARS)) return shears;

        shears = prototypeEntity.getOffhandItem();
        return !shears.isEmpty() && shears.is(Tags.Items.SHEARS) ? shears : ItemStack.EMPTY;
    }

    private boolean isBlockInvalid(BlockState state){
        return !state.is(ChangedBlocks.ORANGE_TREE_LEAVES.get());
    }

    @Nullable
    private BlockPos findNearbyOrangeLeaves(BlockPos center, int range, Vec3 eyePos) {
        BlockPos best = null;
        float bestDist = Float.MAX_VALUE, dist;

        Level level = prototypeEntity.level;
        // Evite .toList() para não alocar tudo; itere o stream diretamente
        for (BlockPos pos : (Iterable<BlockPos>) FoxyasUtils.betweenClosedStreamSphere(center, range, range)::iterator) {
            BlockState state = level.getBlockState(pos);
            if (isBlockInvalid(state)) continue;

            // Distância do olho ao centro do bloco (mais precisa)
            dist = (float) eyePos.distanceToSqr(Vec3.atCenterOf(pos));
            if (dist >= bestDist) continue;

            BlockHitResult hit = level.clip(eyeContext(pos));

            if (hit.getType() == HitResult.Type.BLOCK && hit.getBlockPos().equals(pos)) {
                bestDist = dist;
                best = pos.immutable();
            }
        }
        return best;
    }

    private @NotNull ClipContext eyeContext(BlockPos pos) {
        return new DynamicClipContext(
                prototypeEntity.getEyePosition(),
                Vec3.atCenterOf(pos),
                DynamicClipContext.IGNORE_TRANSLUCENT,
                ClipContext.Fluid.ANY::canPick,
                CollisionContext.of(prototypeEntity));
    }

    private void pruneOrangeLeaves() {
        ItemStack shears = prototypeEntity.getMainHandItem();
        InteractionHand hand = InteractionHand.MAIN_HAND;
        if(shears.isEmpty() || !shears.is(Tags.Items.SHEARS)) {
            shears = prototypeEntity.getOffhandItem();
            if(shears.isEmpty() || !shears.is(Tags.Items.SHEARS)) return;

            hand = InteractionHand.OFF_HAND;
        }

        Level level = prototypeEntity.level;
        BlockState state = level.getBlockState(targetLeave);
        if(isBlockInvalid(state)) return;

        BlockState newState = Blocks.OAK_LEAVES.defaultBlockState();

        if (state.hasProperty(LeavesBlock.DISTANCE)) {
            newState = newState.setValue(LeavesBlock.DISTANCE, state.getValue(LeavesBlock.DISTANCE));
        }

        if (state.hasProperty(LeavesBlock.PERSISTENT)) {
            newState = newState.setValue(LeavesBlock.PERSISTENT, state.getValue(LeavesBlock.PERSISTENT));
        }

        int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, shears);
        UniformInt uniformInt;

        if (fortune > 0) {
            uniformInt = UniformInt.of(fortune, 8 * fortune);
        } else {
            uniformInt = UniformInt.of(1, 8);
        }

        ItemStack orangeStack = new ItemStack(ChangedItems.ORANGE.get());
        orangeStack.setCount(uniformInt.sample(prototypeEntity.getRandom()));
        orangeStack = prototypeEntity.addToInventory(orangeStack, false);
        if(!orangeStack.isEmpty()) Block.popResource(level, targetLeave, orangeStack);

        shears.hurtAndBreak(1, prototypeEntity, (prototype) -> {});

        prototypeEntity.getLookControl().setLookAt(Vec3.atCenterOf(targetLeave));
        prototypeEntity.swing(hand);

        level.setBlockAndUpdate(targetLeave, newState);
        level.playSound(null, prototypeEntity, SoundEvents.SNOW_GOLEM_SHEAR, SoundSource.BLOCKS, 1, 1);
    }
}
