package net.foxyas.changedaddon.fluid;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonFluids;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class LitixCamoniaFluid extends ForgeFlowingFluid {

    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
            ChangedAddonFluids.LITIX_CAMONIA_FLUID_TYPE,
            ChangedAddonFluids.LITIX_CAMONIA_FLUID,
            ChangedAddonFluids.FLOWING_LITIX_CAMONIA_FLUID)
            .explosionResistance(100f)
            .slopeFindDistance(2)
            .bucket(ChangedAddonItems.LITIX_CAMONIA_FLUID_BUCKET)
            .block(ChangedAddonBlocks.LITIX_CAMONIA_FLUID);

    private LitixCamoniaFluid() {
        super(PROPERTIES);
    }

    public static class FluidType extends net.minecraftforge.fluids.FluidType {

        public FluidType() {
            super(Properties.create()
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                    .canExtinguish(true)
                    .supportsBoating(true)
                    .canDrown(true)
                    .canSwim(true)
                    .fallDistanceModifier(0)
            );
        }

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {
                private static final ResourceLocation FLUID_STILL = ChangedAddonMod.resourceLoc("block/ammoniafluid");
                private static final ResourceLocation FLUID_FLOWING = ChangedAddonMod.resourceLoc("block/ammoniafluid");

                public ResourceLocation getStillTexture() {
                    return FLUID_STILL;
                }

                public ResourceLocation getFlowingTexture() {
                    return FLUID_FLOWING;
                }
            });
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
