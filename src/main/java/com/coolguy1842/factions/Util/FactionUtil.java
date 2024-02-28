package com.coolguy1842.factions.Util;

import java.util.UUID;
import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Home;
import com.coolguy1842.factionscommon.Classes.Invite;
import com.coolguy1842.factionscommon.Classes.Rank;
import com.coolguy1842.factionscommon.Classes.Vault;
import com.coolguy1842.factionscommon.Classes.Faction.Option;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class FactionUtil {
    public static void disbandFaction(Server server, UUID factionID) {
        Optional<Faction> factionOptional = Factions.getFactionsCommon().factionManager.getFaction(factionID);
        if(!factionOptional.isPresent()) return;

        Faction faction = factionOptional.get();
        String factionName = faction.getName();

        // remove all invites from this faction
        for(Invite invite : new ArrayList<>(Factions.getFactionsCommon().inviteManager.getInvitesWithInviter(factionID))) {
            Factions.getFactionsCommon().inviteManager.removeInvite(invite.getInviter(), invite.getInvited());
        }
        
        // remove all homes from this faction
        for(Home home : new ArrayList<>(Factions.getFactionsCommon().homeManager.getHomesWithOwner(factionID))) {
            Factions.getFactionsCommon().homeManager.removeHome(home.getID());
        }
        
        // remove all vaults from this faction
        for(Vault vault : new ArrayList<>(Factions.getFactionsCommon().vaultManager.getVaultsInFaction(factionID))) {
            VaultUtil.removeVaultInventory(vault);
            Factions.getFactionsCommon().vaultManager.removeVault(vault.getID());
        }

        // remove all ranks from this faction
        for(Rank rank : new ArrayList<>(Factions.getFactionsCommon().rankManager.getRanksInFaction(factionID))) {
            Factions.getFactionsCommon().rankManager.removeRank(rank.getID());
        }


        // kick all players from the faction
        for(FactionPlayer fP : Factions.getFactionsCommon().playerManager.getPlayersWithFaction(factionID)) {
            Player p = server.getPlayer(fP.getID());
            Factions.getFactionsCommon().playerManager.setPlayerFaction(fP.getID(), null);
            
            if(p != null && p.isOnline()) {        
                PlayerUtil.updatePlayerPermissions(p);
            }
        }

        Factions.getFactionsCommon().factionManager.removeFaction(faction.getID());

        server.broadcast(
            MessageUtil.format(
                Component.text("{} the faction {} has been disbanded!"),
                Factions.getPrefix(),
                Component.text(factionName)
            )
        );


        for(Player player : Bukkit.getOnlinePlayers()) {
            PlayerUtil.updatePlayerTabName(player.getPlayer());
        }
    }

    public static void broadcast(Server server, UUID factionID, Component message) {
        for(FactionPlayer factionPlayer : Factions.getFactionsCommon().playerManager.getPlayersWithFaction(factionID)) {
            Player player = server.getPlayer(factionPlayer.getID());
            
            if(player != null && player.isOnline()) {        
                player.sendMessage(message);
            }
        }
    }


    public static Component getFactionDisplayName(Faction faction) {
        String colorString = Factions.getFactionsCommon().factionManager.getOptionValue(faction.getID(), Option.COLOUR, Optional.of(TextColor.color(255, 255, 255).asHexString())).get();
        TextColor color = TextColor.fromHexString(colorString);

        return Component.text(faction.getName()).color(color);
    }


    public static void updateFactionsPlayerTabNames(UUID factionID) {
        for(FactionPlayer factionPlayer : Factions.getFactionsCommon().playerManager.getPlayersWithFaction(factionID)) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(factionPlayer.getID());

            if(!player.isOnline()) continue;
            PlayerUtil.updatePlayerTabName(player.getPlayer());
        }
    }
}
