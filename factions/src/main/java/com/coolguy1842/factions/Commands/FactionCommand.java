package com.coolguy1842.factions.Commands;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;

import com.coolguy1842.factions.SubCommands.FactionsSubCommands;
import com.coolguy1842.factions.interfaces.Subcommand;

public class FactionCommand {
    public static void register(CommandManager<CommandSender> commandManager) {
        for(Subcommand subcommand : FactionsSubCommands.subcommands) {
            commandManager.command(subcommand.getCommand(commandManager.commandBuilder("faction", "f")));
        }
    }
}
