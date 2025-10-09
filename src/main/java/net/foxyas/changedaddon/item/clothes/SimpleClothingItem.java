package net.foxyas.changedaddon.item.clothes;

import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.item.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class SimpleClothingItem extends Item implements Wearable, Clothing, ExtendedItemProperties {
    public static String INTERACT_INSTRUCTIONS = "changed.instruction.clothing_state";
    public static BooleanProperty CLOSED = BooleanProperty.create("closed");
    public StateDefinition<SimpleClothingItem, ClothingState> stateDefinition;
    public ClothingState defaultClothingState;

    public SimpleClothingItem() {
        super((new Item.Properties()).tab(ChangedTabs.TAB_CHANGED_ITEMS).durability(5));
        StateDefinition.Builder<SimpleClothingItem, ClothingState> builder = new StateDefinition.Builder<>(this);
        this.createClothingStateDefinition(builder);
        this.stateDefinition = builder.create(SimpleClothingItem::defaultClothingState, ClothingState::new);
        this.registerDefaultState(this.stateDefinition.any());
        DispenserBlock.registerBehavior(this, AccessoryItem.DISPENSE_ITEM_BEHAVIOR);
    }

    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> builder, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, builder, tooltipFlag);
        if (tooltipFlag.isAdvanced()) {
            builder.add((new TextComponent(this.getClothingState(stack).toString())).withStyle(ChatFormatting.DARK_GRAY));
        }

    }

    protected void addInteractInstructions(Consumer<Component> builder) {
        builder.accept((new TranslatableComponent(INTERACT_INSTRUCTIONS, Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage())).withStyle(ChatFormatting.GRAY));
    }

    protected void createClothingStateDefinition(StateDefinition.Builder<SimpleClothingItem, ClothingState> builder) {
    }

    public ClothingState defaultClothingState() {
        return this.defaultClothingState;
    }

    public ClothingState getClothingState(ItemStack stack) {
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag == null) {
            return this.defaultClothingState();
        } else {
            CompoundTag stateData = compoundTag.getCompound("state");
            AtomicReference<ClothingState> evaluatedState = new AtomicReference<>(this.defaultClothingState());
            stateData.getAllKeys().forEach((propertyName) -> {
                Property<?> property = this.stateDefinition.getProperty(propertyName);
                if (property != null) {
                    property.getValue(stateData.getString(propertyName)).ifPresent((value) -> evaluatedState.set(evaluatedState.get().setValue(property, (Comparable) value)));
                }
            });
            return evaluatedState.getAcquire();
        }
    }

    @SuppressWarnings("unchecked")
    public void setClothingState(ItemStack stack, net.ltxprogrammer.changed.item.ClothingState state) {
        CompoundTag tag = new CompoundTag();
        state.getProperties().forEach(property -> {
            tag.putString(property.getName(), ((Property) property).getName(state.getValue((Property) property)));
        });

        stack.getOrCreateTag().put("state", tag);
    }

    protected final void registerDefaultState(ClothingState clothingState) {
        this.defaultClothingState = clothingState;
    }

    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    public SoundEvent getEquipSound() {
        return ChangedSounds.EQUIP3;
    }

    public SoundEvent getBreakSound(ItemStack itemStack) {
        return ChangedSounds.SLASH10;
    }

    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        ResourceLocation itemId = stack.getItem().getRegistryName();
        return String.format("%s:textures/models/%s.png", itemId.getNamespace(), itemId.getPath());
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return AccessorySlots.getForEntity(player).map((slots) -> {
            ItemStack copy = stack.copy();
            if (slots.quickMoveStack(stack)) {
                AccessorySlots.equipEventAndSound(player, copy);
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
            } else {
                return InteractionResultHolder.pass(stack);
            }
        }).orElse(InteractionResultHolder.pass(stack));
    }
}
