package net.foxyas.changedaddon.process.variantsExtraStats;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.variant.VariantExtraStats;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedAddonMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FormsStats {

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getPlayer();
        TransfurVariantInstance<?> transfurVariantInstance = ProcessTransfur.getPlayerTransfurVariant(player);
        if (transfurVariantInstance == null) {
            return;
        }

        if (transfurVariantInstance.getSwimEfficiency() > 1 && !player.isOnGround() && player.isEyeInFluid(FluidTags.WATER)) {
            event.setNewSpeed(event.getOriginalSpeed() * 5); // Nullify the slow breaking
        }

        // Verifica se o jogador está segurando um item específico, ou se tem alguma condição
        if (transfurVariantInstance.getChangedEntity() instanceof VariantExtraStats variantExtraStats) {
            event.setNewSpeed(event.getNewSpeed() * variantExtraStats.getBlockBreakSpeedMultiplier()); // More Fast Break
        }
    }



//    Optional Player.class Mixin
//    @ModifyVariable(
//            method = "getDigSpeed",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraftforge/event/ForgeEventFactory;getBreakSpeed(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/state/BlockState;FLnet/minecraft/core/BlockPos;)F"
//            ),
//            ordinal = 0
//    )
//    private float changedAddon$cancelAirPenalty(
//            float speed,
//            BlockState state,
//            @Nullable BlockPos pos
//    ) {
//        Player self = (Player) (Object) this;
//        return ProcessTransfur.getPlayerTransfurVariantSafe(self).map((variantInstance) -> {
//            if (this.onGround && variantInstance.getSwimEfficiency() > 1 && player.isEyeInFluid(FluidTags.WATER)) {
//                return speed * 5;
//            }
//            return speed;
//        }).orElse(speed);
//    }

}
