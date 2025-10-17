package net.foxyas.changedaddon.entity.advanced;

import net.foxyas.changedaddon.entity.defaults.AbstractTraderChangedEntityWithInventory;
import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.menu.CustomMerchantOffer;
import net.foxyas.changedaddon.menu.CustomMerchantOffers;
import net.foxyas.changedaddon.menu.FoxyasMenu;
import net.foxyas.changedaddon.util.CustomMerchantUtil;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class LatexSnowFoxFoxyasEntity extends AbstractTraderChangedEntityWithInventory {

    private static final List<Function<LatexSnowFoxFoxyasEntity, CustomMerchantOffer>> buyOffers = List.of(
            (entity) ->
                    new CustomMerchantOffer(Ingredient.of(new ItemStack(ChangedItems.ORANGE.get(), 3)),
                            Ingredient.of(new ItemStack(Items.GLASS_BOTTLE)), new ItemStack(ChangedAddonItems.ORANGE_JUICE.get()), 16)
    );

    private static final List<Function<LatexSnowFoxFoxyasEntity, CustomMerchantOffer>> sellOffers = List.of(
    );

    public LatexSnowFoxFoxyasEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ChangedAddonEntities.LATEX_SNOW_FOX_FOXYAS.get(), world);
    }

    public LatexSnowFoxFoxyasEntity(EntityType<LatexSnowFoxFoxyasEntity> type, Level world) {
        super(type, world, 27);
        xpReward = 10;
        setNoAi(false);
        setPersistenceRequired();
    }

    protected CustomMerchantOffers makeOffers() {
        return CustomMerchantUtil.makeOffers(this, buyOffers, 2, sellOffers, 2);
    }

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 24);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 5);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        return builder;
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.NONE;
    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public @NotNull MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public @NotNull SoundEvent getHurtSound(@NotNull DamageSource ds) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    public @NotNull SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    @Override
    public void die(@NotNull DamageSource source) {
        super.die(source);

        if (source.getEntity() instanceof ServerPlayer player) {
            Advancement _adv = player.server.getAdvancements().getAdvancement(ResourceLocation.parse("changed_addon:foxyas_advancement"));
            AdvancementProgress _ap = player.getAdvancements().getOrStartProgress(_adv);
            if (!_ap.isDone()) {
                for (String s : _ap.getRemainingCriteria()) player.getAdvancements().award(_adv, s);
            }
        }
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (getOffers().isEmpty() || player.isShiftKeyDown()) {
            if(level.isClientSide) return InteractionResult.SUCCESS;
            NetworkHooks.openGui((ServerPlayer) player, getMenuProvider(), buf -> buf.writeVarInt(getId()));
            return InteractionResult.CONSUME;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inv, @NotNull Player player) {
        if (player.isShiftKeyDown()) {
            return new FoxyasMenu(containerId, inv, this);
        }
        return super.createMenu(containerId, inv, player);
    }

    @Override
    public void baseTick() {
        super.baseTick();
    }
}
