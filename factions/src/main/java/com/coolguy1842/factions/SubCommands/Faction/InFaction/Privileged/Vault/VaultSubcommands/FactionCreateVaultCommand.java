package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Vault.VaultSubcommands;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement.Interface;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.VaultUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

import net.kyori.adventure.text.Component;

public class FactionCreateVaultCommand implements VaultSubcommand {
    public class Requirement implements Interface {
        public Map<String, Component> getErrorMessages() {
            return Map.ofEntries(
                Map.entry("vaultExists", Component.text("A vault named {} already exists!"))
            );
        }

        @Override
        public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) {
            return MessageUtil.format(getErrorMessages().get("vaultExists"), Component.text((String)ctx.get("name")));
        }

        @Override
        public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) {
            Player player = (Player)ctx.sender();
            FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
            Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();
    
            String vaultName = ctx.get("name");
            return !Factions.getFactionsCommon().vaultManager.getVault(faction.getID(), vaultName).isPresent();
        }
    }


    @Override public Permission getPermission() { return Permission.allOf(PlayerPermissions.inFaction, PlayerPermissions.rankPermission(RankPermission.CREATE_VAULT)); }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal("create")
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement(), new Requirement()))
                .permission(getPermission())
                .required("name", StringParser.stringParser())
                    .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();

        String vaultName = ctx.get("name");
        Factions.getFactionsCommon().vaultManager.addVault(UUID.randomUUID(), faction.getID(), vaultName, VaultUtil.serializeInventory(VaultUtil.newVaultInventory(vaultName)));
    
        FactionUtil.broadcast(
            player.getServer(), faction.getID(),
            MessageUtil.format("{} {} has created a new vault named {}!", Factions.getPrefix(), player.displayName(), Component.text(vaultName))
        );
    }
}
