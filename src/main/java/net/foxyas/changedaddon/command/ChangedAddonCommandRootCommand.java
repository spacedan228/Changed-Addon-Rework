package net.foxyas.changedaddon.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ChangedAddonCommandRootCommand {

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("changed-addon")
                .requires(stack -> stack.getEntity() instanceof Player)
                .then(Commands.literal("toggle_reset_transfur_advancement")
                        .then(Commands.literal("info")
                                .executes(arguments -> {
                                    Player player = arguments.getSource().getPlayerOrException();

                                    ChangedAddonVariables.PlayerVariables vars = ChangedAddonVariables.of(player);
                                    if (vars == null) return 0;

                                    player.displayClientMessage(new TextComponent(("reset transfur progress is " + vars.resetTransfurAdvancements)), true);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .then(Commands.argument("turn", BoolArgumentType.bool())
                                .executes(arguments -> {
                                    Player player = arguments.getSource().getPlayerOrException();

                                    ChangedAddonVariables.PlayerVariables vars = ChangedAddonVariables.of(player);
                                    if (vars == null) return 0;

                                    boolean newVal = BoolArgumentType.getBool(arguments, "turn");

                                    if (newVal == vars.resetTransfurAdvancements) {
                                        player.displayClientMessage(new TextComponent("§cNothing changed, it already had that value"), false);
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    player.displayClientMessage(new TextComponent("You " + (newVal ? "Activated" : "Disabled") + " the Transfur Reset Achievements"), false);

                                    vars.resetTransfurAdvancements = newVal;
                                    vars.syncPlayerVariables(player);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("toggle_addon_warns")
                        .requires(stack -> stack.getEntity() instanceof Player)
                        .then(Commands.argument("warns", BoolArgumentType.bool())
                                .executes(arguments -> {
                                    Player player = arguments.getSource().getPlayerOrException();

                                    ChangedAddonVariables.PlayerVariables vars = ChangedAddonVariables.of(player);
                                    if (vars == null) return 0;

                                    vars.showWarns = BoolArgumentType.getBool(arguments, "warns");
                                    vars.syncPlayerVariables(player);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .then(Commands.literal("info")
                                .executes(arguments -> {
                                    Player player = arguments.getSource().getPlayerOrException();

                                    ChangedAddonVariables.PlayerVariables vars = ChangedAddonVariables.of(player);
                                    if (vars == null) return 0;

                                    player.displayClientMessage(new TextComponent("Warns is §4" + vars.showWarns), true);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("Size_Manipulator")
                        .then(Commands.argument("size", FloatArgumentType.floatArg())
                                .executes(arguments -> {
                                    Player player = (Player) arguments.getSource().getEntityOrException();

                                    float newSize = getSize(FloatArgumentType.getFloat(arguments, "size"), true);
                                    Changed.config.client.basicPlayerInfo.setSize(newSize); // Change Size
                                    ChangedAddonMod.LOGGER.info("Size changed to: {} for player: {}", newSize, player.getName().getString()); // Command Classic Log
                                    player.displayClientMessage(new TextComponent("Size changed to: " + newSize), false); // Chat log for the player

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
        );
    }

    private static float getSize(float size, boolean overrideSize) {
        float SIZE_TOLERANCE = BasicPlayerInfo.getSizeTolerance();
        if (size < 1.0f - SIZE_TOLERANCE) {
            ChangedAddonMod.LOGGER.atWarn().log("Size value is too low: {}, The Size Value is going to be auto set to 0.95", size); // Too Low Warn
        } else if (size > 1.0f + SIZE_TOLERANCE) {
            ChangedAddonMod.LOGGER.atWarn().log("Size value is too high: {}, The Size Value is going to be auto set to 1.05", size); // Too High Warn
        }
        return overrideSize ? Mth.clamp(size, 1.0f - SIZE_TOLERANCE, 1.0f + SIZE_TOLERANCE) : size;

        /*
         * if(newSize < 1.0f - SIZE_TOLERANCE) {
         *		player.displayClientMessage(new TextComponent ("Size value is too low: " + newSize + ", The Size Value is going to be auto set to 0.95"),true);
         *	} else if (newSize > 1.0f + SIZE_TOLERANCE) {
         *		player.displayClientMessage(new TextComponent ("Size value is too high: " + newSize + ", The Size Value is going to be auto set to 1.05"),true);
         *	}
         */
    }
}
