package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.client.model.ModelNewHyperFlower;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public abstract class LunarroseItem extends ArmorItem {
    public LunarroseItem(EquipmentSlot slot, Item.Properties properties) {
        super(new ArmorMaterial() {
            @Override
            public int getDurabilityForSlot(@NotNull EquipmentSlot slot) {
                return new int[]{13, 15, 16, 11}[slot.getIndex()] * 100;
            }

            @Override
            public int getDefenseForSlot(@NotNull EquipmentSlot slot) {
                return new int[]{0, 0, 0, 0}[slot.getIndex()];
            }

            @Override
            public int getEnchantmentValue() {
                return 100;
            }

            @Override
            public @NotNull SoundEvent getEquipSound() {
                return SoundEvents.ARMOR_EQUIP_LEATHER;
            }

            @Override
            public @NotNull Ingredient getRepairIngredient() {
                return Ingredient.of();
            }

            @Override
            public @NotNull String getName() {
                return "lunarrose";
            }

            @Override
            public float getToughness() {
                return 0f;
            }

            @Override
            public float getKnockbackResistance() {
                return 0f;
            }
        }, slot, properties);
    }

    public static class Helmet extends LunarroseItem {
        public Helmet() {
            super(EquipmentSlot.HEAD, new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON).fireResistant());
        }

        public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer) {
            consumer.accept(new IItemRenderProperties() {
                @Override
                public HumanoidModel getArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
                    HumanoidModel armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
                            Map.of("head", new ModelNewHyperFlower(Minecraft.getInstance().getEntityModels().bakeLayer(ModelNewHyperFlower.LAYER_LOCATION)).HyperFlowerModel, "hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                                    "body", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_arm",
                                    new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_leg",
                                    new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
                    armorModel.crouching = living.isShiftKeyDown();
                    armorModel.riding = defaultModel.riding;
                    armorModel.young = living.isBaby();
                    return armorModel;
                }
            });
        }

        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "changed_addon:textures/entities/new_moon_rose.png";
        }

        @Override
        public void onArmorTick(ItemStack itemstack, Level level, Player player) {
            if (player == null) return;

            TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);

            if (instance == null) {
                if (!player.hasEffect(MobEffects.REGENERATION)) {
                    if (!player.level.isClientSide())
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 1, false, false));
                }
                {
                    final Vec3 _center = new Vec3((player.getX()), (player.getY()), (player.getZ()));
                    List<Entity> _entfound = level.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(1 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                            .toList();
                    for (Entity entityiterator : _entfound) {
                        if (!(entityiterator == player)) {
                            if (!(entityiterator instanceof LivingEntity _livEnt && _livEnt.hasEffect(MobEffects.REGENERATION))) {
                                if ((player.hasEffect(MobEffects.REGENERATION) ? player.getEffect(MobEffects.REGENERATION).getAmplifier() : 0) < 3) {
                                    if (entityiterator instanceof LivingEntity _entity && !_entity.level.isClientSide())
                                        _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 3, false, false));
                                    if (entityiterator instanceof LivingEntity _entity && !_entity.level.isClientSide())
                                        _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 270, 1, false, false));
                                }
                            }
                        }
                    }
                }
                itemstack.getOrCreateTag().putBoolean("Unbreakable", true);
            } else if (instance.getFormId().toString().startsWith("changed_addon:form_puro_kind") || instance.getFormId().toString().equals("changed_addon:form_light_latex_wolf")) {
                if (!player.hasEffect(MobEffects.REGENERATION)) {
                    if (!player.level.isClientSide())
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 1, false, false));
                }
                {
                    final Vec3 _center = new Vec3((player.getX()), (player.getY()), (player.getZ()));
                    List<Entity> _entfound = level.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(1 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                            .toList();
                    for (Entity entityiterator : _entfound) {
                        if (!(entityiterator == player)) {
                            if (!(entityiterator instanceof LivingEntity _livEnt && _livEnt.hasEffect(MobEffects.REGENERATION))) {
                                if ((player.hasEffect(MobEffects.REGENERATION) ? player.getEffect(MobEffects.REGENERATION).getAmplifier() : 0) < 3) {
                                    if (entityiterator instanceof LivingEntity _entity && !_entity.level.isClientSide())
                                        _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 3, false, false));
                                    if (entityiterator instanceof LivingEntity _entity && !_entity.level.isClientSide())
                                        _entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 270, 1, false, false));
                                }
                            }
                        }
                    }
                }
                itemstack.getOrCreateTag().putBoolean("Unbreakable", true);
            }
        }
    }
}
