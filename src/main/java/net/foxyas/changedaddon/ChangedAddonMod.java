package net.foxyas.changedaddon;

import net.foxyas.changedaddon.init.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventBusInvokeDispatcher;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod("changed_addon")
public class ChangedAddonMod {
    public static final Logger LOGGER = LogManager.getLogger(ChangedAddonMod.class);
    public static final String MODID = "changed_addon";
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(ChangedAddonMod.resourceLoc(MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int messageID = 0;

    public ChangedAddonMod() {
        ChangedAddonTabs.load();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ChangedAddonBlocks.REGISTRY.register(bus);
        ChangedAddonPaintingTypes.PAINTING_TYPES.register(bus);
        ChangedAddonItems.REGISTRY.register(bus);
        ChangedAddonEntities.REGISTRY.register(bus);
        ChangedAddonBlockEntities.REGISTRY.register(bus);
        ChangedAddonFeatures.REGISTRY.register(bus);
        ChangedAddonEnchantments.REGISTRY.register(bus);
        ChangedAddonMobEffects.REGISTRY.register(bus);
        ChangedAddonPotions.REGISTRY.register(bus);
        ChangedAddonAnimationEvents.REGISTRY.register(bus);

        ChangedAddonParticleTypes.REGISTRY.register(bus);
        ChangedAddonVillagerProfessions.PROFESSIONS.register(bus);
        ChangedAddonFluids.REGISTRY.register(bus);
    }

    //Thanks :D
    public static ResourceLocation resourceLoc(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static String resourceLocString(String path) {
        return new ResourceLocation(MODID, path).toString();
    }

    public static ResourceLocation textureLoc(String path) {
        return new ResourceLocation(MODID, path + ".png");
    }

    public static ModelLayerLocation layerLocation(String path, String layer) {
        return new ModelLayerLocation(resourceLoc(path), layer);
    }

    public static <T extends Event> boolean postEvent(T event) {
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static <T extends Event> boolean postEvent(T event, IEventBusInvokeDispatcher dispatcher) {
        return MinecraftForge.EVENT_BUS.post(event, dispatcher);
    }

    public static <T extends Event & IModBusEvent> void postModLoadingEvent(T event) {
        ModLoader.get().postEvent(event);
    }

    public static <T extends Event> void addEventListener(Consumer<T> listener) {
        MinecraftForge.EVENT_BUS.addListener(listener);
    }



    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        messageID++;
    }

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer, NetworkDirection direction) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer, Optional.of(direction));
        messageID++;
    }
}
