package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Option.OptionSubcommands;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;

public interface OptionSubcommand {
    public Permission getPermission();
    public Builder<CommandSender> getCommand(Builder<CommandSender> baseCommand);
    public void runCommand(CommandContext<CommandSender> ctx);
}
