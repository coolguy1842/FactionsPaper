package com.coolguy1842.factions.interfaces;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;

public interface Subcommand {
    public String getName();
    public String getDescription();
    public Permission getPermission();
    
    public Command.Builder<CommandSender> getCommand(Command.Builder<CommandSender> baseCommand);
    public void runCommand(CommandContext<CommandSender> ctx);
}
