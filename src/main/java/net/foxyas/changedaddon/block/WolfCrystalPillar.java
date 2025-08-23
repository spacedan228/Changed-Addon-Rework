package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.ltxprogrammer.changed.block.NonLatexCoverableBlock;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class WolfCrystalPillar extends RotatedPillarBlock implements NonLatexCoverableBlock {

    public WolfCrystalPillar() {
        super(Properties.of(Material.ICE_SOLID)
                .friction(0.98F)
                .sound(SoundType.AMETHYST)
                .strength(2.0F, 2.0F).noOcclusion().requiresCorrectToolForDrops());
    }

    public static void aaa(){
        ItemBlockRenderTypes.setRenderLayer(ChangedAddonBlocks.WOLF_CRYSTAL_PILLAR.get(), renderType -> renderType == RenderType.translucent());
    }
}
