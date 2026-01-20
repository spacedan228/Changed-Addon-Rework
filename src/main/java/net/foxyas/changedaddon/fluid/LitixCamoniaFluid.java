package net.foxyas.changedaddon.fluid;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonFluids;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;

public abstract class LitixCamoniaFluid extends ForgeFlowingFluid {


    private static final ResourceLocation FLUID_STILL = ChangedAddonMod.resourceLoc("block/litix_camonia_fluid/litix_camonia_fluid_still");
    private static final ResourceLocation FLUID_FLOWING = ChangedAddonMod.resourceLoc("block/litix_camonia_fluid/litix_camonia_fluid_flowing");

    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(ChangedAddonFluids.LITIX_CAMONIA_FLUID,
            ChangedAddonFluids.FLOWING_LITIX_CAMONIA_FLUID,
            FluidAttributes.builder(
                            FLUID_STILL,
                            FLUID_FLOWING
                    )
                    .sound(SoundEvents.BUCKET_EMPTY))
            .explosionResistance(100f)
            .slopeFindDistance(2)
            .bucket(ChangedAddonItems.LITIX_CAMONIA_FLUID_BUCKET)
            .block(ChangedAddonBlocks.LITIX_CAMONIA_FLUID);

    private LitixCamoniaFluid() {
        super(PROPERTIES);
    }

    @Override
    public void tick(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull FluidState pState) {
        super.tick(pLevel, pPos, pState);
        if (pLevel.isClientSide()) return;
        for (Direction value : Direction.values()) {
            BlockPos relative = pPos.relative(value);
            BlockState blockState = pLevel.getBlockState(relative);
            if (blockState.hasProperty(AbstractLatexBlock.COVERED) && blockState.getValue(AbstractLatexBlock.COVERED) != LatexType.NEUTRAL) {
                pLevel.setBlockAndUpdate(relative, blockState.setValue(AbstractLatexBlock.COVERED, LatexType.NEUTRAL));
            }
        }
    }

    public static class Source extends LitixCamoniaFluid {

        public int getAmount(@NotNull FluidState state) {
            return 8;
        }

        public boolean isSource(@NotNull FluidState state) {
            return true;
        }
    }

    public static class Flowing extends LitixCamoniaFluid {

        protected void createFluidStateDefinition(StateDefinition.@NotNull Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(@NotNull FluidState state) {
            return false;
        }
    }
}
