package net.foxyas.changedaddon.entity.advanced;

import net.foxyas.changedaddon.entity.api.CustomPatReaction;
import net.foxyas.changedaddon.entity.api.IDynamicPawColor;
import net.foxyas.changedaddon.entity.api.ItemHandlerHolder;
import net.foxyas.changedaddon.entity.defaults.AbstractCanTameChangedEntity;
import net.foxyas.changedaddon.entity.goals.prototype.*;
import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.menu.PrototypeMenu;
import net.foxyas.changedaddon.util.ColorUtil;
import net.foxyas.changedaddon.util.DynamicClipContext;
import net.foxyas.changedaddon.util.FoxyasUtils;
import net.foxyas.changedaddon.variant.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.EyeStyle;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.EntityArmorInvWrapper;
import net.minecraftforge.items.wrapper.EntityHandsInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class PrototypeEntity extends AbstractCanTameChangedEntity implements InventoryCarrier, MenuProvider, CustomPatReaction, IDynamicPawColor, ItemHandlerHolder {
    // Constants
    public static final int MAX_HARVEST_TIMES = 32;

    // Fields
    private final SimpleContainer inventory = new SimpleContainer(9);
    private int harvestsTimes = 0;
    private DepositType depositType = DepositType.BOTH;
    @Nullable
    private BlockPos targetChestPos = null;

    private final CombinedInvWrapper combinedInv;

    // Constructors
    public PrototypeEntity(PlayMessages.SpawnEntity ignoredPacket, Level world) {
        this(ChangedAddonEntities.PROTOTYPE.get(), world);
    }

    public PrototypeEntity(EntityType<PrototypeEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setPersistenceRequired();
        combinedInv = new CombinedInvWrapper(new EntityArmorInvWrapper(this), new EntityHandsInvWrapper(this), new InvWrapper(inventory));
    }

    @Override
    public IItemHandler getItemHandler() {
        return combinedInv;
    }

    @Override
    protected float getEquipmentDropChance(@NotNull EquipmentSlot pSlot) {
        return 2;
    }

    // Static methods
    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = ChangedEntity.createLatexAttributes();
        builder.add(ChangedAttributes.TRANSFUR_DAMAGE.get(), 0f);
        builder.add(Attributes.MOVEMENT_SPEED, 1.05f);
        builder.add(Attributes.MAX_HEALTH, 24);
        builder.add(Attributes.ARMOR, 0);
        builder.add(Attributes.ARMOR_TOUGHNESS, 0);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 0);
        builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder.add(Attributes.FOLLOW_RANGE, 40);
        builder.add(ForgeMod.SWIM_SPEED.get(), 0.95f);
        return builder;
    }

    // Entity overrides
    @Override
    protected void setAttributes(AttributeMap attributes) {}

    @Override
    public void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(50, new FindAndHarvestCropsGoal(this));
        goalSelector.addGoal(15, new TryGrabItemsGoal(this));
        goalSelector.addGoal(10, new FindChestGoal(this));
        goalSelector.addGoal(30, new GotoTargetChestGoal(this));
        goalSelector.addGoal(30, new PlantSeedsGoal(this));
        goalSelector.addGoal(30, new ApplyBonemealGoal(this));
        goalSelector.addGoal(50, new PruningOrangeLeavesGoal(this));
    }

    @Override
    public void WhenPattedReaction(Player patter, InteractionHand hand) {
        CustomPatReaction.super.WhenPattedReaction(patter, hand);
        if(patter.level.isClientSide) return;

        if (!isTame()) {
            tame(patter);
            return;
        }

        InteractionResult interactionresult = super.mobInteract(patter, hand);
        if((interactionresult.consumesAction() && !isBaby()) || !isOwnedBy(patter)) return;

        boolean shouldFollow = !isFollowingOwner();
        setFollowOwner(shouldFollow);

        patter.displayClientMessage(new TranslatableComponent(shouldFollow ? "text.changed.tamed.follow" : "text.changed.tamed.wander", getDisplayName()), false);
        jumping = false;
        navigation.stop();
        setTarget(null);
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Inventory", inventory.createTag());
        tag.putInt("harvestDone", harvestsTimes);
        tag.putString("DepositType", depositType.toString());

        if (targetChestPos != null) {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("targetX", targetChestPos.getX());
            nbt.putInt("targetY", targetChestPos.getY());
            nbt.putInt("targetZ", targetChestPos.getZ());
            tag.put("TargetChestPos", nbt);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        inventory.fromTag(tag.getList("Inventory", 10));
        if (tag.contains("harvestDone")) {
            harvestsTimes = tag.getInt("harvestsTimes");
        }
        if (tag.contains("DepositeType")) {
            depositType = DepositType.valueOf(tag.getString("DepositeType").toUpperCase());
        } else if(tag.contains("DepositType")) {
            depositType = DepositType.valueOf(tag.getString("DepositType"));
        }

        if (tag.contains("TargetChestPos")) {
            CompoundTag nbt = tag.getCompound("TargetChestPos");
            int x = nbt.getInt("targetX");
            int y = nbt.getInt("targetY");
            int z = nbt.getInt("targetZ");
            targetChestPos = new BlockPos(x, y, z);
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected boolean targetSelectorTest(LivingEntity livingEntity) {
        return false;
    }

    @Override
    public boolean tryTransfurTarget(Entity entity) {
        return false;
    }

    @Override
    public boolean tryAbsorbTarget(LivingEntity target, IAbstractChangedEntity source, float amount, @Nullable List<TransfurVariant<?>> possibleMobFusions) {
        return false;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.NONE;
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        Color3 firstColor = Color3.getColor("#AEBBF7");
        Color3 secondColor = Color3.getColor("#71FFFF");
        return ColorUtil.lerpTFColor(firstColor, secondColor, getUnderlyingPlayer());
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData ret = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);

        getBasicPlayerInfo().setEyeStyle(EyeStyle.TALL);
        getBasicPlayerInfo().setRightIrisColor(Color3.getColor("#59c5ff"));
        getBasicPlayerInfo().setLeftIrisColor(Color3.getColor("#59c5ff"));
        return ret;
    }

    @Override
    public @NotNull InteractionResult interactAt(@NotNull Player player, @NotNull Vec3 vec, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) {
            if (!getLevel().isClientSide) {
                depositType = depositType.nextDepositType();
            }
            player.displayClientMessage(new TranslatableComponent("entity.changed_addon.prototype.deposit_type.switch", depositType.getFormatedName()), true);
        } else {
            if (!getLevel().isClientSide) {
                NetworkHooks.openGui((ServerPlayer) player, this, buf -> buf.writeVarInt(getId()));
            }
        }

        if (isTame()) {
            if (isTameItem(itemstack) && getHealth() < getMaxHealth()) {
                itemstack.shrink(1);
                heal(2.0F);
                if (level instanceof ServerLevel _level) {
                    _level.sendParticles(ParticleTypes.HEART, (this.getX()), (this.getY() + 1), (this.getZ()), 7, 0.3, 0.3, 0.3, 1); //Spawn Heal Particles
                }
                this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
                return InteractionResult.SUCCESS;
            }
        }

        player.swing(hand);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (isInventoryFull((itemStacks -> itemStacks.stream().filter(this::canTakeItem).count() >= 4)) && targetChestPos != null && blockPosition().closerThan(targetChestPos, 2.0)) {
            if (getLevel() instanceof ServerLevel serverLevel) {
                depositToChest(serverLevel, targetChestPos);
            }
        }

        if (tickCount % 120 == 0) {
            if (harvestsTimes >= MAX_HARVEST_TIMES) {
                harvestsTimes = 0;
            }
        }
    }

    @Override
    public boolean isTameItem(ItemStack stack) {
        return stack.is(Tags.Items.INGOTS_IRON);
    }

    @Override
    protected void dropAllDeathLoot(@NotNull DamageSource pDamageSource) {
        super.dropAllDeathLoot(pDamageSource);

        if(!inventory.isEmpty()) dropInventoryItems();
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();

        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if(equipmentSlot.getType() != EquipmentSlot.Type.HAND) continue;

            ItemStack stack = getItemBySlot(equipmentSlot);
            if(stack.isEmpty()) continue;

            ItemEntity itemEntity = new ItemEntity(level, getX(), getY() + 0.5, getZ(), stack.copy());
            itemEntity.setDeltaMovement(
                    (level.random.nextDouble() - 0.5) * 0.2,
                    0.2,
                    (level.random.nextDouble() - 0.5) * 0.2
            );
            level.addFreshEntity(itemEntity);
            setItemSlot(equipmentSlot, ItemStack.EMPTY);
        }
    }

    // Inventory related methods
    @Override
    public boolean canTakeItem(@NotNull ItemStack pItemstack) {
        if(pItemstack.isEmpty()) return false;
        if (pickAbleItems().contains(pItemstack.getItem())
                || (pItemstack.is(Tags.Items.CROPS) || (pItemstack.is(FORGE_FRUITS) || pItemstack.is(Tags.Items.SHEARS) || pItemstack.is(Tags.Items.SEEDS)))) {
            return true;
        }

        return super.canTakeItem(pItemstack);
    }

    @Override
    public boolean canPickUpLoot() {
        return true;
    }

    @Override
    public boolean wantsToPickUp(@NotNull ItemStack pStack) {
        if (hasSpaceInInvOrHands()) {
            if (pStack.is(Tags.Items.CROPS) || (pStack.is(FORGE_FRUITS) || pStack.is(Tags.Items.SEEDS) || pStack.is(Tags.Items.SHEARS) || pickAbleItems().contains(pStack.getItem()))) {
                return true;
            }
        }
        return super.wantsToPickUp(pStack);
    }

    @Override
    public boolean canHoldItem(@NotNull ItemStack pStack) {
        return hasSpaceInInvOrHands() && (pStack.is(Tags.Items.CROPS) || (pStack.is(FORGE_FRUITS) || pStack.is(Tags.Items.SEEDS) || pStack.is(Tags.Items.SHEARS) || pickAbleItems().contains(pStack.getItem())));
    }

    @Override
    protected void pickUpItem(@NotNull ItemEntity pItemEntity) {
        ItemStack pStack = pItemEntity.getItem();
        if (pStack.is(Tags.Items.CROPS) || (pStack.is(FORGE_FRUITS) || pStack.is(Tags.Items.SEEDS) || pStack.is(Tags.Items.SHEARS) || pickAbleItems().contains(pStack.getItem()))) {
            addToInventory(pStack);
            return;
        }
        super.pickUpItem(pItemEntity);
    }

    @Override
    public @NotNull SimpleContainer getInventory() {
        return inventory;
    }

    // MenuProvider implementation
    @Override
    public @NotNull Component getDisplayName() {
        return getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new PrototypeMenu(id, playerInventory, this);
    }

    // Inventory management
    public boolean isInventoryFull() {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).isEmpty()) return false;
        }
        return true;
    }

    public boolean hasSpaceInInvOrHands() {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).isEmpty()) return true;
        }

        return getMainHandItem().isEmpty() || getOffhandItem().isEmpty();
    }

    public boolean isInventoryFull(Predicate<NonNullList<ItemStack>> listPredicate) {
        NonNullList<ItemStack> itemStacks = getInventoryItems();
        return listPredicate.test(itemStacks);
    }

    public NonNullList<ItemStack> getInventoryItems() {
        NonNullList<ItemStack> itemStacks = NonNullList.create();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            itemStacks.add(inventory.getItem(i));
        }
        return itemStacks;
    }

    public void addToInventory(ItemStack stack) {
        for (int i = 0; i < getInventory().getContainerSize(); i++) {
            ItemStack slot = getInventory().getItem(i);
            if (slot.isEmpty()) {
                getInventory().setItem(i, stack.copy());
                stack.setCount(0);
                return;
            } else if (ItemStack.isSameItemSameTags(slot, stack)) {
                int canAdd = Math.min(slot.getMaxStackSize() - slot.getCount(), stack.getCount());
                slot.grow(canAdd);
                stack.shrink(canAdd);
                if (stack.isEmpty()) return;
            }
        }
        if (isInventoryFull()) {
            for (EquipmentSlot equipmentSlot : Arrays.stream(EquipmentSlot.values()).filter((equipmentSlot -> equipmentSlot.getType() == EquipmentSlot.Type.HAND)).toList()) {
                ItemStack itemStack = getItemBySlot(equipmentSlot);
                if (itemStack.isEmpty()) {
                    setItemSlot(equipmentSlot, stack);
                } else if (ItemStack.isSameItemSameTags(itemStack, stack)) {
                    itemStack.grow(1);
                    stack.shrink(1);
                }
            }
        }
    }

    private void dropInventoryItems() {
        if (level.isClientSide) return;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level, getX(), getY() + 0.5, getZ(), stack.copy());
                itemEntity.setDeltaMovement(
                        (level.random.nextDouble() - 0.5) * 0.2,
                        0.2,
                        (level.random.nextDouble() - 0.5) * 0.2
                );
                level.addFreshEntity(itemEntity);
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }
        inventory.setChanged();
    }

    // Crop and chest related methods
    public BlockPos tryFindNearbyChest(Level level, BlockPos center, int range) {
        List<ItemStack> carriedItems = new ArrayList<>();
        for (int i = 0; i < getInventory().getContainerSize(); i++) {
            ItemStack stack = getInventory().getItem(i);
            if (!stack.isEmpty()) carriedItems.add(stack);
        }

        BlockPos closestChest = null, bestChest = null;
        double closestDist = Double.MAX_VALUE, bestDist = closestDist;
        double dist;

        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-range, -range, -range), center.offset(range, range, range))) {
            BlockEntity be = level.getBlockEntity(pos);
            if(!(be instanceof ChestBlockEntity chest)) continue;

            dist = pos.distSqr(center);
            if(dist >= bestDist || isChestFull(chest)) continue;

            if(dist < closestDist) {
                closestDist = dist;
                closestChest = pos.immutable();
            }

            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack chestItem = chest.getItem(slot);
                if(chestItem.isEmpty()) continue;

                for (ItemStack carried : carriedItems) {
                    if(!ItemStack.isSameItemSameTags(carried, chestItem)) continue;

                    bestDist = dist;
                    bestChest = pos.immutable();
                    break;
                }
                if(pos.equals(bestChest)) break;
            }
        }

        return bestChest != null ? bestChest : closestChest;
    }

    public BlockPos findNearbyCrop(Level level, BlockPos center, int range) {
        BlockPos closestCrop = null;
        double closestDist = Double.MAX_VALUE;
        double dist;

        for (BlockPos pos : FoxyasUtils.betweenClosedStreamSphere(center, range, range).toList()) {
            BlockState state = level.getBlockState(pos);
            if(!(state.getBlock() instanceof CropBlock crop) || !crop.isMaxAge(state)) continue;

            dist = pos.distSqr(center);
            if(dist >= closestDist) continue;

            closestDist = dist;
            closestCrop = pos.immutable();
        }
        return closestCrop;
    }

    @Nullable
    public BlockPos findNearbyOrangeLeaves(BlockPos center, int range, Vec3 eyePos) {
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        double dist;

        // Evite .toList() para não alocar tudo; itere o stream diretamente
        for (BlockPos pos : (Iterable<BlockPos>) FoxyasUtils.betweenClosedStreamSphere(center, range, range)::iterator) {
            BlockState state = level.getBlockState(pos);
            if (!state.is(ChangedBlocks.ORANGE_TREE_LEAVES.get())) continue;

            // Distância do olho ao centro do bloco (mais precisa)
            dist = eyePos.distanceToSqr(Vec3.atCenterOf(pos));
            if (dist >= bestDist) continue;

            BlockHitResult hit = level.clip(eyeContext(pos));

            if (hit.getType() == HitResult.Type.BLOCK && hit.getBlockPos().equals(pos)) {
                bestDist = dist;
                best = pos.immutable();
            }
        }
        return best;
    }


    private @NotNull ClipContext eyeContext(BlockPos pos) {
        return new DynamicClipContext(
                getEyePosition(),
                Vec3.atCenterOf(pos),
                DynamicClipContext.IGNORE_TRANSLUCENT,
                ClipContext.Fluid.ANY::canPick,
                CollisionContext.of(this));
    }

    public void harvestCrop(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if(!(state.getBlock() instanceof CropBlock crop) || !crop.isMaxAge(state)) return;

        // Drop items naturally (simulate player breaking)
        ItemStack tool = getMainHandItem();
        Block.dropResources(state, level, pos, level.getBlockEntity(pos), this, tool);

        // Replant at age 0
        level.setBlock(pos, crop.getStateForAge(0), 3);
        level.playSound(null, pos, state.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1, 1);
        addHarvestsTime();
    }

    private void depositToChest(ServerLevel level, BlockPos chestPos) {
        BlockState state = level.getBlockState(chestPos);
        BlockEntity be = level.getBlockEntity(chestPos);

        if(!(be instanceof ChestBlockEntity chest)) {
            setTargetChestPos(null);
            return;
        }

        chest.startOpen(FakePlayerFactory.getMinecraft(level));

        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if(equipmentSlot.getType() != EquipmentSlot.Type.HAND) continue;

            ItemStack stack = getItemBySlot(equipmentSlot);
            if (!isChestFull(chest)) {
                if (!stack.isEmpty() && depositType.test(stack)) {
                    lookAt(EntityAnchorArgument.Anchor.FEET, new Vec3(chestPos.getX(), chestPos.getY() - 1, chestPos.getZ()));
                    swing(isLeftHanded() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                    ItemStack remaining = HopperBlockEntity.addItem(null, chest, stack, null);
                    chest.setChanged();
                    setItemSlot(equipmentSlot, remaining);
                    getInventory().setChanged();
                    chest.triggerEvent(1, 1);
                    if (state.getBlock() instanceof ChestBlock) {
                        level.playSound(null, chestPos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.25f, 1);
                        setHarvestsTimes(0);
                    }
                }
            } else {
                targetChestPos = tryFindNearbyChest(getLevel(), blockPosition(), 8);
            }
        }

        for (int i = 0; i < getInventory().getContainerSize(); i++) {
            ItemStack stack = getInventory().getItem(i);
            if (!isChestFull(chest)) {
                if (!stack.isEmpty() && (depositType.test(stack))) {
                    // Make entity look at a target position
                    getLookControl().setLookAt(
                            chestPos.getX(), chestPos.getY(), chestPos.getZ(),
                            30.0F, // yaw change speed (degrees per tick)
                            30.0F  // pitch change speed
                    );

                    swing(isLeftHanded() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                    ItemStack remaining = HopperBlockEntity.addItem(null, chest, stack, null);
                    chest.setChanged();
                    getInventory().setItem(i, remaining);
                    getInventory().setChanged();
                    if ((i == 0 || i == inventory.getContainerSize()) && state.getBlock() instanceof ChestBlock) {
                        level.playSound(null, chestPos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.25f, 1);
                        setHarvestsTimes(0);
                    }
                    setTargetChestPos(null);
                }
            } else {
                targetChestPos = tryFindNearbyChest(getLevel(), blockPosition(), 8);
            }
        }
    }

    private boolean isChestFull(ChestBlockEntity chest) {
        for (int i = 0; i < chest.getContainerSize(); i++) {
            ItemStack stack = chest.getItem(i);
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    // Getters and setters
    public List<Item> pickAbleItems() {
        return List.of(Items.BONE_MEAL, Items.SHEARS);
    }

    public DepositType getDepositType() {
        return depositType;
    }

    public void setDepositType(DepositType depositType) {
        this.depositType = depositType;
    }

    public int getHarvestsTimes() {
        return this.harvestsTimes;
    }

    public void setHarvestsTimes(int harvestsTimes) {
        this.harvestsTimes = harvestsTimes;
    }

    public void addHarvestsTime() {
        this.harvestsTimes++;
    }

    public @Nullable BlockPos getTargetChestPos() {
        return targetChestPos;
    }

    public void setTargetChestPos(@Nullable BlockPos targetChestPos) {
        this.targetChestPos = targetChestPos;
    }

    public boolean willDepositSeeds() {
        return depositType == DepositType.SEEDS || depositType == DepositType.BOTH;
    }

    @Override
    public Color getPawBeansColor() {
        return Color.CYAN;
    }

    private static final TagKey<Item> FORGE_FRUITS = ItemTags.create(new ResourceLocation("forge", "fruits"));

    // Enums
    public enum DepositType {
        SEEDS(Tags.Items.SEEDS),
        CROPS(FORGE_FRUITS, Tags.Items.CROPS),
        BOTH(FORGE_FRUITS, Tags.Items.CROPS, Tags.Items.SEEDS);

        final List<TagKey<Item>> tagKeys;

        DepositType(TagKey<Item> crops, TagKey<Item> seeds) {
            this.tagKeys = List.of(crops, seeds);
        }

        DepositType(TagKey<Item> typeTag) {
            this.tagKeys = List.of(typeTag);
        }

        DepositType(TagKey<Item> fruits, TagKey<Item> crops, TagKey<Item> seeds) {
            this.tagKeys = List.of(fruits, crops, seeds);
        }

        public List<TagKey<Item>> getTagKeys() {
            return tagKeys;
        }

        public String getFormatedName() {
            String normalName = name();
            String lowerCaseName = name().substring(1).toLowerCase();
            return normalName.toUpperCase().charAt(0) + lowerCaseName;
        }

        public boolean test(ItemStack stack) {
            return tagKeys.stream().anyMatch(stack::is);
        }

        public DepositType nextDepositType() {
            int next = ordinal() + 1;
            DepositType[] types = values();
            return next >= types.length ? types[0] : types[next];
        }
    }

    @Mod.EventBusSubscriber
    public static class EventHandle {

        @SubscribeEvent
        public static void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
            if (event.getEntity() instanceof PrototypeEntity) {
                event.setCanceled(true);
                return;
            }

            if (event.getEntity() instanceof Player player) {
                TransfurVariantInstance<?> transfurVariant = ProcessTransfur.getPlayerTransfurVariant(player);
                if (transfurVariant != null && transfurVariant.is(ChangedAddonTransfurVariants.PROTOTYPE)) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
