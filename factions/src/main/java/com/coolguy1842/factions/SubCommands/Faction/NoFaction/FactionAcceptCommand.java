package com.coolguy1842.factions.SubCommands.Faction.NoFaction;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Parsers.FactionParser;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factions.interfaces.Subcommand;
import com.coolguy1842.factionscommon.Classes.Faction;

import net.kyori.adventure.text.Component;

public class FactionAcceptCommand implements Subcommand {    
    private final class Requirement implements FactionRequirement.Interface {
        public Map<String, Component> getErrorMessages() {
            return Map.ofEntries(
                Map.entry("notPlayer", Component.text("Only players can use this!")),
                Map.entry("error", Component.text("Error"))
            );
        }

        @Override
        public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) {
            if(!(ctx.sender() instanceof Player)) {
                return getErrorMessages().get("notPlayer");
            }

            return getErrorMessages().get("error");
        }

        @Override
        public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) {
            return ctx.sender() instanceof Player;
        }
    }

    @Override public String getName() { return "accept"; }
    @Override public String getDescription() { return "Accepts an invite from the specified faction"; }
    @Override public Permission getPermission() { return PlayerPermissions.notInFaction; }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal(getName())
                .required("faction", FactionParser.invitingFaction())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new Requirement()))
                .permission(getPermission())
                .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        final Faction faction = ctx.get("faction");

        PlayerUtil.acceptInvite(player.getServer(), faction.getID(), player.getUniqueId());
    }
}
