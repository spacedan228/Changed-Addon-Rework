package net.foxyas.changedaddon.block;

import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class DormantDarkLatexBlock extends AbstractLatexBlock {

    public DormantDarkLatexBlock() {
        super(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.COLOR_GRAY).sound(SoundType.SLIME_BLOCK).noDrops()
                .strength(1.0F, 4.0F), LatexType.DARK_LATEX, ChangedItems.DARK_LATEX_GOO);
    }
}
