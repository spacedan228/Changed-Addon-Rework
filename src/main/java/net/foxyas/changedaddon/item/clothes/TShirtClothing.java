package net.foxyas.changedaddon.item.clothes;

import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

public class TShirtClothing extends DyeableClothingItem {

    public TShirtClothing() {
        super();
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        this.setColor(stack, 0xffffff);
        return stack;
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientInitializer {
        @SubscribeEvent
        public static void onItemColorsInit(RegisterColorHandlersEvent.Item event) {
            event.register(
                    (stack, layer) -> ((DyeableLeatherItem) stack.getItem()).getColor(stack),
                    ChangedAddonItems.DYEABLE_TSHIRT.get());
        }
    }
}