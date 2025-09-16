package net.foxyas.changedaddon.network;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.qte.FightToKeepConsciousness;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ChangedAddonModVariables {

    public static final Capability<PlayerVariables> PLAYER_VARIABLES_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    @Mod.EventBusSubscriber
    public static class PlayerVariablesProvider implements ICapabilitySerializable<CompoundTag> {

        private final PlayerVariables playerVariables = new PlayerVariables();
        private final LazyOptional<PlayerVariables> instance = LazyOptional.of(() -> playerVariables);

        @SubscribeEvent
        public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player player && !(player instanceof FakePlayer)) {
                event.addCapability(
                        ChangedAddonMod.resourceLoc("player_variables"),
                        new ChangedAddonModVariables.PlayerVariablesProvider()
                );
            }
        }

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
            return cap == PLAYER_VARIABLES_CAPABILITY ? instance.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return playerVariables.writeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            playerVariables.readNBT(nbt);
        }
    }

    public static class PlayerVariables {
        @Nullable
        public FightToKeepConsciousness.MinigameType FTKCminigameType = null;

        public float consciousnessFightProgress = 0;

        public double LatexInfectionCooldown = 0.0;
        public double untransfurProgress = 0.0;

        public boolean showWarns = true;
        public boolean resetTransfurAdvancements = false;
        public boolean actCooldown = false;
        public boolean areDarkLatex = false;
        public boolean Exp009TransfurAllowed = false;
        public boolean Exp10TransfurAllowed = false;

        /**
         * Should never return null unless FakePlayer is used or the player is dead
         */
        public static @Nullable PlayerVariables of(@NotNull Player player) {
            return player.getCapability(PLAYER_VARIABLES_CAPABILITY).resolve().orElse(null);
        }

        public static @NotNull PlayerVariables ofOrDefault(@NotNull Player player) {
            return player.getCapability(PLAYER_VARIABLES_CAPABILITY).resolve().orElseGet(PlayerVariables::new);
        }

        public static @NotNull PlayerVariables nonNullOf(@NotNull Player player) {
            return player.getCapability(PLAYER_VARIABLES_CAPABILITY).orElseThrow(() -> new IllegalStateException("Player Variables Capability expected but not found!"));
        }

        public void syncPlayerVariables(Entity entity) {
            if (entity instanceof ServerPlayer serverPlayer)
                ChangedAddonMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PlayerVariablesSyncMessage(this));
        }

        public void copyTo(PlayerVariables other, boolean wasDeath) {
            other.resetTransfurAdvancements = resetTransfurAdvancements;
            other.areDarkLatex = areDarkLatex;
            other.untransfurProgress = untransfurProgress;
            other.Exp009TransfurAllowed = Exp009TransfurAllowed;
            other.Exp10TransfurAllowed = Exp10TransfurAllowed;
            if (!wasDeath) {
                other.consciousnessFightProgress = consciousnessFightProgress;
                other.FTKCminigameType = FTKCminigameType;
                other.actCooldown = actCooldown;
                other.LatexInfectionCooldown = LatexInfectionCooldown;
            }
        }

        public CompoundTag writeNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean("showWarns", showWarns);
            nbt.putFloat("consciousnessFightProgress", consciousnessFightProgress);
            nbt.putByte("FTKCminigameType", FTKCminigameType != null ? (byte) FTKCminigameType.ordinal() : -1);
            nbt.putBoolean("resetTransfurAdvancements", resetTransfurAdvancements);
            nbt.putBoolean("actCooldown", actCooldown);
            nbt.putBoolean("areDarkLatex", areDarkLatex);
            nbt.putDouble("LatexInfectionCooldown", LatexInfectionCooldown);
            nbt.putDouble("UntransfurProgress", untransfurProgress);
            nbt.putBoolean("Exp009TransfurAllowed", Exp009TransfurAllowed);
            nbt.putBoolean("Exp10TransfurAllowed", Exp10TransfurAllowed);
            return nbt;
        }

        public void readNBT(Tag Tag) {
            CompoundTag nbt = (CompoundTag) Tag;
            showWarns = nbt.getBoolean("showWarns");
            consciousnessFightProgress = nbt.getFloat("consciousnessFightProgress");
            
            if(nbt.contains("FTKCminigameType")) {
                FTKCminigameType = nbt.getByte("FTKCminigameType") != (byte) -1
                        ? FightToKeepConsciousness.MinigameType.values()[nbt.getByte("FTKCminigameType")]
                        : null;
            } else FTKCminigameType = null;

            resetTransfurAdvancements = nbt.getBoolean("resetTransfurAdvancements");
            actCooldown = nbt.getBoolean("actCooldown");
            areDarkLatex = nbt.getBoolean("areDarkLatex");
            LatexInfectionCooldown = nbt.getDouble("LatexInfectionCooldown");
            untransfurProgress = nbt.getDouble("UntransfurProgress");
            Exp009TransfurAllowed = nbt.getBoolean("Exp009TransfurAllowed");
            Exp10TransfurAllowed = nbt.getBoolean("Exp10TransfurAllowed");
        }
    }

    public static class PlayerVariablesSyncMessage {
        public PlayerVariables data;

        public PlayerVariablesSyncMessage(FriendlyByteBuf buffer) {
            this.data = new PlayerVariables();
            this.data.readNBT(buffer.readNbt());
        }

        public PlayerVariablesSyncMessage(PlayerVariables data) {
            this.data = data;
        }

        public static void buffer(PlayerVariablesSyncMessage message, FriendlyByteBuf buffer) {
            buffer.writeNbt(message.data.writeNBT());
        }

        public static void handler(PlayerVariablesSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.setPacketHandled(true);
            if (context.getDirection().getReceptionSide().isServer()) return;

            context.enqueueWork(() -> {
                assert Minecraft.getInstance().player != null;
                PlayerVariables variables = Minecraft.getInstance().player.getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables());
                variables.showWarns = message.data.showWarns;
                variables.consciousnessFightProgress = message.data.consciousnessFightProgress;
                variables.FTKCminigameType = message.data.FTKCminigameType;
                variables.resetTransfurAdvancements = message.data.resetTransfurAdvancements;
                variables.actCooldown = message.data.actCooldown;
                variables.areDarkLatex = message.data.areDarkLatex;
                variables.LatexInfectionCooldown = message.data.LatexInfectionCooldown;
                variables.untransfurProgress = message.data.untransfurProgress;
                variables.Exp009TransfurAllowed = message.data.Exp009TransfurAllowed;
                variables.Exp10TransfurAllowed = message.data.Exp10TransfurAllowed;
            });
        }
    }
}
