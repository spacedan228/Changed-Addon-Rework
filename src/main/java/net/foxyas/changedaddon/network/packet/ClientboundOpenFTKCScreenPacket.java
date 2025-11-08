package net.foxyas.changedaddon.network.packet;

import net.foxyas.changedaddon.qte.FightToKeepConsciousness;
import net.minecraft.network.FriendlyByteBuf;

public record ClientboundOpenFTKCScreenPacket(FightToKeepConsciousness.MinigameType minigameType) {

    public ClientboundOpenFTKCScreenPacket(FriendlyByteBuf buf) {
        this(buf.readEnum(FightToKeepConsciousness.MinigameType.class));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(minigameType);
    }
}
