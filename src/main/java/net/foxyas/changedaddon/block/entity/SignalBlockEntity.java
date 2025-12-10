package net.foxyas.changedaddon.block.entity;

import net.foxyas.changedaddon.effect.particles.SignalParticle;
import net.foxyas.changedaddon.init.ChangedAddonBlockEntities;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.init.ChangedAddonParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class SignalBlockEntity extends BlockEntity {

    private SignalParticle signalParticle = null;

    public SignalBlockEntity(BlockPos position, BlockState state) {
        super(ChangedAddonBlockEntities.SIGNAL_BLOCK.get(), position, state);
    }

    public SignalParticle getSignalParticle() {
        return this.level != null && this.level.isClientSide() ? signalParticle : null;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide() && level instanceof ClientLevel clientLevel) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            // Partícula só se estiver segurando o item
            Item pItem = ChangedAddonItems.SIGNAL_CATCHER.get();
            boolean holding = player.getMainHandItem().is(pItem) || player.getOffhandItem().is(pItem);
            if (signalParticle == null) {
                if (!holding) {
                    return;
                }
                Particle particle = Minecraft.getInstance().particleEngine.createParticle(
                        ChangedAddonParticleTypes.signal(8, new ItemStack(pItem)),
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
                if (particle instanceof SignalParticle sp) signalParticle = sp;
            } else if (!signalParticle.isAlive() || signalParticle.getAge() >= signalParticle.getLifetime()) {
                this.signalParticle = null;
            }
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }
}
