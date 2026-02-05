package net.foxyas.changedaddon.entity.advanced;

import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BuffDazedLatexEntity extends AbstractDazedEntity {

    public BuffDazedLatexEntity(EntityType<BuffDazedLatexEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        this.setAttributes(this.getAttributes());
        setNoAi(false);
        setPersistenceRequired();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void addLivingEntityToBiomes(SpawnPlacementRegisterEvent event) {
        event.register(ChangedAddonEntities.BUFF_DAZED_LATEX.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BuffDazedLatexEntity::canSpawnNear,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    private static boolean canSpawnNear(EntityType<BuffDazedLatexEntity> entityType, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random) {
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
        AttributeSupplier.Builder builder = ChangedEntity.createLatexAttributes();
        builder.add(ChangedAttributes.TRANSFUR_DAMAGE.get(), 0);
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 24);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        return builder;
    }

    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);

        safeSetBaseValue(attributes.getInstance(ChangedAttributes.TRANSFUR_DAMAGE.get()),3);
        safeSetBaseValue(attributes.getInstance(Attributes.MAX_HEALTH),26);
        safeSetBaseValue(attributes.getInstance(Attributes.FOLLOW_RANGE),40.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.MOVEMENT_SPEED),1.075F);
        safeSetBaseValue(attributes.getInstance(ForgeMod.SWIM_SPEED.get()),1.025F);
        safeSetBaseValue(attributes.getInstance(Attributes.ATTACK_DAMAGE),3.0f);
        safeSetBaseValue(attributes.getInstance(Attributes.ARMOR),0);
        safeSetBaseValue(attributes.getInstance(Attributes.ARMOR_TOUGHNESS),0);
        safeSetBaseValue(attributes.getInstance(Attributes.KNOCKBACK_RESISTANCE),0);
    }
}