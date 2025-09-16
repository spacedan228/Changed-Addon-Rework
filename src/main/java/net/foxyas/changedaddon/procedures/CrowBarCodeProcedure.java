package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.item.CrowbarItem;
import net.ltxprogrammer.changed.block.AbstractLabDoor;
import net.ltxprogrammer.changed.block.AbstractLargeLabDoor;
import net.ltxprogrammer.changed.block.NineSection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CrowBarCodeProcedure {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != event.getPlayer().getUsedItemHand()) {
            return;
        }
        updateConnectedDoorBlocks(event.getWorld(), event.getPos(), event.getPlayer(), event.getItemStack());
    }

    private static void updateConnectedDoorBlocks(Level world, BlockPos pos, Player player, ItemStack itemStack) {
        BlockState DoorState = world.getBlockState(pos);
        if (!(itemStack.getItem() instanceof CrowbarItem)) {
            return;
        }
        if (DoorState.getBlock() instanceof AbstractLabDoor abstractLabDoor) {
            if ((player.getCooldowns().isOnCooldown(ChangedAddonItems.CROWBAR.get()))) {
                return;
            }
            if (abstractLabDoor.openDoor(DoorState, world, pos)) {
                player.getCooldowns().addCooldown(itemStack.getItem(), 60);
            }
        } else if (DoorState.getBlock() instanceof AbstractLargeLabDoor abstractLargeLabDoor) {
            if (DoorState.getValue(AbstractLargeLabDoor.SECTION) != NineSection.CENTER) {
            } else {
                if ((player.getCooldowns().isOnCooldown(ChangedAddonItems.CROWBAR.get()))) {
                    return;
                }
                if (abstractLargeLabDoor.openDoor(DoorState, world, pos) && !(player.getCooldowns().isOnCooldown(ChangedAddonItems.CROWBAR.get()))) {
                    player.getCooldowns().addCooldown(itemStack.getItem(), 60);
                }
            }
        }
    }


}
