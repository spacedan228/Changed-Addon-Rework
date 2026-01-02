package net.foxyas.changedaddon.mixins.enchantments;

import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Enchantment.class, priority = 1001)
public class EnchantmentMixin {

    @Unique
    private Enchantment self() {
        return (Enchantment) (Object) this;
    }
}
