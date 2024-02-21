package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands.FactionRankAssignCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands.FactionRankCreateCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands.FactionRankRemoveCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands.FactionRankUnassignCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands.RankSubcommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands.Permission.FactionRankPermissionGetCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands.Permission.FactionRankPermissionSetCommand;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factions.interfaces.Subcommand;
import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

public class FactionRankCommand implements Subcommand {    
    @Override public String getName() { return "rank"; }
    @Override public String getDescription() { return "Base command for ranks."; }
    @Override public Permission getPermission() {
        return Permission.allOf(PlayerPermissions.inFaction, PlayerPermissions.rankPermission(RankPermission.ADMIN));
    }

    private static RankSubcommand subcommands[] = new RankSubcommand[] {
        new FactionRankCreateCommand       (),
        new FactionRankRemoveCommand       (),
        new FactionRankAssignCommand       (),
        new FactionRankUnassignCommand     (),
        new FactionRankPermissionGetCommand(),
        new FactionRankPermissionSetCommand()
    };

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        Builder<CommandSender> rankBaseCommand =
            baseCommand.literal(getName())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                .permission(getPermission());

        List<Builder<CommandSender>> commands = new ArrayList<>();

        for(RankSubcommand subcommand : subcommands) {
            commands.add(subcommand.getCommand(rankBaseCommand));
        }

        return commands;
    }

    @Override public void runCommand(CommandContext<CommandSender> ctx) {}
}
