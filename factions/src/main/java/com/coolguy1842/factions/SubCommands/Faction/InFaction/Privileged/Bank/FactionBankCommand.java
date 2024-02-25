package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Bank;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Interfaces.Subcommand;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Bank.BankSubcommands.BankSubcommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Bank.BankSubcommands.FactionBankDepositCommand;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;

public class FactionBankCommand implements Subcommand {    
    @Override public String getName() { return "bank"; }
    @Override public String getDescription() { return "Base command for the bank."; }
    @Override public Permission getPermission() { return PlayerPermissions.inFaction; }

    private static BankSubcommand subcommands[] = new BankSubcommand[] {
        new FactionBankDepositCommand()
    };

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        Builder<CommandSender> rankBaseCommand =
            baseCommand.literal(getName())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                .permission(getPermission());

        List<Builder<CommandSender>> commands = new ArrayList<>();

        for(BankSubcommand subcommand : subcommands) {
            commands.add(subcommand.getCommand(rankBaseCommand));
        }

        return commands;
    }

    @Override public void runCommand(CommandContext<CommandSender> ctx) {}
}
