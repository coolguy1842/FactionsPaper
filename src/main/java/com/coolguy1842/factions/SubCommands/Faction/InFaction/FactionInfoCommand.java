package com.coolguy1842.factions.SubCommands.Faction.InFaction;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Interfaces.Subcommand;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class FactionInfoCommand implements Subcommand {
    @Override public String getName() { return "info"; }
    @Override public String getDescription() { return "Shows info about your faction!"; }
    @Override public Permission getPermission() { return PlayerPermissions.inFaction; }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal(getName())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                .permission(getPermission())
                .handler(ctx -> runCommand(ctx)),
            baseCommand
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                .permission(getPermission())
                .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());

        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();

        player.sendMessage(
            MessageUtil.format(
                "{} {} Info {}",
                Component.text("    ").decorate(TextDecoration.STRIKETHROUGH),
                FactionUtil.getFactionDisplayName(faction),
                Component.text("    ").decorate(TextDecoration.STRIKETHROUGH)
            )
        );

        player.sendMessage(String.format("Leader: %s", Bukkit.getOfflinePlayer(faction.getLeader()).getName()));
        player.sendMessage(String.format("Members: %d", Factions.getFactionsCommon().playerManager.getPlayersWithFaction(faction.getID()).size()));
        player.sendMessage(String.format("Balance: %d", faction.getBalance()));
        player.sendMessage(String.format("Homes: %d", Factions.getFactionsCommon().homeManager.getHomesWithOwner(faction.getID()).size()));
        player.sendMessage(String.format("Vaults: %d", Factions.getFactionsCommon().vaultManager.getVaultsInFaction(faction.getID()).size()));

        player.sendMessage(Component.text("                " + (" ".repeat(faction.getName().length() * 2))).decorate(TextDecoration.STRIKETHROUGH));
    }
}
