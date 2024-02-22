package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Home;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Suggestions.HomeNameSuggestion;
import com.coolguy1842.factions.Suggestions.HomeNameSuggestion.ParserType;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.LocationUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factions.interfaces.Subcommand;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Home;
import com.coolguy1842.factionscommon.Classes.Home.OwnerType;
import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

import net.kyori.adventure.text.Component;

public class FactionSetHomeCommand implements Subcommand {    
    @Override public String getName() { return "sethome"; }
    @Override public String getDescription() { return "Teleports you to the specified faction home!"; }
    @Override public Permission getPermission() { return Permission.allOf(PlayerPermissions.inFaction, PlayerPermissions.rankPermission(RankPermission.SET_HOME)); }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal(getName())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                .permission(getPermission())
                .optional("home", StringParser.stringParser(), HomeNameSuggestion.homeNameSuggestion(ParserType.FACTION))
                    .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();

        String homeName = ctx.getOrDefault("home", "home");

        Optional<Home> homeOptional = Factions.getFactionsCommon().homeManager.getHome(faction.getID(), homeName);
        if(homeOptional.isPresent()) {
            Factions.getFactionsCommon().homeManager.setHomeLocation(homeOptional.get().getID(), LocationUtil.serializeLocation(player.getLocation()));
            
            FactionUtil.broadcast(
                player.getServer(), faction.getID(),
                MessageUtil.format("{} {} updated location of the home {}.", Factions.getPrefix(), player.displayName(), Component.text(homeName))
            );

            return;
        }

        Factions.getFactionsCommon().homeManager.addHome(
            UUID.randomUUID(), homeName,
            LocationUtil.serializeLocation(player.getLocation()),
            faction.getID(), OwnerType.FACTION
        );

        FactionUtil.broadcast(
            player.getServer(), faction.getID(),
            MessageUtil.format("{} {} created a new home named {}.", Factions.getPrefix(), player.displayName(), Component.text(homeName))
        );
    }
}
