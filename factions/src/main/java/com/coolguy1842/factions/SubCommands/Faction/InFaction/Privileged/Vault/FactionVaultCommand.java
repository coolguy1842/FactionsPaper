package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Vault;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Vault.VaultSubcommands.FactionCreateVaultCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Vault.VaultSubcommands.FactionOpenVaultCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Vault.VaultSubcommands.FactionRemoveVaultCommand;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Vault.VaultSubcommands.VaultSubcommand;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factions.interfaces.Subcommand;

public class FactionVaultCommand implements Subcommand {
    @Override public String getName() { return "vault"; }
    @Override public String getDescription() { return "Opens a vault with the specified name!"; }
    @Override public Permission getPermission() {
        return Permission.allOf(PlayerPermissions.inFaction);
    }

    private static VaultSubcommand subcommands[] = new VaultSubcommand[] {
        new FactionCreateVaultCommand(),
        new FactionRemoveVaultCommand(),
        new FactionOpenVaultCommand()
    };

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        Builder<CommandSender> vaultBaseCommand =
            baseCommand.literal(getName())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                .permission(getPermission());

        List<Builder<CommandSender>> commands = new ArrayList<>();

        for(VaultSubcommand subcommand : subcommands) {
            for(Builder<CommandSender> command : subcommand.getCommands(vaultBaseCommand)) {
                commands.add(command);
            }
        }

        return commands;
    }

    @Override public void runCommand(CommandContext<CommandSender> ctx) {}
}
