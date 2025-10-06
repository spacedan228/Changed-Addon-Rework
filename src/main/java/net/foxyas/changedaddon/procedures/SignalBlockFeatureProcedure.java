package net.foxyas.changedaddon.procedures;

import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SignalBlockFeatureProcedure {

    private static final int LARGE_SEARCH_RADIUS = 121;
    private static final int SMALL_SEARCH_RADIUS = 33;
    private static final int MAX_FOUND_BLOCKS = 10;

    public static void execute(LevelAccessor world, Entity entity, ItemStack itemstack) {
        if (isSignalCatcherInHand(entity)) {
            Player player = (Player) entity;
            if (!player.getCooldowns().isOnCooldown(ChangedAddonItems.SIGNAL_CATCHER.get())) {
                int radius = player.isShiftKeyDown() ? LARGE_SEARCH_RADIUS : SMALL_SEARCH_RADIUS;
                int cooldown = player.isShiftKeyDown() ? 225 : 75;
                searchSignalBlockUsingChunks(world, player.getOnPos(), player, itemstack, radius, cooldown);
            }
        }
    }

    private static boolean isSignalCatcherInHand(Entity entity) {
        LivingEntity livingEntity = (LivingEntity) entity;
        return livingEntity.getMainHandItem().getItem() == ChangedAddonItems.SIGNAL_CATCHER.get() || livingEntity.getOffhandItem().getItem() == ChangedAddonItems.SIGNAL_CATCHER.get();
    }

    private static void searchSignalBlockUsingChunks(LevelAccessor world, BlockPos pos, Player player, ItemStack itemstack, int radius, int cooldown) {
        List<BlockPos> foundPositions = new ArrayList<>();
        int chunkRadius = (radius >> 4) + 1; // Raio em chunks (16 blocos por chunk)
        double x, y, z;
        x = player.getX();
        y = player.getY();
        z = player.getZ();

        int chunkX = (int) x >> 4;
        int chunkZ = (int) z >> 4;

        for (int cx = chunkX - chunkRadius; cx <= chunkX + chunkRadius; cx++) {
            for (int cz = chunkZ - chunkRadius; cz <= chunkZ + chunkRadius; cz++) {
                if (world instanceof Level level) {
                    var chunk = level.getChunk(cx, cz);
                    chunk.getBlockEntities().forEach((BlockPos, entity) -> {
                        // Verifique se a posição está no raio
                        if (entity.getBlockState().getBlock() == ChangedAddonBlocks.SIGNAL_BLOCK.get() &&
                                BlockPos.distSqr(new Vec3i(x, y, z)) <= radius * radius) {
                            foundPositions.add(BlockPos);
                            if (foundPositions.size() >= MAX_FOUND_BLOCKS) {
                                // Limite alcançado
                            }
                        }
                    });
                }
            }
        }

        // Resultado da busca
        if (!foundPositions.isEmpty()) {
            BlockPos firstFound = foundPositions.get(0);
            updatePlayerState(player, itemstack, firstFound.getX(), firstFound.getY(), firstFound.getZ(), cooldown);
            if (player.level.isClientSide()) {
                displayFoundLocations(player, foundPositions);
            }
            playSounds(world, player.getX(), player.getY(), player.getZ(), firstFound);
        } else if (!player.level.isClientSide()) {
            player.displayClientMessage(new TextComponent("No Signal Block Found"), false);
        }
    }

    private static void updatePlayerState(Player player, ItemStack itemstack, int x, int y, int z, int cooldown) {
        itemstack.getOrCreateTag().putDouble("x", x);
        itemstack.getOrCreateTag().putDouble("y", y);
        itemstack.getOrCreateTag().putDouble("z", z);
        itemstack.getOrCreateTag().putBoolean("set", true);
        player.getCooldowns().addCooldown(itemstack.getItem(), cooldown);
    }

    private static void displayFoundLocations(Player player, List<BlockPos> positions) {
        boolean isCreative = player.isCreative();

        player.displayClientMessage(new TextComponent("Signal Blocks found at:"), false); // Mensagem inicial

        for (int i = 0; i < positions.size(); i++) {
            BlockPos pos = positions.get(i);

            // Cria o texto básico da posição
            String positionText = String.format("Block %d: [%d, %d, %d]", i + 1, pos.getX(), pos.getY(), pos.getZ());
            TextComponent message = new TextComponent(positionText);

            if (isCreative) {
                // Adiciona eventos ao texto apenas para jogadores criativos
                Style style = Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/tp %d %d %d", pos.getX(), pos.getY(), pos.getZ())))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Click to copy the teleport command")));

                message.setStyle(style);
            }

            // Envia cada linha individualmente
            player.displayClientMessage(message, false);
        }
    }


    private static void playSounds(LevelAccessor world, double x, double y, double z, BlockPos foundPos) {
        playSound(world, foundPos, "block.conduit.deactivate", SoundSource.BLOCKS, 1.5f);
        playSound(world, new BlockPos(x, y, z), "block.conduit.activate", SoundSource.PLAYERS, 1.5f);
    }

    private static void playSound(LevelAccessor world, BlockPos pos, String soundEvent, SoundSource source, float volume) {
        if (world instanceof Level level) {
            ResourceLocation soundLocation = new ResourceLocation(soundEvent);
            if (!level.isClientSide()) {
                level.playSound(null, pos, Objects.requireNonNull(ForgeRegistries.SOUND_EVENTS.getValue(soundLocation)), source, volume, 0);
            } else {
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), Objects.requireNonNull(ForgeRegistries.SOUND_EVENTS.getValue(soundLocation)), source, volume, 0, false);
            }
        }
    }
}
