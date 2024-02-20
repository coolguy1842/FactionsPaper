package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Parsers.RankParser;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands.FactionRankCreateCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands.FactionRankRemoveCommand;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factions.Util.RankUtil.RankPermission;
import com.coolguy1842.factions.interfaces.Subcommand;

import net.kyori.adventure.text.Component;

public class FactionRankCommand implements Subcommand {    
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

    @Override public String getName() { return "rank"; }
    @Override public String getDescription() { return "Base command for ranks."; }
    @Override public Permission getPermission() {
        return Permission.allOf(PlayerPermissions.inFaction, PlayerPermissions.rankPermission(RankPermission.ADMIN));
    }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal(getName())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new Requirement()))
                .permission(getPermission())
                .literal("create")
                    .required("name", StringParser.stringParser())
                        .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new FactionRankCreateCommand.Requirement()))
                        .handler(ctx -> FactionRankCreateCommand.runCommand(ctx)),
            baseCommand.literal(getName())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new Requirement()))
                .permission(getPermission())
                .literal("remove")
                    .required("name", RankParser.rankParser())
                        .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new FactionRankRemoveCommand.Requirement()))
                        .handler(ctx -> FactionRankRemoveCommand.runCommand(ctx))
        );
    }

    @Override public void runCommand(CommandContext<CommandSender> ctx) {}
}
