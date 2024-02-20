package com.coolguy1842.factions.interfaces;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;

public interface Subcommand {
    public String getName();
    public String getDescription();
    public Permission getPermission();
    
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand);
    public void runCommand(CommandContext<CommandSender> ctx);
}
