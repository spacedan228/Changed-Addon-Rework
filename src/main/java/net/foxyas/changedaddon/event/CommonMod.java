package net.foxyas.changedaddon.event;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.menu.CustomMerchantMenu;
import net.foxyas.changedaddon.network.*;
import net.foxyas.changedaddon.network.packet.*;
import net.foxyas.changedaddon.network.packet.simple.ServerTellClientRespawn;
import net.foxyas.changedaddon.procedure.blocksHandle.BoneMealExpansion;
import net.foxyas.changedaddon.recipe.brewing.TransfurSicknessRecipeBrewingRecipe;
import net.foxyas.changedaddon.recipe.brewing.UntransfurPotionRecipeBrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

@Mod.EventBusSubscriber(modid = ChangedAddonMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonMod {

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ChangedAddonVariables.PlayerVariables.class);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        addPackets();

        event.enqueueWork(() -> {
            BoneMealExpansion.BoneMealDispenserHandler.registerDispenserBehavior();
            BoneMealExpansion.GooApplyDispenserHandler.registerDispenserBehavior();

            BrewingRecipeRegistry.addRecipe(new UntransfurPotionRecipeBrewingRecipe());
            BrewingRecipeRegistry.addRecipe(new TransfurSicknessRecipeBrewingRecipe());
        });
    }

    private static void addPackets(){
        ChangedAddonMod.addNetworkMessage(KeyPressPacket.class, KeyPressPacket::encode,
                KeyPressPacket::new, KeyPressPacket::handle);
        ChangedAddonMod.addNetworkMessage(SyncTransfurVisionsPacket.class, SyncTransfurVisionsPacket::encode,
                SyncTransfurVisionsPacket::new, SyncTransfurVisionsPacket::handle);

        ChangedAddonMod.addNetworkMessage(RequestMovementCheckPacket.class, RequestMovementCheckPacket::encode,
                RequestMovementCheckPacket::new, RequestMovementCheckPacket::handle);
        ChangedAddonMod.addNetworkMessage(ConfirmMovementPacket.class, ConfirmMovementPacket::encode,
                ConfirmMovementPacket::decode, ConfirmMovementPacket::handle);
        ChangedAddonMod.addNetworkMessage(VariantSecondAbilityActivate.class, VariantSecondAbilityActivate::write,
                VariantSecondAbilityActivate::new, VariantSecondAbilityActivate::handle);

        ChangedAddonMod.addNetworkMessage(ChangedAddonVariables.SyncPacket.class, ChangedAddonVariables.SyncPacket::encode,
                ChangedAddonVariables.SyncPacket::new, ChangedAddonVariables.SyncPacket::handler);

        ChangedAddonMod.addNetworkMessage(GeneratorGuiButtonPacket.class, GeneratorGuiButtonPacket::encode,
                GeneratorGuiButtonPacket::new, GeneratorGuiButtonPacket::handler);

        ChangedAddonMod.addNetworkMessage(OpenExtraDetailsPacket.class, OpenExtraDetailsPacket::encode,
                OpenExtraDetailsPacket::new, OpenExtraDetailsPacket::handler);

        ChangedAddonMod.addNetworkMessage(PatKeyPacket.class, PatKeyPacket::encode, PatKeyPacket::new, PatKeyPacket::handler);

        ChangedAddonMod.addNetworkMessage(TransfurSoundsGuiButtonPacket.class, TransfurSoundsGuiButtonPacket::encode,
                TransfurSoundsGuiButtonPacket::new, TransfurSoundsGuiButtonPacket::handler);

        ChangedAddonMod.addNetworkMessage(TurnOffTransfurPacket.class, TurnOffTransfurPacket::encode,
                TurnOffTransfurPacket::new, TurnOffTransfurPacket::handler);

        ChangedAddonMod.addNetworkMessage(InformantBlockGuiKeyPacket.class, InformantBlockGuiKeyPacket::encode,
                InformantBlockGuiKeyPacket::new, InformantBlockGuiKeyPacket::handle);

        ChangedAddonMod.addNetworkMessage(ServerboundProgressFTKCPacket.class, ServerboundProgressFTKCPacket::encode,
                ServerboundProgressFTKCPacket::new, ServerPacketHandler::handleProgressFTKCPacket,
                NetworkDirection.PLAY_TO_SERVER);

        ChangedAddonMod.addNetworkMessage(ClientboundOpenFTKCScreenPacket.class, ClientboundOpenFTKCScreenPacket::encode,
                ClientboundOpenFTKCScreenPacket::new,
                (packet, contextSupplier) -> ClientPacketHandler.handleOpenFTKCScreenPacket(packet, contextSupplier),
                NetworkDirection.PLAY_TO_CLIENT);

        ChangedAddonMod.addNetworkMessage(ClientboundSonarUpdatePacket.class, ClientboundSonarUpdatePacket::encode,
                ClientboundSonarUpdatePacket::new,
                (packet, contextSupplier) -> ClientPacketHandler.handleSonarUpdatePacket(packet, contextSupplier),
                NetworkDirection.PLAY_TO_CLIENT);

        ChangedAddonMod.addNetworkMessage(ServerboundCustomSelectTradePacket.class, ServerboundCustomSelectTradePacket::encode,
                ServerboundCustomSelectTradePacket::new,
                (packet, context) -> {
                    NetworkEvent.Context ctx = context.get();
                    if(ctx.getSender() == null) return;
                    ctx.enqueueWork(()-> {
                        if (ctx.getSender().containerMenu instanceof CustomMerchantMenu menu) {
                            int i = packet.shopItem();
                            menu.setSelectionHint(i);
                            menu.tryMoveItems(i);
                        }
                    });
                }, NetworkDirection.PLAY_TO_SERVER
        );

        ChangedAddonMod.addNetworkMessage(RespawnAsTransfurMessage.class, RespawnAsTransfurMessage::encode,
                RespawnAsTransfurMessage::new, RespawnAsTransfurMessage::handler,
                NetworkDirection.PLAY_TO_SERVER);


        ChangedAddonMod.addNetworkMessage(ServerTellClientRespawn.class, ServerTellClientRespawn::encode,
                ServerTellClientRespawn::new, ServerTellClientRespawn::handle,
                NetworkDirection.PLAY_TO_CLIENT);
    }
}
