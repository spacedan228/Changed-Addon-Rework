package net.foxyas.changedaddon.entity.advanced;

import net.ltxprogrammer.changed.entity.ChangedEntity;
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
        this.entityData.define(SIZE_SCALE, 0.8f);
    }

    @Override
    public void saveColors(CompoundTag originalTag) {
        originalTag.putFloat("size_scale", getDimensionScale());
    }

    @Override
    public void readColors(CompoundTag originalTag) {
        if (originalTag.contains("size_scale")) {
            setDimensionScale(originalTag.getFloat("size_scale"));
        }
    }

    @Override
    protected void applyRandomColors() {
    }
}
