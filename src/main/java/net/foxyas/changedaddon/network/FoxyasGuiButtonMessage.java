package net.foxyas.changedaddon.network;

import net.foxyas.changedaddon.entity.advanced.FoxyasEntity;
import net.foxyas.changedaddon.world.inventory.FoxyasGui2Menu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record FoxyasGuiButtonMessage(int buttonId, int entityId) {

    public FoxyasGuiButtonMessage(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt());
    }

    public static void buffer(FoxyasGuiButtonMessage message, FriendlyByteBuf buf) {
        buf.writeVarInt(message.buttonId);
        buf.writeVarInt(message.entityId);
    }

    public static void handler(FoxyasGuiButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player player = context.getSender();
            Entity entity = player.level.getEntity(message.entityId);
            if(!(entity instanceof FoxyasEntity foxyas)) return;
            handleButtonAction(player, message.buttonId, foxyas);
        });
        context.setPacketHandled(true);
    }

    static void handleButtonAction(Player player, int buttonID, FoxyasEntity entity) {
        if(player.level.isClientSide) return;
        // security measure to prevent arbitrary chunk generation
        if (!entity.isAddedToWorld()) return;

        if (buttonID == 0) {
            entity.doTrade();
            return;
        }

        if (buttonID == 1) {
            if (!(player instanceof ServerPlayer sPlayer)) return;
            NetworkHooks.openGui(sPlayer, new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return new TextComponent("FoxyasGui2");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
                    return new FoxyasGui2Menu(id, inventory, entity);
                }
            }, buf -> buf.writeVarInt(entity.getId()));
        }
    }
}
