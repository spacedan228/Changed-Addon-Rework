package net.foxyas.changedaddon.network.packet;

import net.foxyas.changedaddon.menu.TransfurSoundsGuiMenu;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class OpenExtraDetailsPacket {

    public OpenExtraDetailsPacket() {}

    public OpenExtraDetailsPacket(FriendlyByteBuf buf) {
        this();
    }

    public void encode(FriendlyByteBuf buf) {}

    public static void handler(OpenExtraDetailsPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> pressAction(context.getSender()));
        context.setPacketHandled(true);
    }

    private static void pressAction(Player player) {
        if (player == null || player.level.isClientSide || player.isSpectator()) return;

        if (ProcessTransfur.isPlayerTransfurred(player)) {
            NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return Component.literal("TransfurSoundsGui");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
                    return new TransfurSoundsGuiMenu(id, inventory);
                }
            });
        } else {
            ChangedAddonVariables.PlayerVariables vars = ChangedAddonVariables.of(player);
            if (vars != null && vars.showWarns) {
                player.displayClientMessage(Component.translatable("changedaddon.when_not.transfur"), true);
            }
        }
    }
}
