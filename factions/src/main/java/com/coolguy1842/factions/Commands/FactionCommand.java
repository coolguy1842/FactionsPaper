package com.coolguy1842.factions.Commands;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.Command.Builder;

import com.coolguy1842.factions.SubCommands.FactionsSubCommands;
import com.coolguy1842.factions.interfaces.Subcommand;

public class FactionCommand {
    public static void register(CommandManager<CommandSender> commandManager) {
        for(Subcommand subcommand : FactionsSubCommands.subcommands) {
            for(Builder<CommandSender> command : subcommand.getCommands(commandManager.commandBuilder("faction", "f"))) {
                commandManager.command(command);
            }
        }
    }
}
