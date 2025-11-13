package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.foxyas.changedaddon.item.api.ColorHolder;
import net.ltxprogrammer.changed.block.KeypadBlock;
import net.ltxprogrammer.changed.block.entity.KeypadBlockEntity;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeycardItem extends Item implements ColorHolder {
    public KeycardItem(Properties pProperties) {
        super(pProperties);
    }

    public KeycardItem() {
        super(new Properties().stacksTo(1).tab(ChangedAddonTabs.TAB_CHANGED_ADDON));
    }

    public static void setCode(ItemStack stack, byte[] code) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putByteArray("StoredCode", code);
    }

    public static byte @Nullable [] getCode(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("StoredCode") ? tag.getByteArray("StoredCode") : null;
    }

    public static int getBottomColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("BottomColor") ? tag.getInt("BottomColor") : 0xFFFFFF; // branco
    }

    public static int getTopColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("TopColor") ? tag.getInt("TopColor") : 0xFFFFFF; // vermelho
    }

    public static void setBottomColor(ItemStack stack, int color) {
        stack.getOrCreateTag().putInt("BottomColor", color);
    }

    public static void setTopColor(ItemStack stack, int color) {
        stack.getOrCreateTag().putInt("TopColor", color);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        byte[] code = getCode(stack);
        if (code != null) {
            tooltip.add(new TranslatableComponent("item.changed_addon.keycard.desc.code", Arrays.toString(code))
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(new TranslatableComponent("item.changed_addon.keycard.desc.nocode")
                    .withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack defaultInstance = super.getDefaultInstance();
        setTopColor(defaultInstance, -1);
        setBottomColor(defaultInstance, new Color(255, 0, 0).getRGB());
        return defaultInstance;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack itemInHand = context.getItemInHand();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState blockState = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (player == null) return InteractionResult.PASS;

        if (!(blockState.getBlock() instanceof KeypadBlock keypadBlock) ||
                !(blockEntity instanceof KeypadBlockEntity keypadEntity))
            return InteractionResult.PASS;

        byte[] itemCode = getCode(itemInHand);
        if (itemCode == null) {
            if (keypadEntity.code != null && player.isShiftKeyDown()) {
                setCode(itemInHand, keypadEntity.code);
                playWrite(level, pos);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            return InteractionResult.PASS;
        }

        // Converte o código para lista de bytes (se o keypad usar isso)
        List<Byte> codeList = new ArrayList<>();
        for (byte b : itemCode)
            codeList.add(b);

        // Agora insere o código automaticamente no keypad
        if (!level.isClientSide) {
            keypadEntity.useCode(codeList);
            player.displayClientMessage(
                    new TranslatableComponent("item.changed_addon.keycard.message.used.success").withStyle(ChatFormatting.GREEN),
                    true
            );
        }

        return InteractionResult.SUCCESS;
    }

    private static void playLock(Level level, BlockPos pos) {
        playSound(level, pos, ChangedSounds.KEY, 1.0F, 1.0F);
    }

    private static void playWrite(Level level, BlockPos pos) {
        playSound(level, pos, ChangedSounds.CHIME2, 1.0F, 1.0F);
    }

    private static void playUnlock(Level level, BlockPos pos) {
        playSound(level, pos, ChangedSounds.CHIME2, 1.0F, 1.0F);
    }


    private static void playSound(Level level, BlockPos worldPosition, SoundEvent event, float volume, float pitch) {
        if (level.getServer() != null) {
            ChangedSounds.broadcastSound(level.getServer(), event, worldPosition, volume, pitch);
        }

    }

    @Override
    public void registerCustomColors(ItemColors itemColors, RegistryObject<Item> item) {
        itemColors.register(
                (stack, tintIndex) -> {
                    /*
                     * tintIndex 0 = layer0 (base/letters part)
                     * tintIndex 1 = layer1 (top)
                     * tintIndex 2 = layer2 (bottom)
                     */

                    if (tintIndex == 2) {
                        // Cor da parte de baixo
                        return getBottomColor(stack);
                    } else if (tintIndex == 1) {
                        // Cor da parte de cima
                        return getTopColor(stack);
                    }

                    return -1; // fallback default
                },
                item.get()
        );
    }
}
