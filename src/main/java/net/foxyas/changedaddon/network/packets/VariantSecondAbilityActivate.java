package net.foxyas.changedaddon.network.packets;

import java.util.UUID;
import java.util.function.Supplier;

import net.foxyas.changedaddon.variants.TransfurVariantInstanceExtensor;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.network.packet.ChangedPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class VariantSecondAbilityActivate implements ChangedPacket {
    final UUID uuid;
    final boolean keyState;
    final AbstractAbility<?> ability;

    public static VariantSecondAbilityActivate openRadial(Player player) {
        return new VariantSecondAbilityActivate(player, false);
    }

    public VariantSecondAbilityActivate(Player player, boolean keyState, AbstractAbility<?> ability) {
        this.uuid = player.getUUID();
        this.keyState = keyState;
        this.ability = ability;
    }

    public VariantSecondAbilityActivate(Player player, boolean keyState) {
        this.uuid = player.getUUID();
        this.keyState = keyState;
        this.ability = null;
    }

    public VariantSecondAbilityActivate(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
        this.keyState = buffer.readBoolean();
        this.ability = ChangedRegistry.ABILITY.get().getValue(buffer.readInt());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.uuid);
        buffer.writeBoolean(this.keyState);
        buffer.writeInt(ChangedRegistry.ABILITY.get().getID(this.ability));
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                if (!sender.getUUID().equals(this.uuid)) {
                    return;
                }

                ProcessTransfur.ifPlayerTransfurred(sender, (variant) -> {
                    context.setPacketHandled(true);
                    if (variant instanceof TransfurVariantInstanceExtensor transfurVariantInstanceExtensor) {
                        if (!variant.isTemporaryFromSuit()) {
                            if (this.ability != null) {
                                transfurVariantInstanceExtensor.setSecondSelectedAbility(this.ability);
                            }

                            if (!this.keyState && this.ability == null) {
                                if (!sender.isUsingItem()) {
                                    sender.openMenu(new SimpleMenuProvider((id, inventory, givenPlayer) -> new AbilityRadialMenu(id, inventory, null), AbilityRadialMenu.CONTAINER_TITLE));
                                }
                            } else {
                                transfurVariantInstanceExtensor.setSecondAbilityKeyState(this.keyState);
                            }

                            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> sender), this);
                        }
                    }
                });
            } else {
                ProcessTransfur.ifPlayerTransfurred(UniversalDist.getLevel().getPlayerByUUID(this.uuid), (player, variant) -> {
                    context.setPacketHandled(true);
                    if (variant instanceof TransfurVariantInstanceExtensor transfurVariantInstanceExtensor) {
                        if (!variant.isTemporaryFromSuit()) {
                            if (this.ability != null) {
                                transfurVariantInstanceExtensor.setSecondSelectedAbility(this.ability);
                            }

                            if (this.keyState || this.ability != null) {
                                transfurVariantInstanceExtensor.setSecondAbilityKeyState(this.keyState);
                            }

                        }
                    }
                });
            }

        });
    }
}
