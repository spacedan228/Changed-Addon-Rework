package net.foxyas.changedaddon.mixins.client;

import net.foxyas.changedaddon.client.model.clothes.PlayerModelVisibilityModifier;
import net.foxyas.changedaddon.mixins.client.renderer.LivingEntityRendererAccessor;
import net.ltxprogrammer.changed.client.renderer.accessory.AccessoryRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.AccessoryLayer;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {

    /**
     * Inject before the player renders to adjust model visibility.
     */
    @Inject(method = "setModelProperties", at = @At("RETURN"))
    private void changedaddon$applyModelVisibilityModifiers(AbstractClientPlayer pClientPlayer, CallbackInfo ci) {
        PlayerRenderer thisFix = (PlayerRenderer) (Object) this;
        List<RenderLayer<LivingEntity, EntityModel<LivingEntity>>> layers = ((LivingEntityRendererAccessor) thisFix).getLayers();
        layers.forEach((livingEntityEntityModelRenderLayer -> {
            if (livingEntityEntityModelRenderLayer instanceof AccessoryLayer<LivingEntity, EntityModel<LivingEntity>>) {
                for (EquipmentSlot slot : Arrays.stream(EquipmentSlot.values()).filter((equipmentSlot) -> equipmentSlot.getType() == EquipmentSlot.Type.ARMOR).toList()) {
                    AccessorySlots accessorySlots = AccessorySlots.getForEntity(pClientPlayer).get();
                    List<AccessorySlotType> list = accessorySlots.getSlotTypes().filter((slotType) -> slotType.getEquivalentSlot() == slot).toList();
                    for (AccessorySlotType slotType : list) {
                        Optional<ItemStack> item = accessorySlots.getItem(slotType);
                        ItemStack itemStack = item.isPresent() ? item.get() : ItemStack.EMPTY;
                        Optional<AccessoryRenderer> renderer = AccessoryLayer.getRenderer(itemStack.getItem());
                        renderer.ifPresent((accessoryRenderer) -> {
                            if (accessoryRenderer instanceof PlayerModelVisibilityModifier modifier) {
                                if (modifier.isVisible()) {
                                    modifier.setPlayerModelProperties(pClientPlayer, thisFix.getModel());
                                }
                            }
                        });
                    }

                }
            }
        }));
    }
}
