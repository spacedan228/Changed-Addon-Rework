package net.foxyas.changedaddon.entity.mobs;

import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.world.inventory.FoxyasGuiMenu;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.EntityArmorInvWrapper;
import net.minecraftforge.items.wrapper.EntityHandsInvWrapper;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FoxyasEntity extends Monster {

    private final ItemStackHandler inventory = new ItemStackHandler(9);
    private final CombinedInvWrapper combined = new CombinedInvWrapper(inventory, new EntityHandsInvWrapper(this), new EntityArmorInvWrapper(this));

    public FoxyasEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ChangedAddonEntities.FOXYAS.get(), world);
    }

    public FoxyasEntity(EntityType<FoxyasEntity> type, Level world) {
        super(type, world);
        xpReward = 10;
        setNoAi(false);
        setPersistenceRequired();
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
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.getNavigation().getNodeEvaluator().setCanOpenDoors(true);
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, false) {
            @Override
            protected double getAttackReachSqr(@NotNull LivingEntity entity) {
                return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
            }
        });
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, ServerPlayer.class, (float) 6));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, (float) 6));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1) {
            @Override
            public boolean canUse() {
                return super.canUse() && !isInWater();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !isInWater();
            }
        });
        this.goalSelector.addGoal(7, new RandomSwimmingGoal(this, 1, 40) {
            @Override
            public boolean canUse() {
                return super.canUse() && isInWater();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isInWater();
            }
        });
        this.targetSelector.addGoal(8, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(10, new FloatGoal(this));
        this.goalSelector.addGoal(11, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(12, new OpenDoorGoal(this, false));
    }

    @Override
    public @NotNull MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
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
            Advancement _adv = player.server.getAdvancements().getAdvancement(new ResourceLocation("changed_addon:foxyas_advancement"));
            AdvancementProgress _ap = player.getAdvancements().getOrStartProgress(_adv);
            if (!_ap.isDone()) {
                for (String s : _ap.getRemainingCriteria()) player.getAdvancements().award(_adv, s);
            }
        }
    }

    public void doTrade(){
        ItemStack oranges = combined.getStackInSlot(0);
        if(oranges.getCount() < 2) return;

        ItemStack bottles = combined.getStackInSlot(1);
        if(bottles.getCount() < 1) return;

        ItemStack tmp = oranges.copy();
        tmp.setCount(oranges.getCount() - 2);
        combined.setStackInSlot(0, tmp);

        tmp = bottles.copy();
        tmp.setCount(bottles.getCount() - 1);
        combined.setStackInSlot(1, tmp);

        tmp = new ItemStack(ChangedAddonItems.ORANGE_JUICE.get());
        tmp = combined.insertItem(2, tmp, false);
        if(!tmp.isEmpty()) Block.popResource(level, blockPosition(), tmp);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if (this.isAlive() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side == null)
            return LazyOptional.of(() -> combined).cast();
        return super.getCapability(capability, side);
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        for (int i = 0; i < inventory.getSlots(); ++i) {
            ItemStack itemstack = inventory.getStackInSlot(i);
            if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack)) {
                this.spawnAtLocation(itemstack);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("InventoryCustom", inventory.serializeNBT());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        Tag inventoryCustom = compound.get("InventoryCustom");
        if (inventoryCustom instanceof CompoundTag inventoryTag)
            inventory.deserializeNBT(inventoryTag);
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player sourceentity, @NotNull InteractionHand hand) {
        InteractionResult retval = InteractionResult.sidedSuccess(this.level.isClientSide());
        if (sourceentity instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openGui(serverPlayer, new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return new TextComponent("Foxyas");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
                    return new FoxyasGuiMenu(id, inventory, FoxyasEntity.this);
                }
            }, buf -> buf.writeVarInt(getId()));
        }
        super.mobInteract(sourceentity, hand);

        getNavigation().moveTo(sourceentity.getX(), sourceentity.getY(), sourceentity.getZ(), 1);
        return retval;
    }

    @Override
    public void baseTick() {
        super.baseTick();

        final Vec3 _center = new Vec3(getX(), getY(), getZ());
        List<Entity> _entfound = level.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(20 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).collect(Collectors.toList());
        for (Entity entityiterator : _entfound) {
            if (entityiterator.getPersistentData().getBoolean("FoxyasGui_open")) {
                getNavigation().moveTo((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 0.8);
                break;
            }
        }

        if (isInWater() && isUnderWater() && !isOnGround()) {
            setPose(Pose.SWIMMING);
        } else if (isInWater() && !isUnderWater() && isOnGround()) {
            setPose(Pose.STANDING);
        } else if (!isInWater() && !isUnderWater() && isOnGround()) {
            setPose(Pose.STANDING);
        }
        if (isInWater()) {
            LivingEntity target = getTarget();
            if(target == null) return;

            float deltaX = (float) (target.getX() - getX());
            float deltaY = (float) (target.getY() - getY());
            float deltaZ = (float) (target.getZ() - getZ());
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

            if (distance > 0) {
                float speed = 0.04f;
                float motionX = deltaX / distance * speed;
                float motionY = deltaY / distance * speed;
                float motionZ = deltaZ / distance * speed;
                lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(target.getX(), target.getY(), target.getZ()));
                setDeltaMovement(getDeltaMovement().add(motionX, motionY, motionZ));
            }
        }
    }
}
