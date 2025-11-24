package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.foxyas.changedaddon.variant.ChangedAddonTransfurVariants;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.item.SpecializedItemRendering;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;


public class FoxtaItem extends BlockItem implements SpecializedItemRendering {

    private static final ModelResourceLocation GUIMODEL =
            new ModelResourceLocation(ChangedAddonMod.resourceLoc("foxta_gui"), "inventory");
    private static final ModelResourceLocation HANDMODEL =
            new ModelResourceLocation(ChangedAddonMod.resourceLoc("foxta_hand"), "inventory");
    private static final ModelResourceLocation GROUNDMODEL =
            new ModelResourceLocation(ChangedAddonMod.resourceLoc("foxta_ground"), "inventory");

    public FoxtaItem() {
        super(ChangedAddonBlocks.FOXTA_CAN.get(), new Item.Properties()
                ////.tab(ChangedAddonTabs.CHANGED_ADDON_MAIN_TAB)

                .stacksTo(64)
                .rarity(Rarity.RARE)
                .food(new FoodProperties.Builder()
                        .nutrition(9)
                        .saturationMod(1f)
                        .alwaysEat()
                        .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 15, 2), 0.25F)
                        .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 20 * 15, 1), 0.25F)
                        .build()));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemstack) {
        return UseAnim.DRINK;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext pContext) {
        if (pContext.getPlayer() != null && pContext.getPlayer().isShiftKeyDown()) {
            return super.useOn(pContext);
        } else {
            InteractionResult result = this.use(pContext.getLevel(), pContext.getPlayer(), pContext.getHand()).getResult();
            return result == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : result;
        }
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemstack, @NotNull Level world, @NotNull LivingEntity entity) {
        ItemStack retval = super.finishUsingItem(itemstack, world, entity);
        if (entity.level().random.nextFloat() <= 0.001f) {
            ProcessTransfur.progressTransfur(entity, 15, ChangedAddonTransfurVariants.FOXTA_FOXY.get(), TransfurContext.hazard(TransfurCause.FACE_HAZARD));
        }
        return retval;
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack itemStack, ItemDisplayContext type) {
        return type == ItemDisplayContext.GUI || type == ItemDisplayContext.FIXED ? GUIMODEL
                : type == ItemDisplayContext.GROUND ? GROUNDMODEL : HANDMODEL;
    }

    @Override
    public void loadSpecialModels(Consumer<ResourceLocation> consumer) {
        consumer.accept(GUIMODEL);
        consumer.accept(HANDMODEL);
        consumer.accept(GROUNDMODEL);
    }
}
