package net.foxyas.changedaddon.item.clothes;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.init.ChangedAccessorySlots;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.minecraft.core.NonNullList;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


public class DyeableShorts extends DyeableClothingItem implements DyeableLeatherItem {

    public DyeableShorts() {
        super();
        CauldronInteraction.WATER.put(this, CauldronInteraction.DYED_ITEM);
    }

    @Override
    public boolean allowedInSlot(ItemStack itemStack, LivingEntity wearer, AccessorySlotType slot) {
        return slot == ChangedAccessorySlots.LEGS.get() || slot == ChangedAccessorySlots.LOWER_BODY.get();
    }

    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    public SoundEvent getEquipSound() {
        return ChangedSounds.EQUIP3;
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        this.setColor(stack, 0xffffff);
        return stack;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return ImmutableMultimap.of();
    }

    public SoundEvent getBreakSound(ItemStack itemStack) {
        return ChangedSounds.SLASH10;
    }

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab tab, @NotNull NonNullList<ItemStack> items) {
        if (this.allowdedIn(tab)) {
            for (DefaultColors color : DefaultColors.values()) {
                ItemStack stack = new ItemStack(this);
                this.setColor(stack, color.getColorToInt());
                items.add(stack);
            }
        }
    }

    @Override
    protected boolean allowdedIn(@NotNull CreativeModeTab tab) {
        if (tab == ChangedTabs.TAB_CHANGED_ITEMS) {
            return false;
        } else if (tab == ChangedAddonTabs.TAB_CHANGED_ADDON) {
            return true;
        }
        return super.allowdedIn(tab);
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if ("overlay".equals(type)) {
            return "changed_addon:textures/models/armor/dyeable_shorts_layer_1_overlay.png"; // totalmente invisível
        }
        return "changed_addon:textures/models/armor/dyeable_shorts_layer_1.png";
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientInitializer {
        @SubscribeEvent
        public static void onItemColorsInit(ColorHandlerEvent.Item event) {
            event.getItemColors().register(
                    (stack, layer) -> ((DyeableLeatherItem) stack.getItem()).getColor(stack),
                    ChangedAddonItems.DYEABLE_SHORTS.get());
        }
    }
}