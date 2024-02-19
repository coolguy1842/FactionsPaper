package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Leader;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.PlayerUtil.Permissions;
import com.coolguy1842.factions.interfaces.Subcommand;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;

import net.kyori.adventure.text.Component;

public class FactionDisbandCommand implements Subcommand {    
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

    @Override public String getName() { return "disband"; }
    @Override public String getDescription() { return "Disbands the faction you are in!"; }
    @Override public Permission getPermission() {
        return Permission.allOf(Permissions.inFactionPermission, Permissions.leaderPermission);
    }

    @Override
    public Builder<CommandSender> getCommand(Builder<CommandSender> baseCommand) {
        return
            baseCommand.literal(getName())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new Requirement()))
                .permission(getPermission())
                .handler(ctx -> runCommand(ctx));
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());

        FactionUtil.disbandFaction(player.getServer(), factionPlayer.getFaction());
    }
}
