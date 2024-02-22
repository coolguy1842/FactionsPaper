package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Vault.VaultSubcommands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Parsers.VaultParser;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.VaultUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factionscommon.Classes.Vault;
import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

public class FactionOpenVaultCommand implements VaultSubcommand {
    @Override public Permission getPermission() {
        return Permission.allOf(PlayerPermissions.inFaction, PlayerPermissions.rankPermission(RankPermission.OPEN_VAULT));
    }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                .permission(getPermission())
                .required("vault", VaultParser.vaultParser())
                    .handler(ctx -> runCommand(ctx)),
            baseCommand
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                .permission(getPermission())
                .literal("open")
                    .required("vault", VaultParser.vaultParser())
                        .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        
        Vault vault = ctx.get("vault");
        player.openInventory(VaultUtil.getVaultInventory(vault));
    }
}
