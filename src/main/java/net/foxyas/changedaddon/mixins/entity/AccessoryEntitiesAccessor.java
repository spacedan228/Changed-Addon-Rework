package net.foxyas.changedaddon.mixins.entity;

import com.google.common.collect.Multimap;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.entity.AccessoryEntities;
import net.ltxprogrammer.changed.util.Cacheable;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AccessoryEntities.class, remap = false)
public interface AccessoryEntitiesAccessor {

    @Accessor("validEntities")
    Cacheable<Multimap<EntityType<?>, AccessorySlotType>> getValidEntities();
}
