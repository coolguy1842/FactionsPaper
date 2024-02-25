package com.coolguy1842.factions.Events.Player;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.Invite;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class OnPlayerJoin implements Listener {
    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer(); 
        PlayerUtil.updatePlayerPermissions(player);

        player.getServer().broadcast(MessageUtil.format("{} joined the game!", player.displayName()));
        for(Invite invite : Factions.getFactionsCommon().inviteManager.getInvitesWithInvited(player.getUniqueId())) {
            Optional<Faction> factionOptional = Factions.getFactionsCommon().factionManager.getFaction(invite.getInviter());
            if(!factionOptional.isPresent()) {
                Factions.getFactionsCommon().inviteManager.removeInvite(invite.getInviter(), invite.getInvited());
                continue;
            }

            Faction faction = factionOptional.get();
            player.sendMessage(
                MessageUtil.format(
                    "You have an invite from {}!\n{}",
                    Component.text(faction.getName()),
                    MessageUtil.getAcceptDeny(
                        ClickEvent.runCommand("/f accept " + faction.getName()),
                        HoverEvent.showText(MessageUtil.format("Accept invite from {}?", Component.text(faction.getName()))),

                        ClickEvent.runCommand("/f reject " + faction.getName()),
                        HoverEvent.showText(MessageUtil.format("Reject invite from {}?", Component.text(faction.getName())))
                    )
                )
            );
        }

        e.joinMessage(null);
    }
}
