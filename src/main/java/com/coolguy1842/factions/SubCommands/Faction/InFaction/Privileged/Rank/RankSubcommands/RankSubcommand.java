package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;

public interface RankSubcommand {
    public Builder<CommandSender> getCommand(Builder<CommandSender> baseCommand);
    public void runCommand(CommandContext<CommandSender> ctx);
}
