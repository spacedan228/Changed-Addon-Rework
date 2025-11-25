package net.foxyas.changedaddon.client.renderer.items;

import net.foxyas.changedaddon.item.LaserPointer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.registries.RegistryObject;

public class LaserItemDynamicRender {

    public static void DynamicLaserColor(RegistryObject<LaserPointer> item) {
        Minecraft.getInstance().getItemColors().register(
                (stack, tintIndex) -> {
                    if (tintIndex == 0) { // Só aplica a cor no layer certo
                        if (LaserPointer.getAWTColor(stack) == null) {
                            return -1; // Cor padrão (branco)
                        }
                        return LaserPointer.getAWTColor(stack).getRGB();
                    }
                    return -1; // Cor padrão (branco)
                },
                item.get()
        );
    }
}
