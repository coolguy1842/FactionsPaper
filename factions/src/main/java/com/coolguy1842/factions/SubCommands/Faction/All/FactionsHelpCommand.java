package com.coolguy1842.factions.SubCommands.Faction.All;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.interfaces.Subcommand;

public class FactionsHelpCommand implements Subcommand {
    @Override public String getName() { return "help"; }
    @Override public String getDescription() { return "Shows this screen!"; }
    @Override public Permission getPermission() { return null; }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal(getName())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        // Player player = (Player)ctx.sender();
        // FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
    }
}
