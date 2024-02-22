package com.coolguy1842.factions.SubCommands.Faction.NoFaction;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Parsers.FactionParser;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factions.interfaces.Subcommand;
import com.coolguy1842.factionscommon.Classes.Faction;

public class FactionRejectCommand implements Subcommand {    
    @Override public String getName() { return "reject"; }
    @Override public String getDescription() { return "Rejects an invite from the specified faction"; }
    @Override public Permission getPermission() { return PlayerPermissions.notInFaction; }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal(getName())
                .required("faction", FactionParser.invitingFaction())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                .permission(getPermission())
                .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        final Faction faction = ctx.get("faction");

        PlayerUtil.rejectInvite(player.getServer(), faction.getID(), player.getUniqueId());
    }
}
