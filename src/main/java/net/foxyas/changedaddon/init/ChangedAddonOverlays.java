package net.foxyas.changedaddon.init;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.client.gui.overlays.HazardSuitHelmetOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = {Dist.CLIENT})
public class ChangedAddonOverlays {

    public static final IIngameOverlay GRABBED_ELEMENT = OverlayRegistry.registerOverlayBottom(ChangedAddonMod.resourceLocString("hazard_helmet"), HazardSuitHelmetOverlay::renderHelmetOverlay);

}
