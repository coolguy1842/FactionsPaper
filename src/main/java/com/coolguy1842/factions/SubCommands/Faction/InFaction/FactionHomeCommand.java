package com.coolguy1842.factions.SubCommands.Faction.InFaction;

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
import com.coolguy1842.factions.Util.LocationUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Home;

import net.kyori.adventure.text.Component;

public class FactionHomeCommand implements Subcommand {
    public class Requirement implements Interface {
        public Map<String, Component> getErrorMessages() {
            return Map.ofEntries(
                Map.entry("noHome", Component.text("No default home exists!")),
                Map.entry("error", Component.text("Error"))
            );
        }

        @Override
        public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) {
            Home home = ctx.getOrDefault("home", null);

            if(home == null) return getErrorMessages().get("noHome");
            return getErrorMessages().get("error");
        }

        @Override
        public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) {
            Player player = (Player)ctx.sender();
            FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());

            Home home = ctx.getOrDefault("home", null);
            
            if(home == null) {
                if(!Factions.getFactionsCommon().homeManager.getHome(factionPlayer.getFaction(), "home").isPresent()) {
                    return false;
                }
            }

            return true;
        }
    }


    @Override public String getName() { return "home"; }
    @Override public String getDescription() { return "Teleports you to the specified faction home!"; }
    @Override public Permission getPermission() {
        return PlayerPermissions.inFaction;
    }

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

        player.sendMessage(MessageUtil.format("{} Teleporting you to {}!", Factions.getPrefix(), Component.text(home.getName())));
        PlayerUtil.teleportPlayer(player, LocationUtil.deserializeLocation(home.getLocation()));
    }
}
