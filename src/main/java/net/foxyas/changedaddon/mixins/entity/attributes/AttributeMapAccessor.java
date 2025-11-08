package net.foxyas.changedaddon.mixins.entity.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AttributeMap.class)
public interface AttributeMapAccessor {

    @Accessor("supplier")
    AttributeSupplier getSupplier();

}
