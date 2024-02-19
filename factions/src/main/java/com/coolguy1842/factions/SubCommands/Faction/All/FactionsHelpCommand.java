package com.coolguy1842.factions.SubCommands.Faction.All;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.interfaces.Subcommand;

import net.kyori.adventure.text.Component;

public class FactionsHelpCommand implements Subcommand {
    private final class Requirement implements FactionRequirement.Interface {
        public Map<String, Component> getErrorMessages() {
            return Map.ofEntries( Map.entry("notPlayer", Component.text("Only players can use this!")) );
        }

        @Override
        public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) { return getErrorMessages().get("notPlayer"); }

        @Override
        public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) { return ctx.sender() instanceof Player; }
    }

    @Override public String getName() { return "help"; }
    @Override public String getDescription() { return "Shows this screen!"; }
    @Override public Permission getPermission() { return null; }

    @Override
    public Builder<CommandSender> getCommand(Builder<CommandSender> baseCommand) {
        return
            baseCommand.literal(getName())
            .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new Requirement()))
            .handler(ctx -> runCommand(ctx));
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        // Player player = (Player)ctx.sender();
        // FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
    }
}
