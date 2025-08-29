package net.foxyas.changedaddon.event;

import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PrunOrangeLeavesEvent {

    @SubscribeEvent
    public static void rightClickOrangeLeaves(PlayerInteractEvent.RightClickBlock rightClickBlockEvent) {
        Player player = rightClickBlockEvent.getPlayer();
        ItemStack usedItem = rightClickBlockEvent.getItemStack();
        Level world = rightClickBlockEvent.getWorld();
        BlockHitResult blockHitResult = rightClickBlockEvent.getHitVec();
        BlockPos position = blockHitResult.getBlockPos();
        BlockState state = world.getBlockState(position);
        InteractionHand hand = rightClickBlockEvent.getHand();
        if (world instanceof ServerLevel serverLevel) {
            if (state.is(ChangedBlocks.ORANGE_TREE_LEAVES.get())) {
                boolean hasShearsLikeItemInHand = usedItem.getItem() instanceof HoeItem || usedItem.is(Tags.Items.SHEARS);

                if (hasShearsLikeItemInHand) {
                    BlockState newState = Blocks.OAK_LEAVES.defaultBlockState();

                    if (state.hasProperty(LeavesBlock.DISTANCE)) {
                        newState = newState.setValue(LeavesBlock.DISTANCE, state.getValue(LeavesBlock.DISTANCE));
                    }

                    if (state.hasProperty(LeavesBlock.PERSISTENT)) {
                        newState = newState.setValue(LeavesBlock.PERSISTENT, state.getValue(LeavesBlock.PERSISTENT));
                    }

                    int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, usedItem);
                    UniformInt uniformInt;

                    if (fortune > 0) {
                        uniformInt = UniformInt.of(fortune, 8 * fortune);
                    } else {
                        uniformInt = UniformInt.of(1, 8);
                    }

                    ItemStack orangeStack = new ItemStack(ChangedItems.ORANGE.get());
                    orangeStack.setCount(uniformInt.sample(player.getRandom()));
                    if (!usedItem.isEmpty()) {
                        usedItem.hurtAndBreak(1, player, (ignored) -> {
                        });
                        player.swing(hand, true);
                    }

                    serverLevel.setBlockAndUpdate(position, newState);
                    LeavesBlock.popResource(serverLevel, position, orangeStack);
                    serverLevel.playSound(null, player, SoundEvents.SNOW_GOLEM_SHEAR, SoundSource.BLOCKS, 1, 1);
                }
            }
        }

    }

    /*
     * Silly Idea But I didn't get it working
    @SubscribeEvent
    public static void rightClickOrangeLeavesItem(PlayerInteractEvent.EntityInteract rightClickEntityEvent) {
        Player player = rightClickEntityEvent.getPlayer();
        ItemStack usedItem = rightClickEntityEvent.getItemStack();
        Level world = rightClickEntityEvent.getWorld();
        InteractionHand hand = rightClickEntityEvent.getHand();
        Entity entity = rightClickEntityEvent.getTarget();
        if (!(entity instanceof ItemEntity itemEntity)) {
            return;
        }
        ItemStack itemEntityStack = itemEntity.getItem();
        if (!(itemEntityStack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        if (world instanceof ServerLevel serverLevel) {
            if (blockItem.getBlock() == (ChangedBlocks.ORANGE_TREE_LEAVES.get())) {
                boolean hasShearsLikeItemInHand = usedItem.getItem() instanceof HoeItem || usedItem.is(Tags.Items.SHEARS);

                if (hasShearsLikeItemInHand) {
                    ItemStack newState = new ItemStack(Blocks.OAK_LEAVES.asItem());
                    int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, usedItem);
                    UniformInt uniformInt;

                    if (fortune > 0) {
                        uniformInt = UniformInt.of(fortune, 8 * fortune);
                    } else {
                        uniformInt = UniformInt.of(1, 8);
                    }

                    ItemStack orangeStack = new ItemStack(ChangedItems.ORANGE.get());
                    orangeStack.setCount(uniformInt.sample(player.getRandom()));
                    if (!usedItem.isEmpty()) {
                        usedItem.hurtAndBreak(1, player, (ignored) -> {
                        });
                        player.swing(hand, true);
                    }

                    itemEntity.setItem(newState);
                    LeavesBlock.popResource(serverLevel, rightClickEntityEvent.getPos(), orangeStack);
                    serverLevel.playSound(null, player, SoundEvents.SNOW_GOLEM_SHEAR, SoundSource.BLOCKS, 1, 1);
                }
            }
        }

    }
    */
}
