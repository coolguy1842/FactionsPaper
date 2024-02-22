package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Vault.VaultSubcommands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;

public interface VaultSubcommand {
    public Permission getPermission();
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand);
    public void runCommand(CommandContext<CommandSender> ctx);
}
