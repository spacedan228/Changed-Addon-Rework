package net.foxyas.changedaddon.network.packet;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.variant.TransfurVariantInstanceExtensor;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistry;

import java.util.UUID;
import java.util.function.Supplier;

public class VariantSecondAbilityActivate {
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
        this.ability = ChangedRegistry.ABILITY.readRegistryObject(buffer);
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeBoolean(keyState);
        ChangedRegistry.ABILITY.writeRegistryObject(buffer, ability);
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

                        GrabEntityAbility.getGrabberSafe(sender).ifPresent(entity -> {
                            if (entity.getAbilityInstanceSafe(ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                                    .map(ability -> ability.grabbedHasControl).orElse(false)) {
                                entity.getEntity().interact(sender, InteractionHand.MAIN_HAND);
                            }
                        });

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

                            ChangedAddonMod.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> sender), this);
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
