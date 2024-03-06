package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Leader;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Interfaces.Subcommand;
import com.coolguy1842.factions.Parsers.FactionPlayerParser;
import com.coolguy1842.factions.Parsers.FactionPlayerParser.ParserType;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;

import net.kyori.adventure.text.Component;

public class FactionTransferCommand implements Subcommand {    
    @Override public String getName() { return "transfer"; }
    @Override public String getDescription() { return "Transfers your faction to the specified player!"; }
    @Override public Permission getPermission() {
        return Permission.allOf(PlayerPermissions.inFaction, PlayerPermissions.leader);
    }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal(getName())
                .required("player", FactionPlayerParser.withOptions(ParserType.INCLUDES_OFFLINE, ParserType.IN_SAME_FACTION))
                    .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                    .permission(getPermission())
                    .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());

        FactionPlayer transferee = ctx.get("player");
        OfflinePlayer transfereePlayer = Bukkit.getOfflinePlayer(transferee.getID());

        Factions.getFactionsCommon().factionManager.setFactionLeader(factionPlayer.getFaction(), transferee.getID());

        FactionUtil.broadcast(
            player.getServer(), factionPlayer.getFaction(),
            MessageUtil.format(
                "{} {} has transferred the faction to {}!",
                FactionUtil.getFactionNameAsPrefix(Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get()),
                Component.text(player.getName()),
                Component.text(transfereePlayer.getName())
            )
        );

        if(transfereePlayer.isOnline()) {
            PlayerUtil.updatePlayerPermissions(transfereePlayer.getPlayer());
        }

        PlayerUtil.updatePlayerPermissions(player);
    }
}
