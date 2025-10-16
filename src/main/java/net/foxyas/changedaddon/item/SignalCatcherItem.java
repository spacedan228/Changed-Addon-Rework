package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonBlocks;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SignalCatcherItem extends Item {

    private static final int LARGE_SEARCH_RADIUS = 121;
    private static final int SMALL_SEARCH_RADIUS = 33;
    private static final int MAX_FOUND_BLOCKS = 10;

    public SignalCatcherItem() {
        super(new Item.Properties().tab(ChangedAddonTabs.TAB_CHANGED_ADDON).stacksTo(64).rarity(Rarity.COMMON));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemstack) {
        return 15;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player entity, @NotNull InteractionHand hand) {
        InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        entity.startUsingItem(hand);
        return ar;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if(!isSignalCatcherInHand(entity)) return stack;

        Player player = (Player) entity;
        if (!player.getCooldowns().isOnCooldown(ChangedAddonItems.SIGNAL_CATCHER.get())) {
            int radius = player.isShiftKeyDown() ? LARGE_SEARCH_RADIUS : SMALL_SEARCH_RADIUS;
            int cooldown = player.isShiftKeyDown() ? 225 : 75;
            searchSignalBlockUsingChunks(level, player, stack, radius, cooldown);
        }

        return stack;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack itemstack, @NotNull Level world, @NotNull LivingEntity entity, int time) {
        if (!itemstack.getOrCreateTag().getBoolean("set")) {
            if (entity instanceof Player player && !player.level.isClientSide())
                player.displayClientMessage(new TextComponent("§o§bNo Location Found §l[Not Close Enough]"), false);
        }
    }

    private static boolean isSignalCatcherInHand(LivingEntity entity) {
        return entity.getMainHandItem().is(ChangedAddonItems.SIGNAL_CATCHER.get()) || entity.getOffhandItem().is(ChangedAddonItems.SIGNAL_CATCHER.get());
    }

    private static void searchSignalBlockUsingChunks(Level level, Player player, ItemStack itemstack, int radius, int cooldown) {
        List<BlockPos> foundPositions = new ArrayList<>();
        int chunkRadius = (radius >> 4) + 1; // Raio em chunks (16 blocos por chunk)
        int x, y, z;
        x = player.getBlockX();
        y = player.getBlockY();
        z = player.getBlockZ();

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        for (int cx = chunkX - chunkRadius; cx <= chunkX + chunkRadius; cx++) {
            for (int cz = chunkZ - chunkRadius; cz <= chunkZ + chunkRadius; cz++) {
                if (foundPositions.size() >= MAX_FOUND_BLOCKS) break;

                LevelChunk chunk = level.getChunk(cx, cz);
                chunk.getBlockEntities().forEach((BlockPos, entity) -> {
                    // Verifique se a posição está no raio
                    if (entity.getBlockState().getBlock() == ChangedAddonBlocks.SIGNAL_BLOCK.get() &&
                            BlockPos.distSqr(new Vec3i(x, y, z)) <= radius * radius) {
                        foundPositions.add(BlockPos);
                    }
                });
            }
        }

        // Resultado da busca
        if (!foundPositions.isEmpty()) {
            BlockPos firstFound = foundPositions.get(0);
            updatePlayerState(player, itemstack, firstFound.getX(), firstFound.getY(), firstFound.getZ(), cooldown);
            if (player.level.isClientSide()) {
                displayFoundLocations(player, foundPositions);
            }

            level.playSound(null, firstFound, SoundEvents.CONDUIT_DEACTIVATE, SoundSource.BLOCKS, 1.5f, 1);
            level.playSound(null, player, SoundEvents.CONDUIT_ACTIVATE, SoundSource.PLAYERS, 1.5f, 1);
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
}
