package net.foxyas.changedaddon.item.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.foxyas.changedaddon.init.ChangedAddonAttributes;
import net.foxyas.changedaddon.init.ChangedAddonSoundEvents;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.foxyas.changedaddon.item.clothes.AccessoryItemExtension;
import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAccessorySlots;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.item.ClothingItem;
import net.ltxprogrammer.changed.item.ClothingState;
import net.ltxprogrammer.changed.item.ExtendedItemProperties;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class HazardBodySuit extends ClothingItem implements AccessoryItemExtension {
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
        LivingEntity wearer = slotContext.wearer();
        /*if (wearer instanceof Player player) {
            player.displayClientMessage(new TextComponent("Iframes =" + player.invulnerableTime + "\n"
                    + player.hurtDir + "\n"
                    + player.hurtDuration + "\n"
                    + player.hurtMarked + "\n"
                    + player.hurtTime + "\n"), false);
        }*/
        boolean nonHurtFrame = wearer.hurtTime <= 10 && wearer.invulnerableTime <= 10;
        if (wearer.hurtMarked || !nonHurtFrame) return;

        if (!source.isBypassArmor() && !(source instanceof ChangedDamageSources.TransfurDamageSource)) {
            this.applyDamage(source, amount, slotContext);
        } else if (source instanceof ChangedDamageSources.TransfurDamageSource) {
            this.applyDamage(source, amount, slotContext);
        }
    }

    @Override
    public SoundEvent getBreakSound(ItemStack itemStack) {
        return super.getBreakSound(itemStack);
    }

    public boolean IsAffectedByMending(AccessorySlotType slotType, ItemStack itemStack) {
        return true;
    }

    public void applyDamage(DamageSource damageSource, float amount, AccessorySlotContext<?> slotContext) {
        if (!(amount <= 0.0F)) {
            amount /= 4.0F;
            if (amount < 1.0F) {
                amount = 1.0F;
            }
            ItemStack itemStack = slotContext.stack();
            LivingEntity player = slotContext.wearer();
            if ((!damageSource.isFire() || !itemStack.getItem().isFireResistant())) {
                itemStack.hurtAndBreak((int) amount, player, (livingEntity) -> {
                    if (!itemStack.isEmpty()) {
                        if (!livingEntity.isSilent()) {
                            livingEntity.level.playSound(null, livingEntity,
                                    this.getBreakSound(itemStack),
                                    livingEntity.getSoundSource(),
                                    0.8F,
                                    0.8F + livingEntity.level.random.nextFloat() * 0.4F);
                        }

                        //livingEntity.spawnItemParticles(pStack, 5);

                        for (int i = 0; i < 5; ++i) {
                            Vec3 vec3 = new Vec3(((double) livingEntity.getRandom().nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
                            vec3 = vec3.xRot(-livingEntity.getXRot() * ((float) Math.PI / 180F));
                            vec3 = vec3.yRot(-livingEntity.getYRot() * ((float) Math.PI / 180F));
                            double d0 = (double) (-livingEntity.getRandom().nextFloat()) * 0.6D - 0.3D;
                            Vec3 vec31 = new Vec3(((double) livingEntity.getRandom().nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
                            vec31 = vec31.xRot(-livingEntity.getXRot() * ((float) Math.PI / 180F));
                            vec31 = vec31.yRot(-livingEntity.getYRot() * ((float) Math.PI / 180F));
                            vec31 = vec31.add(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ());
                            if (livingEntity.level instanceof ServerLevel serverLevel) //Forge: Fix MC-2518 spawnParticle is nooped on server, need to use server specific variant
                            {
                                serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, itemStack), vec31.x, vec31.y, vec31.z, 1, vec3.x, vec3.y + 0.05D, vec3.z, 0.0D);
                            }
                        }

                    }
                    //livingEntity.broadcastBreakEvent(EquipmentSlot.CHEST);
                });
            }

        }
    }

    @Override
    public boolean allowedInSlot(ItemStack itemStack, LivingEntity wearer, AccessorySlotType slot) {
        return slot == ChangedAccessorySlots.FULL_BODY.get();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.CHEST) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

            Boolean helmeted = this.getClothingState(stack).getValue(HELMET);
            float multiplier = helmeted ? 1f : 0.75f;
            builder.put(
                    ChangedAttributes.TRANSFUR_TOLERANCE.get(),
                    new AttributeModifier(
                            UUID.fromString("00000000-0000-0000-0000-000000000000"),
                            "Hazard Transfur Tolerance Buff",
                            multiplier,
                            AttributeModifier.Operation.MULTIPLY_TOTAL
                    )
            );

            builder.put(
                    ChangedAttributes.TRANSFUR_DAMAGE.get(),
                    new AttributeModifier(
                            UUID.fromString("00000000-0000-0000-0000-000000000000"),
                            "Hazard Transfur Tolerance Buff",
                            -1,
                            AttributeModifier.Operation.MULTIPLY_TOTAL
                    )
            );

            builder.put(
                    ChangedAddonAttributes.LATEX_RESISTANCE.get(),
                    new AttributeModifier(
                            UUID.fromString("00000000-0000-0000-0000-000000000001"),
                            "Hazard Armor Speed Debuff",
                            0.05 * multiplier,
                            AttributeModifier.Operation.ADDITION
                    )
            );

            builder.put(
                    Attributes.ARMOR,
                    new AttributeModifier(
                            UUID.fromString("00000000-0000-0000-0000-000000000002"),
                            "Hazard Armor Buff",
                            2 * multiplier,
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
