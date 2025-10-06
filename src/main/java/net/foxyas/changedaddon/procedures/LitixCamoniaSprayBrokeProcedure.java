package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;

@Mod.EventBusSubscriber
public class LitixCamoniaSprayBrokeProcedure {

    @SubscribeEvent
    public static void onItemDestroyed(PlayerDestroyItemEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        ItemStack itemstack = event.getOriginal();
        if (itemstack.is(ChangedAddonItems.LITIX_CAMONIA_SPRAY.get())
                || itemstack.is(ChangedAddonItems.WHITE_LATEX_SPRAY.get())
                || itemstack.is(ChangedAddonItems.DARK_LATEX_SPRAY.get())) {
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ChangedAddonItems.EMPTY_SPRAY.get()));
        }
    }
}
