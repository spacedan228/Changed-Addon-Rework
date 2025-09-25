package net.foxyas.changedaddon.item;

import com.google.common.collect.Multimap;
import net.foxyas.changedaddon.configuration.ChangedAddonServerConfiguration;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.init.ChangedAddonSounds;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.foxyas.changedaddon.item.tooltip.TransfurTotemTooltipComponent;
import net.foxyas.changedaddon.procedures.SummonDripParticlesProcedure;
import net.foxyas.changedaddon.util.PlayerUtil;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.item.Syringe;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class TransfurTotemItem extends Item {

    public static final AttributeModifier TOTEM_BUFF_ATTACK = new AttributeModifier(UUID.fromString("17c5b5cf-bdae-4191-84d1-433db7cba751"), "transfur_stats", 4, AttributeModifier.Operation.ADDITION);
    public static final AttributeModifier TOTEM_BUFF_DEFENSE = new AttributeModifier(UUID.fromString("17c5b5cf-bdae-4191-84d1-433db7cba752"), "transfur_stats", 8, AttributeModifier.Operation.ADDITION);
    public static final AttributeModifier TOTEM_BUFF_ARMOR = new AttributeModifier(UUID.fromString("17c5b5cf-bdae-4191-84d1-433db7cba753"), "transfur_stats", 6, AttributeModifier.Operation.ADDITION);

    public TransfurTotemItem() {
        super(new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON).stacksTo(1).fireResistant().rarity(Rarity.RARE));
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack defaultInstance = super.getDefaultInstance();
        defaultInstance.getOrCreateTag().putString("form", "");
        return defaultInstance;
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack) {
        if (Syringe.getVariant(pStack) == null) {
            return super.getTooltipImage(pStack);
        }
        return Optional.of(new TransfurTotemTooltipComponent(pStack));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return super.getAttributeModifiers(slot, stack);
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    public static class ShowTransfurTotemItemTip {

        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
            ItemStack stack = event.getItemStack();
            List<Component> tooltip = event.getToolTip();

            if (stack.getItem() == ChangedAddonItems.TRANSFUR_TOTEM.get()) {
                CompoundTag itemTag = stack.getOrCreateTag();
                if ((itemTag.getString("form")).isEmpty())
                    tooltip.add(1, (new TranslatableComponent("item.changed_addon.transfur_totem.no_form_linked")));
                else {
                    TransfurVariant<?> variant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(TagUtil.getResourceLocation(itemTag, "form"));
                    if (variant == null) {
                        tooltip.add(1, (new TranslatableComponent("item.changed_addon.transfur_totem.no_form_linked")));
                        return;
                    }
                    if (Screen.hasShiftDown() && !Screen.hasAltDown() && !Screen.hasControlDown())
                        tooltip.add(1, new TextComponent(("§6Form=" + itemTag.getString("form"))));
                    else if (Screen.hasAltDown() && Screen.hasControlDown())
                        tooltip.add(1, (new TranslatableComponent("item.changed_addon.transfur_totem.desc_1")));
                    else {
                        String ID = Syringe.getVariantDescriptionId(stack);
                        tooltip.add(1, new TextComponent(("§6(" + new TranslatableComponent(ID).getString() + ")")));
                    }
                }
            }
        }
    }

    private static void tryLinkForm(Level level, Player player, ItemStack itemstack) {
        TransfurVariantInstance<?> tf = ProcessTransfur.getPlayerTransfurVariant(player);
        ResourceLocation latexFormRes = tf == null ? null : tf.getFormId();
        if (latexFormRes == null) return;

        String latexForm = latexFormRes.toString();

        if (ChangedAddonServerConfiguration.ACCEPT_ALL_VARIANTS.get() || latexForm.startsWith("changed:form"))
            linkForm(level, player, itemstack, tf, latexForm);
        else if (latexForm.startsWith("changed_addon:form")) {
            cooldown(player, itemstack, 50);
            visualActivate(level, player, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR);
            player.displayClientMessage(new TranslatableComponent("changed_addon.latex_totem.not_valid"), true);
        } else if (latexForm.startsWith("changed:special"))
            linkForm(level, player, itemstack, tf, "changed:form_light_latex_wolf");
    }

    private static void linkForm(Level level, Player player, ItemStack stack, TransfurVariantInstance<?> tf, String form) {
        stack.getOrCreateTag().putString("form", form);
        CompoundTag variantData = tf.save();
        variantData.remove("previousAttributes");
        variantData.remove("newAttributes");
        variantData.remove("transfurProgressionO");
        variantData.remove("transfurProgression");
        stack.getOrCreateTag().put("TransfurVariantData", variantData);
        activateVisuals(level, player, stack, null, 100, SoundEvents.BEACON_ACTIVATE);
    }

    private static void cooldown(Player entity, ItemStack itemstack, int ticks) {
        if (!entity.getAbilities().instabuild) entity.getCooldowns().addCooldown(itemstack.getItem(), ticks);
    }

    private static void activateVisuals(Level level, Player entity, ItemStack itemstack, String advancement, int cooldown, SoundEvent soundEvent) {
        if (level.isClientSide())
            Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);

        cooldown(entity, itemstack, cooldown);
        if (soundEvent != null) visualActivate(level, entity, soundEvent);

        if (advancement != null)
            grantAdvancement(entity, advancement);
    }

    private static void visualActivate(Level level, Player player, SoundEvent sound) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.NEUTRAL, 1, 1);
    }

    private static void grantAdvancement(Entity entity, String id) {
        if (!(entity instanceof ServerPlayer player)) return;

        Advancement adv = player.server.getAdvancements().getAdvancement(new ResourceLocation(id));
        if (adv == null) return;

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
        if (!progress.isDone())
            for (String criterion : progress.getRemainingCriteria()) player.getAdvancements().award(adv, criterion);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemstack) {
        return UseAnim.BLOCK;
    }

    @Override
    public boolean hasCraftingRemainingItem() {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemstack) {
        return new ItemStack(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(@NotNull ItemStack itemstack) {
        String form = itemstack.getOrCreateTag().getString("form");
        if (form.isEmpty()) return false;
        else if (form.startsWith("changed:form")) return true;

        return form.startsWith("changed_addon:form") && ChangedAddonServerConfiguration.ACCEPT_ALL_VARIANTS.get() == true;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        InteractionResultHolder<ItemStack> ar = super.use(level, player, hand);
        ItemStack stack = ar.getObject();
        if (!(level instanceof ServerLevel serverLevel)) return ar;

        boolean isValidUse = (player.getOffhandItem().is(stack.getItem()) && (player.getMainHandItem().is(stack.getItem())))
                || (player.getOffhandItem().is(stack.getItem()) && player.getMainHandItem().isEmpty())
                || player.getMainHandItem().is(stack.getItem());

        if (!isValidUse) return ar;

        String form = stack.getOrCreateTag().getString("form");
        boolean isTransfurred = ProcessTransfur.isPlayerTransfurred(player);

        if (player.isShiftKeyDown()) {
            if (!form.isEmpty()) {
                stack.getOrCreateTag().putString("form", "");
                if (stack.getOrCreateTag().contains("TransfurVariantData"))
                    stack.getOrCreateTag().remove("TransfurVariantData");
                activateVisuals(level, player, stack, null, 50, SoundEvents.BEACON_DEACTIVATE);
                return ar;
            }

            if (isTransfurred) tryLinkForm(level, player, stack);
            return ar;
        }

        if (form.isEmpty()) {
            player.displayClientMessage(new TextComponent("No form linked, please link one with §e<Shift+Click>"), true);
            return ar;
        }

        if (isTransfurred) {
            SummonDripParticlesProcedure.execute(player);
            PlayerUtil.UnTransfurPlayer(player);
            cooldown(player, stack, 100);
            visualActivate(level, player, ChangedAddonSounds.UNTRANSFUR);
            grantAdvancement(player, "changed_addon:transfur_totem_advancement_1");
            return ar;
        }

        if (form.equals("changed_addon:form_puro_kind/female")) {
            form = "changed_addon:form_latex_puro_kind/female";
            stack.getOrCreateTag().putString("form", form);
        }

        if (stack.getOrCreateTag().contains("TransfurVariantData")) {
            CompoundTag data = stack.getOrCreateTag().getCompound("TransfurVariantData");
            PlayerUtil.TransfurPlayerAndLoadData(player, form, data, 0.85f);
            // 0.85f to avoid issues with the transfur animation and because is design choice
        } else PlayerUtil.TransfurPlayer(player, form, 0.85f);

        activateVisuals(level, player, stack, "changed_addon:transfur_totem_advancement_1", 100, null);


        return ar;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack pStack, @NotNull Player player, @NotNull LivingEntity targetEntity, @NotNull InteractionHand pUsedHand) {
        if (pUsedHand != player.getUsedItemHand()) return InteractionResult.PASS;

        Level level = player.level;

        ItemStack totem = player.getMainHandItem();
        if (!totem.is(ChangedAddonItems.TRANSFUR_TOTEM.get()) || !totem.getOrCreateTag().getString("form").isEmpty())
            totem = player.getOffhandItem();
        if (!totem.is(ChangedAddonItems.TRANSFUR_TOTEM.get()) || !totem.getOrCreateTag().getString("form").isEmpty())
            return InteractionResult.PASS;

        if (player.getCooldowns().isOnCooldown(ChangedAddonItems.TRANSFUR_TOTEM.get()) || !player.isShiftKeyDown())
            return InteractionResult.PASS;

        if (targetEntity instanceof Player target) {
            if (!ProcessTransfur.isPlayerTransfurred(target)) return InteractionResult.PASS;

            String transfurId = ProcessTransfur.getPlayerTransfurVariant(target).getFormId().toString();
            if (ChangedAddonServerConfiguration.ACCEPT_ALL_VARIANTS.get() == false) {
                if (transfurId.startsWith("changed:form")) {
                    cooldown(player, totem, 20);

                    if (level.isClientSide()) Minecraft.getInstance().gameRenderer.displayItemActivation(totem);

                    totem.getOrCreateTag().putString("form", transfurId);

                    level.playSound(null, player, SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 1, 1);
                    return InteractionResult.SUCCESS;
                } else if (transfurId.startsWith("changed_addon:form")) {
                    cooldown(player, totem, 50);

                    if (level.isClientSide()) Minecraft.getInstance().gameRenderer.displayItemActivation(totem);

                    level.playSound(null, player, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.NEUTRAL, 1, 0);

                    if (!target.level.isClientSide())
                        target.displayClientMessage(new TextComponent((new TranslatableComponent("changed_addon.latex_totem.not_valid").getString())), true);

                    return InteractionResult.SUCCESS;
                }
            } else {
                cooldown(player, totem, 20);


                if (level.isClientSide()) Minecraft.getInstance().gameRenderer.displayItemActivation(totem);

                totem.getOrCreateTag().putString("form", transfurId);

                level.playSound(null, player, SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 1, 1);
                return InteractionResult.SUCCESS;
            }
        } else if (targetEntity instanceof ChangedEntity changedEntity) {
            String string = changedEntity.getSelfVariant() != null ? changedEntity.getSelfVariant().getFormId().toString() : "";

            cooldown(player, totem, 20);


            if (level.isClientSide()) Minecraft.getInstance().gameRenderer.displayItemActivation(totem);

            totem.getOrCreateTag().putString("form", string);

            level.playSound(null, player, SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 1, 1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.CONSUME;
    }

    private static void addModifier(LivingEntity entity, Attribute attribute, AttributeModifier modifier) {
        if (!Objects.requireNonNull(entity.getAttribute(attribute)).hasModifier(modifier))
            Objects.requireNonNull(entity.getAttribute(attribute)).addTransientModifier(modifier);
    }

    private static void removeModifier(LivingEntity entity, Attribute attribute, AttributeModifier modifier) {
        if (Objects.requireNonNull(entity.getAttribute(attribute)).hasModifier(modifier))
            Objects.requireNonNull(entity.getAttribute(attribute)).removeModifier(modifier);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemstack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemstack, level, entity, slot, selected);

        if (!(entity instanceof Player player)) return;

        if (player instanceof ServerPlayer serverPlayer) {
            TransfurVariantInstance<?> variant = ProcessTransfur.getPlayerTransfurVariant(player);

            boolean isTransfurred = variant != null;
            boolean holdingTotem = serverPlayer.getMainHandItem().getItem() == ChangedAddonItems.TRANSFUR_TOTEM.get()
                    || serverPlayer.getOffhandItem().getItem() == ChangedAddonItems.TRANSFUR_TOTEM.get();

            if (isTransfurred && holdingTotem) {
                addModifier(serverPlayer, Attributes.ATTACK_DAMAGE, TOTEM_BUFF_ATTACK);
                addModifier(serverPlayer, Attributes.ARMOR, TOTEM_BUFF_DEFENSE);
                addModifier(serverPlayer, Attributes.ARMOR_TOUGHNESS, TOTEM_BUFF_ARMOR);
            } else {
                removeModifier(serverPlayer, Attributes.ATTACK_DAMAGE, TOTEM_BUFF_ATTACK);
                removeModifier(serverPlayer, Attributes.ARMOR, TOTEM_BUFF_DEFENSE);
                removeModifier(serverPlayer, Attributes.ARMOR_TOUGHNESS, TOTEM_BUFF_ARMOR);
            }
        }

        if (!player.getMainHandItem().is(itemstack.getItem()) && !player.getOffhandItem().is(itemstack.getItem()))
            return;


        if (player.getCooldowns().isOnCooldown(itemstack.getItem()) || !ProcessTransfur.isPlayerTransfurred(player)
                || !ProcessTransfur.getPlayerTransfurVariant(player).is(ChangedTransfurVariants.LATEX_BENIGN_WOLF.get()))
            return;

        SummonDripParticlesProcedure.execute(entity);
        PlayerUtil.UnTransfurPlayer(player);

        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, SoundSource.NEUTRAL, 1, 1);

        player.getCooldowns().addCooldown(itemstack.getItem(), 100);
        if (level.isClientSide()) Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);

        if (entity instanceof ServerPlayer _player) {
            player.displayClientMessage(new TextComponent("The totem you were carrying has been activated"), true);

            Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("changed_addon:transfur_totem_advancement_2"));
            AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
            if (!_ap.isDone()) for (String s : _ap.getRemainingCriteria()) _player.getAdvancements().award(_adv, s);
        }
    }

    public static float itemPropertyFunc(Entity entity) {
        if (!(entity instanceof Player player)) return 0;

        var instance = ProcessTransfur.getPlayerTransfurVariant(player);
        if (instance == null || !instance.is(ChangedTransfurVariants.LATEX_BENIGN_WOLF)) return 0;

        return 0.5f;
    }

    @Mod.EventBusSubscriber
    public static class TransfurTotemItemIsStruckByLighting {

        @SubscribeEvent
        public static void onLightning(EntityStruckByLightningEvent event) {
            if (!(event.getEntity() instanceof ItemEntity itemEntity)) return;

            if (itemEntity.getItem().is(ChangedAddonItems.TRANSFUR_TOTEM.get())) event.setCanceled(true);
        }
    }
}
