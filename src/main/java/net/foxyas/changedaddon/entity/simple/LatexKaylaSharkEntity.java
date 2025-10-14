package net.foxyas.changedaddon.entity.simple;

import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.util.ColorUtil;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.beast.LatexTigerShark;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public class LatexKaylaSharkEntity extends LatexTigerShark implements GenderedEntity {

    protected static final EntityDataAccessor<Boolean> GLOWING_STATE = SynchedEntityData.defineId(LatexKaylaSharkEntity.class, EntityDataSerializers.BOOLEAN);;

    public LatexKaylaSharkEntity(EntityType<? extends LatexKaylaSharkEntity> type, Level level) {
        super(type, level);
    }

    public LatexKaylaSharkEntity(PlayMessages.SpawnEntity ignoredSpawnEntity, Level level) {
        this(ChangedAddonEntities.LATEX_KAYLA_SHARK.get(), level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GLOWING_STATE, false);
    }

    public void setGlowingState(boolean value) {
        this.entityData.set(GLOWING_STATE, value);
    }

    public boolean getGlowingState() {
        return this.entityData.get(GLOWING_STATE);
    }

    @Override
    public Gender getGender() {
        return Gender.FEMALE;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        return ColorUtil.lerpTFColors(this.maybeGetUnderlying(),
                1,
                Color3.parseHex("#d14f64"),
                Color3.parseHex("#f7d74f"),
                Color3.parseHex("#6394b1"));
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData finalizedSpawn = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        BasicPlayerInfo basicPlayerInfo = this.getBasicPlayerInfo();
        basicPlayerInfo.setEyeStyle(EyeStyle.TALL);
        basicPlayerInfo.setScleraColor(Color3.parseHex("#060606"));
        basicPlayerInfo.setLeftIrisColor(Color3.parseHex("#a81dc8"));
        basicPlayerInfo.setRightIrisColor(Color3.parseHex("#4cc4f5"));

        return finalizedSpawn;
    }

    public static LootTable.@NotNull Builder KaylaLoot() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.PINK_DYE)
                                .setWeight(1)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                        .add(LootItem.lootTableItem(ChangedItems.LATEX_BASE::get)
                                .setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
                        .add(LootItem.lootTableItem(Items.COD)
                                .setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))))
                        .add(LootItem.lootTableItem(Items.SALMON)
                                .setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))))
                        .add(LootItem.lootTableItem(Items.TROPICAL_FISH)
                                .setWeight(1)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F))))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0, 3)))
                );
    }
}
