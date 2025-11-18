package net.foxyas.changedaddon.entity.advanced;

import net.foxyas.changedaddon.entity.api.CustomPatReaction;
import net.foxyas.changedaddon.entity.api.IDynamicPawColor;
import net.foxyas.changedaddon.entity.api.ItemHandlerHolder;
import net.foxyas.changedaddon.entity.defaults.AbstractCanTameChangedEntity;
import net.foxyas.changedaddon.entity.goals.prototype.*;
import net.foxyas.changedaddon.menu.PrototypeMenu;
import net.foxyas.changedaddon.util.ColorUtil;
import net.foxyas.changedaddon.util.DynamicClipContext;
import net.foxyas.changedaddon.util.FoxyasUtils;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.EyeStyle;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.*;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.EntityArmorInvWrapper;
import net.minecraftforge.items.wrapper.EntityHandsInvWrapper;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PrototypeEntity extends AbstractCanTameChangedEntity implements MenuProvider, CustomPatReaction, IDynamicPawColor, ItemHandlerHolder {

    // Constants
    public static final int MAX_HARVEST_TIMES = 32;

    // Fields
    private int harvestsTimes = 0;
    private DepositType depositType = DepositType.BOTH;
    @Nullable
    private BlockPos targetChestPos = null;

    private final IItemHandlerModifiable hands = new EntityHandsInvWrapper(this);
    private final ItemStackHandler inv = new ItemStackHandler(9);
    private final CombinedInvWrapper handsInv = new CombinedInvWrapper(hands, inv);
    private final CombinedInvWrapper combinedInv = new CombinedInvWrapper(new EntityArmorInvWrapper(this), hands, inv);

    // Constructors
    public PrototypeEntity(EntityType<PrototypeEntity> type, Level world) {
        super(type, world);
        xpReward = 0;
        setPersistenceRequired();
    }

    @Override
    public IItemHandler getItemHandler() {
        return combinedInv;
    }

    public SimpleContainer getInventory() {
        SimpleContainer container = new SimpleContainer(9);
        for (int i = 0; i < container.getContainerSize(); i++) {
            container.setItem(i, combinedInv.getStackInSlot(i));
            container.setChanged();
        }
        return container;
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
    protected void setAttributes(AttributeMap attributes) {
    }

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
        if (patter.level.isClientSide) return;

        if (!isTame()) {
            tame(patter);
            return;
        }

        InteractionResult interactionresult = super.mobInteract(patter, hand);
        if ((interactionresult.consumesAction() && !isBaby()) || !isOwnedBy(patter)) return;

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
        tag.put("Inv", inv.serializeNBT());
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

        if (tag.contains("Inventory")) {//DataFix
            SimpleContainer container = new SimpleContainer(9);
            container.fromTag(tag.getList("Inventory", 10));
            for (int i = 0; i < 9; i++) {
                inv.setStackInSlot(i, container.getItem(i));
            }
        } else inv.deserializeNBT(tag.getCompound("Inv"));

        if (tag.contains("harvestDone")) {
            harvestsTimes = tag.getInt("harvestsTimes");
        }
        if (tag.contains("DepositeType")) {//DataFix
            depositType = DepositType.valueOf(tag.getString("DepositeType").toUpperCase());
        } else if (tag.contains("DepositType")) {
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
        tryToDepositItemsInTargetChest();

        if (tickCount % 120 != 0) return;

        if (harvestsTimes >= MAX_HARVEST_TIMES) {
            harvestsTimes = 0;
        }
    }

    private void tryToDepositItemsInTargetChest() {
        if (wantsToDeposit() && targetChestPos != null && blockPosition().closerThan(targetChestPos, 2.0)) {
            if (getLevel() instanceof ServerLevel serverLevel) {
                depositToChest(serverLevel, targetChestPos);
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

        ItemStack stack;
        ItemEntity itemEntity;
        for (int i = 0; i < combinedInv.getSlots(); i++) {
            stack = combinedInv.extractItem(i, combinedInv.getSlotLimit(i), false);
            if (stack.isEmpty()) continue;

            itemEntity = new ItemEntity(level, getX(), getY() + 0.5, getZ(), stack);
            itemEntity.setDeltaMovement(
                    (random.nextDouble() - 0.5) * 0.2, 0.2, (random.nextDouble() - 0.5) * 0.2
            );
            level.addFreshEntity(itemEntity);
        }
    }

    // Inventory related methods
    @Override
    public boolean canTakeItem(@NotNull ItemStack pItemstack) {
        if (pItemstack.isEmpty()) return false;
        if (canTakeItemNoArmor(pItemstack)) {
            return true;
        }

        return super.canTakeItem(pItemstack);
    }

    public boolean canTakeItemNoArmor(@NotNull ItemStack stack) {
        return stack.is(Tags.Items.CROPS) || (stack.is(FORGE_FRUITS) || stack.is(Tags.Items.SEEDS) || stack.is(Tags.Items.SHEARS) || pickAbleItems().contains(stack.getItem()));
    }

    @Override
    public boolean canPickUpLoot() {
        return true;
    }

    @Override
    public boolean canHoldItem(@NotNull ItemStack pStack) {
        return hasSpaceInInvOrHands() && canTakeItem(pStack);
    }

    @Override
    protected void pickUpItem(@NotNull ItemEntity pItemEntity) {
        ItemStack pStack = pItemEntity.getItem();
        if (canTakeItemNoArmor(pStack)) {
            ItemStack remainder = ItemHandlerHelper.insertItem(handsInv, pStack, false);

            if (remainder.isEmpty()) {
                pItemEntity.discard();
            } else pItemEntity.setItem(remainder);
            return;
        }
        super.pickUpItem(pItemEntity);
    }

    @Override
    public boolean canTrample(@NotNull BlockState state, @NotNull BlockPos pos, float fallDistance) {
        return false;
    }

    // compatibility with the "/item replace" command
    @Override
    public @NotNull SlotAccess getSlot(int slot) {
        if (getEquipmentSlot(slot) == null) {
            if (slot >= 0 && slot < this.getInventory().getContainerSize()) {
                return SlotAccess.forContainer(this.getInventory(), slot);
            }
        }

        return super.getSlot(slot);
    }

    @Nullable
    protected static EquipmentSlot getEquipmentSlot(int pIndex) {
        if (pIndex == 100 + EquipmentSlot.HEAD.getIndex()) {
            return EquipmentSlot.HEAD;
        } else if (pIndex == 100 + EquipmentSlot.CHEST.getIndex()) {
            return EquipmentSlot.CHEST;
        } else if (pIndex == 100 + EquipmentSlot.LEGS.getIndex()) {
            return EquipmentSlot.LEGS;
        } else if (pIndex == 100 + EquipmentSlot.FEET.getIndex()) {
            return EquipmentSlot.FEET;
        } else if (pIndex == 98) {
            return EquipmentSlot.MAINHAND;
        } else {
            return pIndex == 99 ? EquipmentSlot.OFFHAND : null;
        }
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
        for (int i = 0; i < handsInv.getSlots(); i++) {
            if (handsInv.getStackInSlot(i).isEmpty()) return false;
        }

        return true;
    }

    public boolean hasSpaceInInvOrHands() {
        return !isInventoryFull();
    }

    public boolean wantsToDeposit() {
        int crops = 0;
        ItemStack stack;
        for (int i = 0; i < handsInv.getSlots(); i++) {
            stack = handsInv.getStackInSlot(i);
            if (depositType.test(stack)) crops++;
            if (crops >= 4) return true;
        }

        return false;
    }

    // Crop and chest related methods
    public BlockPos tryFindNearbyChest(Level level, BlockPos center, int range) {
        List<ItemStack> carriedItems = new ArrayList<>();
        for (int i = 0; i < handsInv.getSlots(); i++) {//Deposit from hands & inv
            ItemStack stack = handsInv.getStackInSlot(i);
            if (!stack.isEmpty() && depositType.test(stack)) carriedItems.add(stack.copy());
        }

        BlockPos closestChest = null, bestChest = null;
        double closestDist = Double.MAX_VALUE, bestDist = closestDist;
        double dist;
        boolean isFull, potentiallyBest;

        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-range, -range, -range), center.offset(range, range, range))) {
            if (!(level.getBlockEntity(pos) instanceof ChestBlockEntity chest)) continue;

            dist = pos.distSqr(center);
            if (dist >= bestDist) continue;

            isFull = true;
            potentiallyBest = false;
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                ItemStack chestItem = chest.getItem(slot);
                if (chestItem.isEmpty()) {//If not full
                    isFull = false;

                    if (dist < closestDist) {
                        closestDist = dist;
                        closestChest = pos.immutable();
                    }

                    if (potentiallyBest) {
                        bestDist = dist;
                        bestChest = pos.immutable();
                        break;
                    }
                    continue;
                }

                for (ItemStack carried : carriedItems) {
                    if (!ItemStack.isSameItemSameTags(carried, chestItem)) continue;

                    if (chestItem.getCount() >= chestItem.getMaxStackSize() && isFull) {
                        potentiallyBest = true;
                        break;
                    }

                    bestDist = dist;
                    bestChest = pos.immutable();
                    break;
                }
                if (pos.equals(bestChest)) break;
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
            if (!(state.getBlock() instanceof CropBlock crop) || !crop.isMaxAge(state)) continue;

            dist = pos.distSqr(center);
            if (dist >= closestDist) continue;

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
        if (!(state.getBlock() instanceof CropBlock crop) || !crop.isMaxAge(state)) return;

        // Drop items naturally (simulate player breaking)
        ItemStack tool = getMainHandItem();
        List<ItemStack> drops = Block.getDrops(state, level, pos, level.getBlockEntity(pos), this, tool);

        for (ItemStack stack : drops) {
            stack = ItemHandlerHelper.insertItem(handsInv, stack, false);
            if (stack.isEmpty()) continue;

            Block.popResource(level, pos, stack);
        }

        // Replant at age 0
        level.setBlock(pos, crop.getStateForAge(0), 3);
        level.playSound(null, pos, state.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1, 1);
        addHarvestsTime();
    }

    private void depositToChest(ServerLevel level, BlockPos chestPos) {
        BlockState state = level.getBlockState(chestPos);
        BlockEntity be = level.getBlockEntity(chestPos);

        if (!(be instanceof ChestBlockEntity chest)) {
            setTargetChestPos(null);
            return;
        }

        boolean anyInserted = false;
        ItemStack stack, remainder;
        IItemHandler handler = chest.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().orElseThrow();
        for (int i = 0; i < handsInv.getSlots(); i++) {
            stack = handsInv.getStackInSlot(i);
            if (stack.isEmpty() || !depositType.test(stack)) continue;

            remainder = ItemHandlerHelper.insertItem(handler, stack, false);
            if (remainder == stack) continue;

            anyInserted = true;
            handsInv.setStackInSlot(i, remainder);
        }

        if (anyInserted) {
            lookAt(EntityAnchorArgument.Anchor.FEET, new Vec3(chestPos.getX(), chestPos.getY() - 1, chestPos.getZ()));
            swing(isLeftHanded() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            chest.startOpen(FakePlayerFactory.getMinecraft(level));
            chest.triggerEvent(1, 1);
            chest.setChanged();
            if (state.getBlock() instanceof ChestBlock) {
                level.playSound(null, chestPos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.25f, 1);
                setHarvestsTimes(0);
            }
        } else setTargetChestPos(tryFindNearbyChest(level, blockPosition(), 8));
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

        public boolean willDepositSeeds() {
            return this == SEEDS || this == BOTH;
        }
    }

}
