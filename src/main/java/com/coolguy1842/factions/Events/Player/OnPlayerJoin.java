package com.coolguy1842.factions.Events.Player;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Util.DiscordUtil;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Invite;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class OnPlayerJoin implements Listener {
    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer(); 
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Optional<Faction> factionOptional = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction());

        if(factionOptional.isPresent()) {
            FactionUtil.updateFactionsPlayerTabNames(factionOptional.get().getID());
        }

        PlayerUtil.updatePlayerPermissions(player);

        Component joinMessage = MessageUtil.format("{} joined the game!", PlayerUtil.playerGlobalName(player));

        player.getServer().broadcast(joinMessage);
        DiscordUtil.sendToDiscord(PlainTextComponentSerializer.plainText().serialize(joinMessage), "Factions", DiscordUtil.getAvatar(player));
        for(Invite invite : Factions.getFactionsCommon().inviteManager.getInvitesWithInvited(player.getUniqueId())) {
            Optional<Faction> fOptional = Factions.getFactionsCommon().factionManager.getFaction(invite.getInviter());
            if(!fOptional.isPresent()) {
                Factions.getFactionsCommon().inviteManager.removeInvite(invite.getInviter(), invite.getInvited());
                continue;
            }

            Faction f = fOptional.get();
            player.sendMessage(
                MessageUtil.format(
                    "You have an invite from {}!\n{}",
                    Component.text(f.getName()),
                    MessageUtil.getAcceptDeny(
                        ClickEvent.runCommand("/f accept " + f.getName()),
                        HoverEvent.showText(MessageUtil.format("Accept invite from {}?", Component.text(f.getName()))),

                        ClickEvent.runCommand("/f reject " + f.getName()),
                        HoverEvent.showText(MessageUtil.format("Reject invite from {}?", Component.text(f.getName())))
                    )
                )
            );
        }

        e.joinMessage(null);
    }
}
