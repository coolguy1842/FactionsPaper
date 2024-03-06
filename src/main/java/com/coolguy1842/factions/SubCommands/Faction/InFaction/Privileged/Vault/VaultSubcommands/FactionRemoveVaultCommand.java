package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Vault.VaultSubcommands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Parsers.VaultParser;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.VaultUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factionscommon.Classes.Vault;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

import net.kyori.adventure.text.Component;

public class FactionRemoveVaultCommand implements VaultSubcommand {
    @Override public Permission getPermission() {
        return Permission.allOf(PlayerPermissions.inFaction, PlayerPermissions.rankPermission(RankPermission.REMOVE_VAULT));
    }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal("remove")
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                .permission(getPermission())
                .required("vault", VaultParser.vaultParser())
                    .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();
        
        Vault vault = ctx.get("vault");
        Inventory vaultInventory = VaultUtil.getVaultInventory(vault);

        for(ItemStack item : vaultInventory.getContents()) {
            if(item == null || item.isEmpty()) continue;
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }

        
        int numVaults = Factions.getFactionsCommon().vaultManager.getVaultsInFaction(faction.getID()).size();
        if(numVaults > 1) {
            Factions.getFactionsCommon().factionManager.setFactionBalance(faction.getID(), faction.getBalance() + FactionCreateVaultCommand.vaultFee);
        }
        else {
            Factions.getFactionsCommon().factionManager.setFactionBalance(faction.getID(), faction.getBalance() + FactionCreateVaultCommand.firstVaultFee);
        }

        VaultUtil.removeVaultInventory(vault);
        Factions.getFactionsCommon().vaultManager.removeVault(vault.getID());

        FactionUtil.broadcast(
            player.getServer(), vault.getFaction(),
            MessageUtil.format("{} {} removed the vault named {}.", FactionUtil.getFactionNameAsPrefix(faction), player.displayName(), Component.text(vault.getName()))
        );
    }
}
