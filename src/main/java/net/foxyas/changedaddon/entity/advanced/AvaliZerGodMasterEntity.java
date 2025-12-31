package net.foxyas.changedaddon.entity.advanced;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;

public class AvaliZerGodMasterEntity extends AvaliEntity {
    public AvaliZerGodMasterEntity(PlayMessages.SpawnEntity ignoredPacket, Level world) {
        super(ignoredPacket, world);
    }

    public AvaliZerGodMasterEntity(EntityType<? extends ChangedEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineAvaliSyncData() {
    }

    @Override
    public void saveColors(CompoundTag originalTag) {
    }

    @Override
    public void readColors(CompoundTag originalTag) {
    }

    @Override
    protected void applyRandomColors() {
    }
}
