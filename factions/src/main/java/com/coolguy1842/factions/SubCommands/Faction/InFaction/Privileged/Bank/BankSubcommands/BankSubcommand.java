package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Bank.BankSubcommands;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;

public interface BankSubcommand {
    public Builder<CommandSender> getCommand(Builder<CommandSender> baseCommand);
    public void runCommand(CommandContext<CommandSender> ctx);
}
