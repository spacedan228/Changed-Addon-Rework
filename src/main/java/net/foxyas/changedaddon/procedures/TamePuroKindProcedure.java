package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.entity.simple.PuroKindFemaleEntity;
import net.foxyas.changedaddon.entity.simple.PuroKindMaleEntity;
import net.foxyas.changedaddon.entity.simple.WolfyEntity;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.ltxprogrammer.changed.entity.beast.AbstractDarkLatexWolf;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TamePuroKindProcedure {

    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() != event.getPlayer().getUsedItemHand()) return;

        Entity entity = event.getTarget();
        Player player = event.getPlayer();

        if (!(entity instanceof AbstractDarkLatexWolf wolf) || player == null) return;

        if (entity instanceof PuroKindMaleEntity || entity instanceof PuroKindFemaleEntity) {
            if(wolf.isTame()) return;

            if (player.getMainHandItem().is(ChangedItems.ORANGE.get())) {
                wolf.tame(player);
            } else if (player.getOffhandItem().is(ChangedItems.ORANGE.get())) {
                wolf.tame(player);
            }

            return;
        }
        
        if (entity instanceof WolfyEntity) {
            if(wolf.isTame()) return;

            if (player.getMainHandItem().is(ChangedAddonItems.FOXTA.get())) {
                wolf.tame(player);
            } else if (player.getOffhandItem().is(ChangedAddonItems.FOXTA.get())) {
                wolf.tame(player);
            }
        }
    }
}
