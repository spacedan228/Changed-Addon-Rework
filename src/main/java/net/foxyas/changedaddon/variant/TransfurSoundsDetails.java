package net.foxyas.changedaddon.variant;

import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.foxyas.changedaddon.util.ComponentUtil;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static net.foxyas.changedaddon.util.PlayerUtil.*;
import static net.foxyas.changedaddon.variant.TransfurSoundsDetails.TransfurSoundType.*;

public class TransfurSoundsDetails {

    public static void tryPlay(Player player, TransfurSoundAction action, boolean warnPlayer) {


        if (!action.canUse(player)) {
            if (warnPlayer) {
                player.sendMessage(
                        ComponentUtil.literal("Your current form can't do that."), Util.NIL_UUID
                );
            }
            return;
        }


        if (ChangedAddonVariables.nonNullOf(player).actCooldown) {
            if (warnPlayer) {
                player.sendMessage(
                        ComponentUtil.literal("You're too exhausted to do that."), Util.NIL_UUID
                );
            }
            return;
        }


        SoundEvent sound = getSoundFor(player, action);
        if (sound == null) return;


        player.level.playSound(
                null,
                player.blockPosition(),
                sound,
                SoundSource.PLAYERS,
                1.0f,
                getPitch(player, action)
        );


        ChangedAddonVariables.nonNullOf(player).actCooldown = true;
    }

    private static float getPitch(Player player, TransfurSoundAction action) {
        return switch (action) {
            case YIP, CHATTER -> Mth.nextFloat(player.getRandom(), 1.2f, 1.6f);
            case FOX_SCREAM -> 0.8f;
            default -> 1.0f;
        };
    }

    public static SoundEvent getSoundFor(Player player, TransfurSoundAction action) {
        return switch (action) {


            // 🐶 Wolf/Dogs
            case BARK -> SoundEvents.WOLF_AMBIENT;
            case GROWL -> SoundEvents.WOLF_GROWL;
            case WHINE -> SoundEvents.WOLF_WHINE;
            case HOWL -> SoundEvents.WOLF_HOWL;


            // 🐱 Cats
            case MEOW -> SoundEvents.CAT_AMBIENT;
            case HISS -> SoundEvents.CAT_HISS;


            // 🦁 Big cats
            case ROAR -> ChangedSounds.MONSTER2;


            // 🦊 Fox
            case YIP -> SoundEvents.FOX_AMBIENT;
            case CHATTER -> SoundEvents.FOX_AGGRO;
            case FOX_SCREAM -> SoundEvents.FOX_SCREECH;
        };
    }

    public enum TransfurSoundType {
        CAT,
        DOG,
        WOLF,
        FOX,
        BIG_CAT;


        public static Set<TransfurSoundType> getSoundTypes(Player player) {
            Set<TransfurSoundType> types = EnumSet.noneOf(TransfurSoundType.class);


            if (isCatTransfur(player)) types.add(CAT);
            if (isWolfTransfur(player)) {
                types.add(WOLF);
                types.add(DOG);
            }
            if (isFoxTransfur(player)) types.add(FOX);
            if (canRoar(player)) types.add(BIG_CAT);


            return types;
        }

    }

    public enum TransfurSoundAction {

        MEOW(20, CAT, "meow", "miau"),
        HISS(40, CAT, "hiss"),

        BARK(10, DOG, WOLF, "bark"),
        GROWL(60, DOG, WOLF, "growl", "grr"),
        HOWL(60, DOG, WOLF, "howl", "awoo"),
        WHINE(30, DOG, "whine"),

        YIP(15, FOX, "yip"),
        CHATTER(25, FOX, "chatter"),
        FOX_SCREAM(40, FOX, "scream"),

        ROAR(80, BIG_CAT, "roar", "rawr");

        private final int cooldown;
        private final Set<TransfurSoundType> allowed;
        private final Set<String> chatMatches;

        TransfurSoundAction(
                int cooldown,
                TransfurSoundType[] types,
                String... chatMatches
        ) {
            this.cooldown = cooldown;
            this.allowed = EnumSet.copyOf(List.of(types));
            this.chatMatches = Set.of(chatMatches);
        }

        // 🔹 compatível com seu construtor atual
        TransfurSoundAction(
                int cooldown,
                TransfurSoundType type,
                String... chatMatches
        ) {
            this(cooldown, new TransfurSoundType[]{type}, chatMatches);
        }

        TransfurSoundAction(
                int cooldown,
                TransfurSoundType type1,
                TransfurSoundType type2,
                String... chatMatches
        ) {
            this(cooldown, new TransfurSoundType[]{type1, type2}, chatMatches);
        }

        public boolean canUse(Player player) {
            return getSoundTypes(player)
                    .stream()
                    .anyMatch(allowed::contains);
        }
        
        public boolean matchesChat(String text) {
            text = text.toLowerCase();
            for (String match : chatMatches) {
                if (text.contains(match)) {
                    return true;
                }
            }
            return false;
        }

        public int getCooldown() {
            return cooldown;
        }

        public String getFormatedName() {
            String[] parts = this.name().toLowerCase().split("_");
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (part.isEmpty()) continue;

                result.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1));

                if (i < parts.length - 1) {
                    result.append(" ");
                }
            }
            return result.toString();
        }
    }
}