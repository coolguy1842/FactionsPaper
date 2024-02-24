package com.coolguy1842.factions.Commands.Sell;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;

public class SellCommand {
    public static void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(
            commandManager.commandBuilder("sell")
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                .required("amount", IntegerParser.integerParser(1))
                    .handler(ctx -> runCommand(ctx))
        );
    }

    public static void runCommand(CommandContext<CommandSender> ctx) {
        // Player player = (Player)ctx.sender();
        
    }
}
