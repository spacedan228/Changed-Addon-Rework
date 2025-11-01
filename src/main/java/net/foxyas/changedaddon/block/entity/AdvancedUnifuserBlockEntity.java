package net.foxyas.changedaddon.block.entity;

import net.foxyas.changedaddon.init.ChangedAddonBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class AdvancedUnifuserBlockEntity extends UnifuserBlockEntity {

    public AdvancedUnifuserBlockEntity(BlockPos position, BlockState state) {
        super(ChangedAddonBlockEntities.ADVANCED_UNIFUSER.get(), position, state);
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }

    @Override
    public @NotNull Component getDefaultName() {
        return new TextComponent("advanced_unifuser");
    }

    @Override
    public @NotNull Component getDisplayName() {
        return new TextComponent("Advanced Unifuser");
    }
}
