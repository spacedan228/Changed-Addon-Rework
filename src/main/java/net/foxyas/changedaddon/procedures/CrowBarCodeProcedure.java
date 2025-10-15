package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.item.CrowbarItem;
import net.ltxprogrammer.changed.block.AbstractLabDoor;
import net.ltxprogrammer.changed.block.AbstractLargeLabDoor;
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
        if (event.getHand() != event.getPlayer().getUsedItemHand()) return;

        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof CrowbarItem)) return;

        Player player = event.getPlayer();
        if (player.getCooldowns().isOnCooldown(ChangedAddonItems.CROWBAR.get())) return;

        Level level = player.level;
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof AbstractLabDoor abstractLabDoor) {
            if (abstractLabDoor.openDoor(state, level, pos)) {
                player.getCooldowns().addCooldown(stack.getItem(), 60);
            }
            return;
        }

        if (state.getBlock() instanceof AbstractLargeLabDoor abstractLargeLabDoor) {
            if (abstractLargeLabDoor.openDoor(state, level, pos)) {
                player.getCooldowns().addCooldown(stack.getItem(), 60);
            }
        }
    }
}
