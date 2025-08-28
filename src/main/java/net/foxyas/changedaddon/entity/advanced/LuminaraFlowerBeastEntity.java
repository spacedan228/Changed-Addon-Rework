package net.foxyas.changedaddon.entity.advanced;

import net.foxyas.changedaddon.entity.defaults.AbstractBasicOrganicChangedEntity;
import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.variants.VariantExtraStats;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;

public class LuminaraFlowerBeastEntity extends AbstractBasicOrganicChangedEntity implements VariantExtraStats {

    private static final EntityDataAccessor<Boolean> AWAKENED = SynchedEntityData.defineId(LuminaraFlowerBeastEntity.class, EntityDataSerializers.BOOLEAN);

    public LuminaraFlowerBeastEntity(PlayMessages.SpawnEntity ignoredPacket, Level world) {
        this(ChangedAddonEntities.LUMINARA_FLOWER_BEAST.get(), world);
    }

    public LuminaraFlowerBeastEntity(EntityType<? extends ChangedEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AWAKENED, false);
    }

    public boolean isAwakened() {
        return this.entityData.get(AWAKENED);
    }

    public void setAwakened(boolean value) {
        this.entityData.set(AWAKENED, value);
    }

    @Override
    public float extraBlockBreakSpeed() {
        return 0;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.saveExtraData(tag);
    }

    public void saveExtraData(CompoundTag tag) {
        tag.putBoolean("Awakened", this.isAwakened());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readExtraData(tag);
    }

    public void readExtraData(CompoundTag tag) {
        if (tag.contains("Awakened")) this.setAwakened(tag.getBoolean("Awakened"));
    }

    @Override
    public FlyType getFlyType() {
        return this.isAwakened() ? FlyType.BOTH : FlyType.NONE;
    }
}
