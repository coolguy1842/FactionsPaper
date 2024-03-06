package com.coolguy1842.factions.SubCommands.Faction.All;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Interfaces.Subcommand;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factionscommon.Classes.Faction;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class FactionLeaderboardCommand implements Subcommand {
    @Override public String getName() { return "leaderboard"; }
    @Override public String getDescription() { return "Shows the leaderboard of all of the factions!"; }
    @Override public Permission getPermission() { return null; }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal(getName())
                .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        CommandSender sender = ctx.sender();
        sender.sendMessage(
            MessageUtil.format(
                "{} Leaderboard {}",
                Component.text("    ").decorate(TextDecoration.STRIKETHROUGH),
                Component.text("    ").decorate(TextDecoration.STRIKETHROUGH)
            )
        );

        for(Faction faction : Factions.getFactionsCommon().factionManager.getFactions()) {
            sender.sendMessage(MessageUtil.format("{}: {}", FactionUtil.getFactionDisplayName(faction), Component.text(faction.getBalance())));
        }

        sender.sendMessage(Component.text("                          ").decorate(TextDecoration.STRIKETHROUGH));
    }
}
