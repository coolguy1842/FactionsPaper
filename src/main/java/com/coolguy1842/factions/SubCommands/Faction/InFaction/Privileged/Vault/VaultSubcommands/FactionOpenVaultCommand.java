package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Vault.VaultSubcommands;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Parsers.VaultParser;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement.Interface;
import com.coolguy1842.factions.Util.VaultUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factionscommon.Classes.Vault;

import net.kyori.adventure.text.Component;

import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

public class FactionOpenVaultCommand implements VaultSubcommand {
    public class Requirement implements Interface {
        public Map<String, Component> getErrorMessages() {
            return Map.ofEntries(
                Map.entry("noVault", Component.text("No default vault exists!"))
            );
        }

        @Override
        public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) {
            return getErrorMessages().get("noVault");
        }

        @Override
        public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) {
            Player player = (Player)ctx.sender();
            FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());

            if(ctx.getOrDefault("vault", null) == null) {
                if(!Factions.getFactionsCommon().vaultManager.getVault(factionPlayer.getFaction(), "vault").isPresent()) {
                    return false;
                }
            }

            return true;
        }
    }


    @Override public Permission getPermission() {
        return Permission.allOf(PlayerPermissions.inFaction, PlayerPermissions.rankPermission(RankPermission.OPEN_VAULT));
    }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement(), new Requirement()))
                .permission(getPermission())
                .optional("vault", VaultParser.vaultParser())
                    .handler(ctx -> runCommand(ctx)),
            baseCommand
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement(), new Requirement()))
                .permission(getPermission())
                .literal("open")
                    .optional("vault", VaultParser.vaultParser())
                        .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();
        
        Optional<Vault> defaultVault = Factions.getFactionsCommon().vaultManager.getVault(faction.getID(), "vault");
        Vault vault = ctx.getOrDefault("vault", null);
        if(vault == null) {
            vault = defaultVault.get();
        }

        player.openInventory(VaultUtil.getVaultInventory(vault));
    }
}
