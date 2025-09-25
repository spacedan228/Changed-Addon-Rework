package net.foxyas.changedaddon.item.tooltip;

import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.item.Syringe;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class TransfurTotemTooltipComponent implements TooltipComponent {
    private final TransfurVariant<?> variant;
    private final ItemStack transfurTotemStack;

    public TransfurTotemTooltipComponent(TransfurVariant<?> variant, ItemStack transfurTotemStack) {
        this.variant = variant;
        this.transfurTotemStack = transfurTotemStack;
    }

    public TransfurTotemTooltipComponent(ItemStack transfurTotemStack) {
        this.variant = Syringe.getVariant(transfurTotemStack);
        this.transfurTotemStack = transfurTotemStack;
    }

    @Nullable
    public TransfurVariant<?> getVariant() {
        return this.variant;
    }

    public ItemStack getTransfurTotemStack() {
        return transfurTotemStack;
    }
}