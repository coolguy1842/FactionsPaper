package com.coolguy1842.factions.SubCommands.Faction.All;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Interfaces.Subcommand;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.SubCommands.FactionsSubCommands;
import com.coolguy1842.factions.Util.MessageUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class FactionsHelpCommand implements Subcommand {
    @Override public String getName() { return "help"; }
    @Override public String getDescription() { return "Shows this screen!"; }
    @Override public Permission getPermission() { return null; }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal(getName())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();

        player.sendMessage(
            MessageUtil.format(
                "{} Help {}",
                Component.text("    ").decorate(TextDecoration.STRIKETHROUGH),
                Component.text("    ").decorate(TextDecoration.STRIKETHROUGH)
            )
        );

        for(Subcommand subcommand : FactionsSubCommands.subcommands) {
            if(subcommand.getPermission() != null && !ctx.hasPermission(subcommand.getPermission())) continue;

            player.sendMessage(
                MessageUtil.format(
                    "{}: {}",
                    Component.text(subcommand.getName()), Component.text(subcommand.getDescription())
                )
            );
        }

        player.sendMessage(
            Component.text("                  ").decorate(TextDecoration.STRIKETHROUGH)
        );
    }
}
