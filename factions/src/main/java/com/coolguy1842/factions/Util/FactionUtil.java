package com.coolguy1842.factions.Util;

import java.util.UUID;
import java.util.Optional;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Invite;

import net.kyori.adventure.text.Component;

public class FactionUtil {
    public static void disbandFaction(Server server, UUID factionID) {
        Optional<Faction> factionOptional = Factions.getFactionsCommon().factionManager.getFaction(factionID);
        if(!factionOptional.isPresent()) return;

        Faction faction = factionOptional.get();
        String factionName = faction.getName();

        // remove all invites from this faction
        for(Invite invite : Factions.getFactionsCommon().inviteManager.getInvitesWithInviter(factionID)) {
            Factions.getFactionsCommon().inviteManager.removeInvite(invite.getInviter(), invite.getInvited());
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
    }

    public static void broadcast(Server server, UUID factionID, Component message) {
        for(FactionPlayer factionPlayer : Factions.getFactionsCommon().playerManager.getPlayersWithFaction(factionID)) {
            Player player = server.getPlayer(factionPlayer.getID());
            
            if(player != null && player.isOnline()) {        
                player.sendMessage(message);
            }
        }
    }
}
