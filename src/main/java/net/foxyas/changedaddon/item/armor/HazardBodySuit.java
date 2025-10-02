package net.foxyas.changedaddon.item.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.event.ProgressTransfurEvents;
import net.foxyas.changedaddon.init.ChangedAddonAttributes;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.init.ChangedAddonSoundEvents;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAccessorySlots;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.item.ClothingItem;
import net.ltxprogrammer.changed.item.ClothingState;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class HazardBodySuit extends ClothingItem {
    public static final BooleanProperty HELMET = BooleanProperty.create("helmet");

    public HazardBodySuit() {
        super();
    }

    @Override
    public ClothingState defaultClothingState() {
        return super.defaultClothingState().setValue(HELMET, false);
    }

    @Override
    public SoundEvent getEquipSound() {
        return ChangedAddonSoundEvents.ARMOR_EQUIP;
    }

    @Override
    public void accessoryInteract(AccessorySlotContext<?> slotContext) {
        super.accessoryInteract(slotContext);

        this.setClothingState(
                slotContext.stack(),
                this.getClothingState(slotContext.stack()).cycle(HELMET)
        );

        SoundEvent changeSound = this.getEquipSound();
        if (changeSound != null) {
            slotContext.wearer().playSound(changeSound, 1.0F, 1.0F);
        }
    }

    @Override
    protected void createClothingStateDefinition(StateDefinition.Builder<ClothingItem, ClothingState> builder) {
        super.createClothingStateDefinition(builder);
        builder.add(HELMET);
    }

    @Override
    protected void addInteractInstructions(Consumer<Component> builder) {
        builder.accept(new TranslatableComponent(
                INTERACT_INSTRUCTIONS,
                Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage()
        ).withStyle(ChatFormatting.GRAY));
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
    public int getMaxDamage(ItemStack stack) {
        return 525;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    public void accessoryDamaged(AccessorySlotContext<?> slotContext, DamageSource source, float amount) {
        if (!source.isBypassArmor() && !(source instanceof ChangedDamageSources.TransfurDamageSource)) {
            this.applyDamage(source, amount, slotContext);
        } else if (source instanceof ChangedDamageSources.TransfurDamageSource) {
            this.applyDamage(source, amount, slotContext);
        }
    }

    public void applyDamage(DamageSource damageSource, float amount, AccessorySlotContext<?> slotContext) {
        if (!(amount <= 0.0F)) {
            amount /= 4.0F;
            if (amount < 1.0F) {
                amount = 1.0F;
            }
            ItemStack itemStack = slotContext.stack();
            LivingEntity player = slotContext.wearer();
            if ((!damageSource.isFire() || !itemStack.getItem().isFireResistant()) && itemStack.getItem() instanceof ArmorItem) {
                itemStack.hurtAndBreak((int)amount, player, (livingEntity) -> livingEntity.broadcastBreakEvent(EquipmentSlot.CHEST));
            }

        }
    }

    @Override
    public boolean allowedInSlot(ItemStack itemStack, LivingEntity wearer, AccessorySlotType slot) {
        return slot == ChangedAccessorySlots.FULL_BODY.get();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.CHEST) { // ou FULL_BODY se quiser aplicar só nesse caso
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

            builder.put(
                    ChangedAttributes.TRANSFUR_TOLERANCE.get(),
                    new AttributeModifier(
                            UUID.fromString("00000000-0000-0000-0000-000000000000"),
                            "Hazard Transfur Tolerance Buff",
                            1,
                            AttributeModifier.Operation.MULTIPLY_TOTAL
                    )
            );

            builder.put(
                    ChangedAddonAttributes.LATEX_RESISTANCE.get(),
                    new AttributeModifier(
                            UUID.fromString("00000000-0000-0000-0000-000000000001"),
                            "Hazard Armor Speed Debuff",
                            0.05,
                            AttributeModifier.Operation.ADDITION
                    )
            );

            builder.put(
                    Attributes.ARMOR,
                    new AttributeModifier(
                            UUID.fromString("00000000-0000-0000-0000-000000000002"),
                            "Hazard Armor Buff",
                            2,
                            AttributeModifier.Operation.ADDITION
                    )
            );

            builder.put(
                    Attributes.MOVEMENT_SPEED,
                    new AttributeModifier(
                            UUID.fromString("00000000-0000-0000-0000-000000000003"),
                            "Hazard Armor Speed Debuff",
                            -0.05,
                            AttributeModifier.Operation.MULTIPLY_TOTAL
                    )
            );

            builder.put(
                    Attributes.ATTACK_SPEED,
                    new AttributeModifier(
                            UUID.fromString("00000000-0000-0000-0000-000000000004"),
                            "Hazard Armor Attack Speed Debuff",
                            -0.09,
                            AttributeModifier.Operation.MULTIPLY_TOTAL
                    )
            );

            return builder.build();
        }


        return super.getAttributeModifiers(slot, stack);
    }

    public String getHelmetState(ItemStack stack) {
        Boolean flag = this.getClothingState(stack).getValue(HELMET);
        return flag ? "helmeted" : "no_helmet";
    }


    private <T extends Entity> String getPlayerModelStyle(T entity) {
        if (entity instanceof AbstractClientPlayer player) {
            return player.getModelName();
        }
        return "default";
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (entity instanceof ChangedEntity) {
            ResourceLocation itemId = stack.getItem().getRegistryName();
            return String.format("%s:textures/models/hazard_suit/%s_%s_tf.png", itemId.getNamespace(), itemId.getPath(), getHelmetState(stack));
        }
        if (entity instanceof Player player) {
            TransfurVariantInstance<?> transfurVariant = ProcessTransfur.getPlayerTransfurVariant(player);
            if (transfurVariant != null && transfurVariant.isTransfurring()) {
                ResourceLocation itemId = stack.getItem().getRegistryName();
                return String.format("%s:textures/models/hazard_suit/%s_%s_tf.png", itemId.getNamespace(), itemId.getPath(), getHelmetState(stack));
            }
        }


        ResourceLocation itemId = stack.getItem().getRegistryName();
        return String.format("%s:textures/models/hazard_suit/%s_%s_%s.png", itemId.getNamespace(), itemId.getPath(), getHelmetState(stack), getPlayerModelStyle(entity));
    }
}
