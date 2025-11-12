package net.foxyas.changedaddon.item;

import net.foxyas.changedaddon.init.ChangedAddonTabs;
import net.ltxprogrammer.changed.block.KeypadBlock;
import net.ltxprogrammer.changed.block.entity.KeypadBlockEntity;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.ChatFormatting;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeycardItem extends Item {

    public static final byte[] DEFAULT_CODE = {};

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
        setCode(defaultInstance, DEFAULT_CODE);
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
        if (itemCode == null || Arrays.equals(itemCode, DEFAULT_CODE)) {
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

}
