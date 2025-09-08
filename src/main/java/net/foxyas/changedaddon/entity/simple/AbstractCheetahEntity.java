package net.foxyas.changedaddon.entity.simple;

import net.foxyas.changedaddon.entity.interfaces.ChangedEntityExtension;
import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.util.ColorUtil;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.beast.AbstractSnowLeopard;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.init.ChangedMobCategories;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class AbstractCheetahEntity extends AbstractSnowLeopard {

    public AbstractCheetahEntity(EntityType<? extends AbstractSnowLeopard> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.4F);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.9);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(20.0F);
        attributes.getInstance(ChangedAttributes.TRANSFUR_DAMAGE.get()).setBaseValue(2.5);
        attributes.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(2.0);
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        Color3 firstColor = Color3.getColor("#d8b270");
        Color3 secondColor = Color3.getColor("#634927");
        Color3 thirdColor = Color3.getColor("#ecddc1");

        float progress = ColorUtil.getPlayerTransfurProgressSafe(this.getUnderlyingPlayer(), 1);
        progress = Mth.clamp(progress, 0.0f, 1.0f);

        if (progress < 0.5f) {
            // 0.0 → 0.5 → vai de first → second
            float t = progress / 0.5f; // normaliza para 0–1
            return ColorUtil.lerpTFColor(firstColor, secondColor, t);
        } else {
            // 0.5 → 1.0 → vai de second → third
            float t = (progress - 0.5f) / 0.5f; // normaliza para 0–1
            return ColorUtil.lerpTFColor(secondColor, thirdColor, t);
        }
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData returnValue = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (this.getRandom().nextFloat() >= 0.75) {
            ChangedEntityExtension.of(this).setPacified(true);
        }
        return returnValue;
    }
}
