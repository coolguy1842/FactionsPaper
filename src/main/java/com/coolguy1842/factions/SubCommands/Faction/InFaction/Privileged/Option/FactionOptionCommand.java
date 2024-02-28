package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Option;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Interfaces.Subcommand;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Option.OptionSubcommands.FactionOptionGetCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Option.OptionSubcommands.FactionOptionSetCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Option.OptionSubcommands.OptionSubcommand;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

public class FactionOptionCommand implements Subcommand {    
    @Override public String getName() { return "option"; }
    @Override public String getDescription() { return "Base command for options."; }
    @Override public Permission getPermission() {
        return Permission.allOf(PlayerPermissions.inFaction, PlayerPermissions.rankPermission(RankPermission.ADMIN));
    }

    private static OptionSubcommand subcommands[] = new OptionSubcommand[] {
        new FactionOptionSetCommand(),
        new FactionOptionGetCommand()
    };

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        Builder<CommandSender> optionBaseCommand =
            baseCommand.literal(getName())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                .permission(getPermission());

        List<Builder<CommandSender>> commands = new ArrayList<>();

        for(OptionSubcommand subcommand : subcommands) {
            commands.add(subcommand.getCommand(optionBaseCommand));
        }

        return commands;
    }

    @Override public void runCommand(CommandContext<CommandSender> ctx) {}
}
