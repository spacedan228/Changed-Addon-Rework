package net.foxyas.changedaddon.entity.advanced;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.init.ChangedAddonGameRules;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static net.ltxprogrammer.changed.entity.HairStyle.BALD;

@Mod.EventBusSubscriber
public class DazedLatexEntity extends ChangedEntity {

    // Definindo a chave de sincronização no seu código
    private static final EntityDataAccessor<Boolean> DATA_PUDDLE_MORPHED = SynchedEntityData.defineId(DazedLatexEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_REPLICATION_TIMES = SynchedEntityData.defineId(DazedLatexEntity.class, EntityDataSerializers.INT);
    private static final Set<ResourceLocation> SPAWN_BIOMES = Set.of(ResourceLocation.parse("plains"));
    public static UseItemMode PuddleForm = UseItemMode.create("PuddleForm", false, false, false, true, false);

    public boolean willTransfurTarget = false;

    public boolean wasMorphed = false;

    public DazedLatexEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ChangedAddonEntities.DAZED_LATEX.get(), world);
    }

    public DazedLatexEntity(EntityType<DazedLatexEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        this.setAttributes(this.getAttributes());
        setNoAi(false);
        setPersistenceRequired();
    }

    @SubscribeEvent
    public static void addLivingEntityToBiomes(SpawnPlacementRegisterEvent event) {
        event.register(ChangedAddonEntities.DAZED_LATEX.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DazedLatexEntity::canSpawnNear, SpawnPlacementRegisterEvent.Operation.OR);

        //Fixme add the biome modifier in the data folder
        if (SPAWN_BIOMES.contains(event.getName())) {
            event.getSpawns().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(ChangedAddonEntities.DAZED_LATEX.get(), 125, 1, 4));
        }
    }

    public static void init() {
        SpawnPlacements.register(ChangedAddonEntities.DAZED_LATEX.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DazedLatexEntity::canSpawnNear);
    }

    public static boolean isDarkEnoughToSpawn(ServerLevelAccessor p_33009_, BlockPos p_33010_, Random p_33011_) {
        if (p_33009_.getBrightness(LightLayer.SKY, p_33010_) > p_33011_.nextInt(32)) {
            return false;
        } else if (p_33009_.getBrightness(LightLayer.BLOCK, p_33010_) > 5) {
            return false;
        } else {
            int i = p_33009_.getLevel().isThundering() ? p_33009_.getMaxLocalRawBrightness(p_33010_, 10) : p_33009_.getMaxLocalRawBrightness(p_33010_);
            return i <= p_33011_.nextInt(8);
        }
    }

    private static boolean canSpawnNear(EntityType<DazedLatexEntity> entityType, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random) {
        if (world.getDifficulty() == Difficulty.PEACEFUL) {
            return false;
        }

        if (!isDarkEnoughToSpawn(world, pos, random)) {
            //ChangedAddonMod.LOGGER.info("A Try To Spawn A Dazed Entity in " + pos + "\n isn't dark enough");
            return false;
        }

        if (!world.getBiome(pos).is(Tags.Biomes.IS_PLAINS)) {
            //ChangedAddonMod.LOGGER.info("A Try To Spawn A Dazed Entity in " + pos + "\n isn't plains");
            return false;
        }

        // Certifica-se de que o bloco abaixo não é ar e é sólido
        BlockState blockBelow = world.getBlockState(pos.below());
        if (!blockBelow.isSolidRender(world, pos.below()) || !blockBelow.isFaceSturdy(world, pos.below(), Direction.UP)) {
            //ChangedAddonMod.LOGGER.info("A Try To Spawn A Dazed Entity in " + pos + "\n isn't a good block");
            return false;
        }

        // Defina uma AABB (Área de Checagem) ao redor do spawn para verificar se há Oak Log por perto.
        AABB checkArea = new AABB(pos).inflate(32); // Raio de 32 blocos ao redor

        //ChangedAddonMod.LOGGER.info("A Try To Spawn A Dazed Entity in " + pos + "\n" + nearSpawnBlock);

        return world.getBlockStatesIfLoaded(checkArea)
                .anyMatch(state -> state.is(ChangedAddonBlocks.GOO_CORE.get()));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder.add(ChangedAttributes.TRANSFUR_DAMAGE.get(), 0);
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 24);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        return builder;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PUDDLE_MORPHED, false); // Define o valor inicial como 'false'
        this.entityData.define(DATA_REPLICATION_TIMES, 0);
    }

    // Getter para checar se está no estado morphed
    public boolean isMorphed() {
        return this.entityData.get(DATA_PUDDLE_MORPHED);
    }

    // Setter para alterar o estado morphed
    public void setMorphed(boolean morphed) {
        this.entityData.set(DATA_PUDDLE_MORPHED, morphed);
    }

    public int getReplicationTimes() {
        return entityData.get(DATA_REPLICATION_TIMES);
    }

    public void setReplicationTimes(int replicationTimes) {
        this.entityData.set(DATA_REPLICATION_TIMES, replicationTimes);
    }

    public void subReplicationTimes(int replicationTimes) {
        this.entityData.set(DATA_REPLICATION_TIMES, getReplicationTimes() - replicationTimes);
    }

    public void addReplicationTimes(int replicationTimes) {
        this.entityData.set(DATA_REPLICATION_TIMES, getReplicationTimes() + replicationTimes);
    }

    protected void setAttributes(AttributeMap attributes) {
        safeSetBaseValue(attributes.getInstance(ChangedAttributes.TRANSFUR_DAMAGE.get()), 3);
        safeSetBaseValue(attributes.getInstance(Attributes.MAX_HEALTH), 26);
        safeSetBaseValue(attributes.getInstance(Attributes.FOLLOW_RANGE), 40.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.MOVEMENT_SPEED), 1.075F);
        safeSetBaseValue(attributes.getInstance(ForgeMod.SWIM_SPEED.get()), 1.025F);
        safeSetBaseValue(attributes.getInstance(Attributes.ATTACK_DAMAGE), 3.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.ARMOR), 0);
        safeSetBaseValue(attributes.getInstance(Attributes.ARMOR_TOUGHNESS), 0);
        safeSetBaseValue(attributes.getInstance(Attributes.KNOCKBACK_RESISTANCE), 0);
    }

    protected void setMorphedAttributes(AttributeMap attributes) {
        safeMulBaseValue(attributes.getInstance(ForgeMod.ENTITY_REACH.get()), 0.5f);
        safeMulBaseValue(attributes.getInstance(ForgeMod.BLOCK_REACH.get()), 0.5f);
    }

    protected void safeSetBaseValue(@Nullable AttributeInstance instance, double value) {
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }

    protected void safeMulBaseValue(@Nullable AttributeInstance instance, double value) {
        if (instance != null) {
            instance.setBaseValue(instance.getBaseValue() * value);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Morphed")) {
            this.setMorphed(tag.getBoolean("Morphed"));
        }
        if (tag.contains("ReplicationTimes")) {
            this.setReplicationTimes(tag.getInt("ReplicationTimes"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Morphed", isMorphed());
        tag.putInt("ReplicationTimes", getReplicationTimes());
    }

    @Override
    public float getEyeHeightMul() {
        if (this.isMorphed())
            return 0.4F;
        else
            return super.getEyeHeightMul();
    }

    @Override
    public @NotNull EntityDimensions getDimensions(Pose pose) {
        EntityDimensions core = super.getDimensions(pose);
        if (this.isMorphed())
            return EntityDimensions.scalable(core.width - 0.05f, core.height - 1.25f);
        else
            return core;
    }

    @Override
    public UseItemMode getItemUseMode() {
        if (this.isMorphed()) {
            return PuddleForm;
        }
        return super.getItemUseMode();
    }

    @Override
    public boolean tryTransfurTarget(Entity entity) {
        return super.tryTransfurTarget(entity);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (isMorphed() && !wasMorphed) {
            this.setMorphedAttributes(this.getAttributes());
            wasMorphed = true;

            IAbstractChangedEntity.forEitherSafe(this.maybeGetUnderlying()).map(IAbstractChangedEntity::getTransfurVariantInstance).ifPresent(TransfurVariantInstance::refreshAttributes);
        } else if (!isMorphed() && wasMorphed) {
            this.setAttributes(this.getAttributes());
            wasMorphed = false;

            IAbstractChangedEntity.forEitherSafe(this.maybeGetUnderlying()).map(IAbstractChangedEntity::getTransfurVariantInstance).ifPresent(TransfurVariantInstance::refreshAttributes);
        }
    }

    public Color3 getHairColor(int i) {
        return Color3.getColor("#E5E5E5");
    }

    @Override
    public int getTicksRequiredToFreeze() {
        return 700;
    }

    @Override
    public TransfurMode getTransfurMode() {
        if (this.getReplicationTimes() > 0) {
            if (willTransfurTarget) {
                subReplicationTimes(1);
            }
            return TransfurMode.REPLICATION;
        }
        return TransfurMode.ABSORPTION;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        HairStyle Hair = BALD.get();
        if (level.random.nextInt(10) > 5) {
            Hair = HairStyle.SHORT_MESSY.get();
        } else {
            Hair = BALD.get();
        }
        return Hair;
    }

    @Override
    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.MALE.getStyles();
    }

    public Color3 getDripColor() {
        Color3 color = Color3.getColor("#ffffff");
        if (level.random.nextInt(10) > 5) {
            color = Color3.getColor("#ffffff");
        } else {
            color = Color3.getColor("#CFCFCF");
        }
        return color;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#CFCFCF");
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RestrictSunGoal(this) {
            @Override
            public boolean canUse() {
                Level world = DazedLatexEntity.this.level;
                return super.canUse() && world.getGameRules().getBoolean(ChangedAddonGameRules.DO_DAZED_LATEX_BURN);
            }
        });

		/*this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
			}
		});
		this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(5, new FloatGoal(this));*/
    }

    @Override
    public @NotNull MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public double getMyRidingOffset() {
        return super.getMyRidingOffset();
    }

    @Override
    public @NotNull SoundEvent getHurtSound(@NotNull DamageSource ds) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    public @NotNull SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    @Mod.EventBusSubscriber(modid = ChangedAddonMod.MODID)
    public static class WhenTransfuredEntity {
        @SubscribeEvent
        public static void WhenDazedTransfur(ProcessTransfur.TransfurAttackEvent event) {
            LivingEntity target = event.target;
            IAbstractChangedEntity source = event.context.source;
            if (source == null) {
                return;
            }
            if (source.getChangedEntity() instanceof DazedLatexEntity dazedLatexEntity) {
                dazedLatexEntity.willTransfurTarget = ProcessTransfur.willTransfur(target,
                        (float) dazedLatexEntity.getAttributeValue(ChangedAttributes.TRANSFUR_DAMAGE.get()));
            }

        }
    }
}