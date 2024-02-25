package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Home;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Interfaces.Subcommand;
import com.coolguy1842.factions.Parsers.HomeParser;
import com.coolguy1842.factions.Parsers.HomeParser.ParserType;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement.Interface;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Home;
import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

import net.kyori.adventure.text.Component;

public class FactionDelHomeCommand implements Subcommand {
    public class Requirement implements Interface {
        public Map<String, Component> getErrorMessages() {
            return Map.ofEntries(
                Map.entry("noHome", Component.text("No default home exists!"))
            );
        }

        @Override
        public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) {
            return getErrorMessages().get("noHome");
        }

        @Override
        public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) {
            Player player = (Player)ctx.sender();
            FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());

            if(ctx.getOrDefault("home", null) == null) {
                if(!Factions.getFactionsCommon().homeManager.getHome(factionPlayer.getFaction(), "home").isPresent()) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override public String getName() { return "delhome"; }
    @Override public String getDescription() { return "Deletes the specified faction home!"; }
    @Override public Permission getPermission() { return Permission.allOf(PlayerPermissions.inFaction, PlayerPermissions.rankPermission(RankPermission.DEL_HOME)); }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal(getName())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement(), new Requirement()))
                .permission(getPermission())
                .optional("home", HomeParser.homeParser(ParserType.FACTION))
                    .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();

        Optional<Home> defaultHome = Factions.getFactionsCommon().homeManager.getHome(faction.getID(), "home");
        Home home = ctx.getOrDefault("home", null);
        if(home == null) {
            home = defaultHome.get();
        }

        
        int numHomes = Factions.getFactionsCommon().homeManager.getHomesWithOwner(faction.getID()).size();
        if(numHomes > 1) {
            Factions.getFactionsCommon().factionManager.setFactionBalance(faction.getID(), faction.getBalance() + FactionSetHomeCommand.homeFee);
        }

        Factions.getFactionsCommon().homeManager.removeHome(home.getID());
        FactionUtil.broadcast(
            player.getServer(), faction.getID(),
            MessageUtil.format("{} {} deleted the home {}.", Factions.getPrefix(), player.displayName(), Component.text(home.getName()))
        );
    }
}
