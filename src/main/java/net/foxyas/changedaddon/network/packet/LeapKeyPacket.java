package net.foxyas.changedaddon.network.packet;

import net.foxyas.changedaddon.procedures.LeapProcedure;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record LeapKeyPacket(int type, int pressedMs) {

    public LeapKeyPacket(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(type);
        buf.writeVarInt(pressedMs);
    }

    public static void handler(LeapKeyPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> pressAction(context.getSender(), message.type, message.pressedMs));
        context.setPacketHandled(true);
    }

    public static void pressAction(Player player, int type, int pressedms) {
        if (player == null) return;
        Level world = player.level;
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        if (type == 0) {

            LeapProcedure.execute(world, x, y, z, player);
        }
    }
}
