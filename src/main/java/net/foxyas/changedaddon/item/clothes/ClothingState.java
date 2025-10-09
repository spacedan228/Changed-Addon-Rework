package net.foxyas.changedaddon.item.clothes;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

public class ClothingState extends StateHolder<SimpleClothingItem, ClothingState> {
    protected ClothingState(SimpleClothingItem item, ImmutableMap<Property<?>, Comparable<?>> properties, MapCodec<ClothingState> codec) {
        super(item, properties, codec);
    }
}
