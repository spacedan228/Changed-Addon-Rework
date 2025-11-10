package net.foxyas.changedaddon.block;

import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.ForgeSoundType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class GooCoreBlock extends Block {

    public GooCoreBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE)
                .sound(new ForgeSoundType(1.0f, 1.0f, () -> SoundEvents.STONE_BREAK, () -> SoundEvents.SCULK_SENSOR_STEP,
                        () -> SoundEvents.SCULK_SENSOR_PLACE, () -> SoundEvents.SCULK_SENSOR_HIT, () -> SoundEvents.STONE_FALL))
                .strength(20f, 5f).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderLayer() {
        ItemBlockRenderTypes.setRenderLayer(ChangedAddonBlocks.GOO_CORE.get(), renderType -> renderType == RenderType.cutout());
    }

    @Override
    public void appendHoverText(ItemStack itemstack, @Nullable BlockGetter world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, world, list, flag);
        list.add(new TextComponent("A Gooey Core which appears to be covered in obsidian"));
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 15;
    }
}
