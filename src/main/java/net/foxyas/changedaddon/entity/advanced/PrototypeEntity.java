package net.foxyas.changedaddon.entity.advanced;

import net.foxyas.changedaddon.entity.defaults.AbstractCanTameChangedEntity;
import net.foxyas.changedaddon.entity.goals.prototype.*;
import net.foxyas.changedaddon.entity.interfaces.CustomPatReaction;
import net.foxyas.changedaddon.entity.interfaces.IDynamicPawColor;
import net.foxyas.changedaddon.init.ChangedAddonEntities;
import net.foxyas.changedaddon.menu.PrototypeMenu;
import net.foxyas.changedaddon.util.ColorUtil;
import net.foxyas.changedaddon.util.DynamicClipContext;
import net.foxyas.changedaddon.util.FoxyasUtils;
import net.foxyas.changedaddon.variants.ChangedAddonTransfurVariants;
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
import net.minecraft.tags.Tag;
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
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.EntityArmorInvWrapper;
import net.minecraftforge.items.wrapper.EntityHandsInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public class PrototypeEntity extends AbstractCanTameChangedEntity implements InventoryCarrier, MenuProvider, CustomPatReaction, IDynamicPawColor {
    // Constants
    public static final int MAX_HARVEST_TIMES = 32;
    // Fields
    private final SimpleContainer inventory = new SimpleContainer(9);
    private final MenuProvider menuProvider = new MenuProvider() {
        @Override
        public @NotNull Component getDisplayName() {
            return PrototypeEntity.this.getDisplayName();
        }

        @Override
        public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
            return PrototypeEntity.this.createMenu(i, inventory, player);
        }
    };
    private int harvestsTimes = 0;
    private DepositeType depositeType = DepositeType.BOTH;
    @Nullable
    private BlockPos targetChestPos = null;

    private final IItemHandlerModifiable combinedInv;

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

    public IItemHandlerModifiable getCombinedInv() {
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
        builder = builder.add(ChangedAttributes.TRANSFUR_DAMAGE.get(), 0f);
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 24);
        builder = builder.add(Attributes.ARMOR, 0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
        builder = builder.add(Attributes.FOLLOW_RANGE, 16);
        return builder;
    }

    // Entity overrides
    @Override
    protected void setAttributes(AttributeMap attributes) {
        Objects.requireNonNull(attributes.getInstance(ChangedAttributes.TRANSFUR_DAMAGE.get())).setBaseValue(0);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(24);
        attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(40.0f);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.05f);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.95f);
        attributes.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(3.0f);
        attributes.getInstance(Attributes.ARMOR).setBaseValue(0);
        attributes.getInstance(Attributes.ARMOR_TOUGHNESS).setBaseValue(0);
        attributes.getInstance(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(50, new FindAndHarvestCropsGoal(this));
        this.goalSelector.addGoal(15, new TryGrabItemsGoal(this));
        this.goalSelector.addGoal(10, new FindChestGoal(this));
        this.goalSelector.addGoal(30, new GotoTargetChestGoal(this));
        this.goalSelector.addGoal(30, new PlantSeedsGoal(this));
        this.goalSelector.addGoal(30, new ApplyBonemealGoal(this));
        this.goalSelector.addGoal(50, new PruningOrangeLeavesGoal(this));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Inventory", inventory.createTag());
        tag.putInt("harvestDone", this.harvestsTimes);
        tag.putString("DepositeType", this.depositeType.toString().toLowerCase(Locale.ROOT));

        if (targetChestPos != null) {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("targetX", targetChestPos.getX());
            nbt.putInt("targetY", targetChestPos.getY());
            nbt.putInt("targetZ", targetChestPos.getZ());
            tag.put("TargetChestPos", nbt);
        }
    }

    @Override
    public void WhenPattedReaction(Player patter, InteractionHand hand) {
        CustomPatReaction.super.WhenPattedReaction(patter, hand);
        if (!patter.level.isClientSide()) {
            if (!this.isTame()) {
                this.tame(patter);
            } else {
                InteractionResult interactionresult = super.mobInteract(patter, hand);
                if ((!interactionresult.consumesAction() || this.isBaby()) && this.isOwnedBy(patter)) {
                    boolean shouldFollow = !this.isFollowingOwner();
                    this.setFollowOwner(shouldFollow);

                    patter.displayClientMessage(new TranslatableComponent(shouldFollow ? "text.changed.tamed.follow" : "text.changed.tamed.wander", this.getDisplayName()), false);
                    this.jumping = false;
                    this.navigation.stop();
                    this.setTarget(null);
                }
            }
        }
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        inventory.fromTag(tag.getList("Inventory", 10));
        if (tag.contains("harvestDone")) {
            this.harvestsTimes = tag.getInt("harvestsTimes");
        }
        if (tag.contains("DepositeType")) {
            this.depositeType = DepositeType.valueOf(tag.getString("DepositeType").toUpperCase());
        }

        if (tag.contains("TargetChestPos")) {
            CompoundTag nbt = tag.getCompound("TargetChestPos");
            int x = nbt.getInt("targetX");
            int y = nbt.getInt("targetY");
            int z = nbt.getInt("targetZ");
            this.targetChestPos = new BlockPos(x, y, z);
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
        return ColorUtil.lerpTFColor(firstColor, secondColor, this.getUnderlyingPlayer());
    }


    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData ret = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);

        this.getBasicPlayerInfo().setEyeStyle(EyeStyle.TALL);
        this.getBasicPlayerInfo().setRightIrisColor(Color3.getColor("#59c5ff"));
        this.getBasicPlayerInfo().setLeftIrisColor(Color3.getColor("#59c5ff"));
        return ret;
    }

    @Override
    public @NotNull InteractionResult interactAt(@NotNull Player player, @NotNull Vec3 vec, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) {
            if (!getLevel().isClientSide) {
                this.depositeType = depositeType.switchDepositeType();
            }
            player.displayClientMessage(new TranslatableComponent("entity.changed_addon.prototype.deposite_type.switch", depositeType.getFormatedName()), true);
        } else {
            if (!getLevel().isClientSide) {
                NetworkHooks.openGui((ServerPlayer) player, getMenuProvider(), buf -> buf.writeVarInt(getId()));
            }
        }

        if (this.isTame()) {
            if (this.isTame() && this.isTameItem(itemstack) && this.getHealth() < this.getMaxHealth()) {
                itemstack.shrink(1);
                this.heal(2.0F);
                if (this.level instanceof ServerLevel _level) {
                    _level.sendParticles(ParticleTypes.HEART, (this.getX()), (this.getY() + 1), (this.getZ()), 7, 0.3, 0.3, 0.3, 1); //Spawn Heal Particles
                }
                this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
                return InteractionResult.SUCCESS;
            }
        }

        player.swing(hand);
        if (!getLevel().isClientSide) {
            return InteractionResult.CONSUME;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (this.isInventoryFull((itemStacks -> itemStacks.stream().filter(this::canTakeItem).count() >= 4)) && targetChestPos != null && this.blockPosition().closerThan(targetChestPos, 2.0)) {
            if (this.getLevel() instanceof ServerLevel serverLevel) {
                depositToChest(serverLevel, targetChestPos);
            }
        }

        if (tickCount % 120 == 0) {
            if (this.harvestsTimes >= MAX_HARVEST_TIMES) {
                this.harvestsTimes = 0;
            }
        }
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        super.die(pDamageSource);
    }

    @Override
    public boolean isTameItem(ItemStack stack) {
        return stack.is(Tags.Items.INGOTS_IRON);
    }

    @Override
    protected void dropAllDeathLoot(@NotNull DamageSource pDamageSource) {
        super.dropAllDeathLoot(pDamageSource);

        if (!this.inventory.isEmpty()) {
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                dropInventoryItems();
            }
        }
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();

        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (equipmentSlot.getType() == EquipmentSlot.Type.HAND) {
                ItemStack stack = this.getItemBySlot(equipmentSlot);
                if (!stack.isEmpty()) {
                    ItemEntity itemEntity = new ItemEntity(level, this.getX(), this.getY() + 0.5, this.getZ(), stack.copy());
                    itemEntity.setDeltaMovement(
                            (level.random.nextDouble() - 0.5) * 0.2,
                            0.2,
                            (level.random.nextDouble() - 0.5) * 0.2
                    );
                    level.addFreshEntity(itemEntity);
                    this.setItemSlot(equipmentSlot, ItemStack.EMPTY);
                }
            }
        }
    }

    // Inventory related methods
    @Override
    public boolean canTakeItem(@NotNull ItemStack pItemstack) {
        if (this.pickAbleItems().contains(pItemstack.getItem())
                || (pItemstack.is(Tags.Items.CROPS) || (pItemstack.is(tag("fruits")) || pItemstack.is(Tags.Items.SHEARS) || pItemstack.is(Tags.Items.SEEDS)))) {
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
        if (!isInventoryAndHandsFull()) {
            if (pStack.is(Tags.Items.CROPS) || (pStack.is(tag("fruits")) || pStack.is(Tags.Items.SEEDS) || pStack.is(Tags.Items.SHEARS) || pickAbleItems().contains(pStack.getItem()))) {
                return true;
            }
        }
        return super.wantsToPickUp(pStack);
    }

    @Override
    public boolean canHoldItem(@NotNull ItemStack pStack) {
        return !isInventoryAndHandsFull() && (pStack.is(Tags.Items.CROPS) || (pStack.is(tag("fruits")) || pStack.is(Tags.Items.SEEDS) || pStack.is(Tags.Items.SHEARS) || pickAbleItems().contains(pStack.getItem())));
    }

    @Override
    protected void pickUpItem(@NotNull ItemEntity pItemEntity) {
        ItemStack pStack = pItemEntity.getItem();
        if (pStack.is(Tags.Items.CROPS) || (pStack.is(tag("fruits")) || pStack.is(Tags.Items.SEEDS) || pStack.is(Tags.Items.SHEARS) || pickAbleItems().contains(pStack.getItem()))) {
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
        return this.getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new PrototypeMenu(id, playerInventory, this);
    }

    public MenuProvider getMenuProvider() {
        return menuProvider;
    }

    // Inventory management
    public boolean isInventoryFull() {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isInventoryAndHandsFull() {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).isEmpty()) {
                return false;
            }
        }

        if (this.getMainHandItem().isEmpty()) {
            return false;
        } else return !this.getOffhandItem().isEmpty();
    }

    public boolean isInventoryFull(Predicate<NonNullList<ItemStack>> listPredicate) {
        NonNullList<ItemStack> itemStacks = this.getInventoryItems();
        return listPredicate.test(itemStacks);
    }

    public NonNullList<ItemStack> getInventoryItems() {
        NonNullList<ItemStack> itemStacks = NonNullList.create();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            itemStacks.add(inventory.getItem(i));
        }
        return itemStacks;
    }

    public boolean isInventoryEmpty() {
        return inventory.isEmpty();
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
        if (this.isInventoryFull()) {
            for (EquipmentSlot equipmentSlot : Arrays.stream(EquipmentSlot.values()).filter((equipmentSlot -> equipmentSlot.getType() == EquipmentSlot.Type.HAND)).toList()) {
                ItemStack itemStack = this.getItemBySlot(equipmentSlot);
                if (itemStack.isEmpty()) {
                    this.setItemSlot(equipmentSlot, stack);
                } else if (ItemStack.isSameItemSameTags(itemStack, stack)) {
                    itemStack.grow(1);
                    stack.shrink(1);
                }
            }
        }
    }

    private void dropInventoryItems() {
        Level level = this.level;
        if (level.isClientSide) return;

        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level, this.getX(), this.getY() + 0.5, this.getZ(), stack.copy());
                itemEntity.setDeltaMovement(
                        (level.random.nextDouble() - 0.5) * 0.2,
                        0.2,
                        (level.random.nextDouble() - 0.5) * 0.2
                );
                level.addFreshEntity(itemEntity);
                this.inventory.setItem(i, ItemStack.EMPTY);
            }
        }
        this.inventory.setChanged();
    }

    // Crop and chest related methods
    public BlockPos tryFindNearbyChest(Level level, BlockPos center, int range) {
        List<ItemStack> carriedItems = new ArrayList<>();
        for (int i = 0; i < getInventory().getContainerSize(); i++) {
            ItemStack stack = getInventory().getItem(i);
            if (!stack.isEmpty()) carriedItems.add(stack);
        }

        BlockPos closestChest = null;
        double closestDist = Double.MAX_VALUE;

        // First try to find chest containing at least one matching item
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-range, -range, -range), center.offset(range, range, range))) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ChestBlockEntity chest) {
                if (!isChestFull(chest)) {
                    for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                        ItemStack chestItem = chest.getItem(slot);
                        if (!chestItem.isEmpty()) {
                            for (ItemStack carried : carriedItems) {
                                if (ItemStack.isSameItemSameTags(carried, chestItem)) {
                                    double dist = pos.distSqr(center);
                                    if (dist < closestDist) {
                                        closestDist = dist;
                                        closestChest = pos.immutable();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Otherwise return any chest
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-range, -range, -range), center.offset(range, range, range))) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ChestBlockEntity chest && !isChestFull(chest)) {
                double dist = pos.distSqr(center);
                if (dist < closestDist) {
                    closestDist = dist;
                    closestChest = pos.immutable();
                }
            }
        }

        return closestChest;
    }

    public BlockPos findNearbyCrop(Level level, BlockPos center, int range) {
        BlockPos closestCrop = null;
        double closestDist = Double.MAX_VALUE;

        for (BlockPos pos : FoxyasUtils.betweenClosedStreamSphere(center, range, range).toList()) {
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
                double dist = pos.distSqr(center);
                if (dist < closestDist) {
                    closestDist = dist;
                    closestCrop = pos.immutable();
                }
            }
        }
        return closestCrop;
    }

    @Nullable
    public BlockPos findNearbyOrangeLeaves(BlockPos center, int range, Vec3 eyePos, @Nullable Entity viewer) {
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;

        // Evite .toList() para não alocar tudo; itere o stream diretamente
        for (BlockPos pos : (Iterable<BlockPos>) FoxyasUtils.betweenClosedStreamSphere(center, range, range)::iterator) {
            BlockState state = level.getBlockState(pos);
            if (!state.is(ChangedBlocks.ORANGE_TREE_LEAVES.get()))
                continue;

            // Distância do olho ao centro do bloco (mais precisa)
            double dist = eyePos.distanceToSqr(Vec3.atCenterOf(pos));
            if (dist >= bestDist) continue;

            // Raycast: do olho até o centro do bloco
            /*ClipContext ctx = new ClipContext(
                    eyePos,
                    Vec3.atCenterOf(pos),
                    ClipContext.Block.COLLIDER, // considera colisões de bloco reais
                    ClipContext.Fluid.NONE,
                    viewer
            );*/
            BlockHitResult hit = level.clip(eyeContext(pos));

            if (hit.getType() == HitResult.Type.BLOCK &&
                    hit.getBlockPos().equals(pos)) {
                bestDist = dist;
                best = pos.immutable();
            }
        }
        return best;
    }


    private @NotNull ClipContext eyeContext(BlockPos pos) {
        return new DynamicClipContext(
                this.getEyePosition(),
                Vec3.atCenterOf(pos),
                DynamicClipContext.IGNORE_TRANSLUCENT,
                ClipContext.Fluid.ANY::canPick,
                CollisionContext.of(this));
    }

    public void harvestCrop(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
            // Drop items naturally (simulate player breaking)
            ItemStack tool = getMainHandItem();
            Block.dropResources(state, level, pos, level.getBlockEntity(pos), this, tool);

            // Replant at age 0
            level.setBlock(pos, crop.getStateForAge(0), 3);
            level.playSound(null, pos, state.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1, 1);
            this.addHarvestsTime();
        }
    }

    private void depositToChest(ServerLevel level, BlockPos chestPos) {
        BlockState state = level.getBlockState(chestPos);
        BlockEntity be = level.getBlockEntity(chestPos);

        if (be instanceof ChestBlockEntity chest) {
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                if (equipmentSlot.getType() == EquipmentSlot.Type.HAND) {
                    ItemStack stack = this.getItemBySlot(equipmentSlot);
                    if (!isChestFull(chest)) {
                        if (!stack.isEmpty() && (this.depositeType.isRightType(stack))) {
                            this.lookAt(EntityAnchorArgument.Anchor.FEET, new Vec3(chestPos.getX(), chestPos.getY() - 1, chestPos.getZ()));
                            this.swing(this.isLeftHanded() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                            ItemStack remaining = HopperBlockEntity.addItem(null, chest, stack, null);
                            chest.setChanged();
                            this.setItemSlot(equipmentSlot, remaining);
                            this.getInventory().setChanged();
                            chest.triggerEvent(1, 1);
                            if (state.getBlock() instanceof ChestBlock chestBlock) {
                                this.level.playSound(null, chestPos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.25f, 1);
                                this.setHarvestsTimes(0);
                            }
                        }
                    } else {
                        targetChestPos = tryFindNearbyChest(getLevel(), this.blockPosition(), 8);
                    }
                }
            }

            for (int i = 0; i < this.getInventory().getContainerSize(); i++) {
                ItemStack stack = this.getInventory().getItem(i);
                if (!isChestFull(chest)) {
                    if (!stack.isEmpty() && (this.depositeType.isRightType(stack))) {
                        // Make entity look at a target position
                        this.getLookControl().setLookAt(
                                chestPos.getX(), chestPos.getY(), chestPos.getZ(),
                                30.0F, // yaw change speed (degrees per tick)
                                30.0F  // pitch change speed
                        );
                        //this.lookAt(EntityAnchorArgument.Anchor.FEET, new Vec3(chestPos.getX(), chestPos.getY() - 1, chestPos.getZ()));
                        this.swing(this.isLeftHanded() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                        ItemStack remaining = HopperBlockEntity.addItem(null, chest, stack, null);
                        chest.setChanged();
                        this.getInventory().setItem(i, remaining);
                        this.getInventory().setChanged();
                        if ((i == 0 || i == this.inventory.getContainerSize()) && state.getBlock() instanceof ChestBlock chestBlock) {
                            this.level.playSound(null, chestPos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.25f, 1);
                            this.setHarvestsTimes(0);
                        }
                        this.setTargetChestPos(null);
                    }
                } else {
                    targetChestPos = tryFindNearbyChest(getLevel(), this.blockPosition(), 8);
                }
            }
        } else {
            this.setTargetChestPos(null);
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

    public DepositeType getDepositeType() {
        return depositeType;
    }

    public void setDepositeType(DepositeType depositeType) {
        this.depositeType = depositeType;
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
        return this.depositeType == DepositeType.SEEDS || this.depositeType == DepositeType.BOTH;
    }

    @Override
    public Color getPawColor() {
        return Color.CYAN;
    }

    // Enums
    public enum DepositeType {
        SEEDS(Tags.Items.SEEDS),
        CROPS(tag("fruits"), Tags.Items.CROPS),
        BOTH(tag("fruits"), Tags.Items.CROPS, Tags.Items.SEEDS);

        final List<TagKey<Item>> tagKeys;

        DepositeType(TagKey<Item> crops, TagKey<Item> seeds) {
            this.tagKeys = List.of(crops, seeds);
        }

        DepositeType(TagKey<Item> typeTag) {
            this.tagKeys = List.of(typeTag);
        }

        DepositeType(TagKey<Item> fruits, TagKey<Item> crops, TagKey<Item> seeds) {
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


        public boolean isRightType(ItemStack stack) {
            return this.tagKeys.stream().anyMatch(stack::is);
        }

        public DepositeType switchDepositeType() {
            int next = (this.ordinal() + 1) % DepositeType.values().length;
            return DepositeType.values()[next];
        }
    }

    public static TagKey<Item> tag(String name) {
        return ItemTags.create(new ResourceLocation("forge", name));
    }

    @Mod.EventBusSubscriber
    public static class EventHandle {

        @SubscribeEvent
        public static void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
            if (event.getEntity() instanceof PrototypeEntity) {
                event.setCanceled(true);
            } else if (event.getEntity() instanceof Player player) {
                TransfurVariantInstance<?> transfurVariant = ProcessTransfur.getPlayerTransfurVariant(player);
                if (transfurVariant != null && transfurVariant.is(ChangedAddonTransfurVariants.PROTOTYPE)) {
                    event.setCanceled(true);
                }
            }
        }

    }
}
