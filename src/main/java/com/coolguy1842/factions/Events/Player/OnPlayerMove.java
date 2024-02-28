package com.coolguy1842.factions.Events.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factionscommon.Classes.Claim;
import com.coolguy1842.factionscommon.Classes.Faction;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public class OnPlayerMove implements Listener {
    private static Map<UUID, UUID> playersLastChunkFaction = new HashMap<>();

    private static Times titleShowTime = Times.times(Duration.ofMillis(500), Duration.ofSeconds(1), Duration.ofMillis(250));
    
    @EventHandler
    private void playerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        
        Optional<Claim> claim = Factions.getFactionsCommon().claimManager.getClaim(player.getWorld().getUID(), player.getChunk().getChunkKey());
        if(claim.isPresent() && claim.get().getFaction().equals(playersLastChunkFaction.getOrDefault(player.getUniqueId(), null))) {
            return;
        }
        else if(!claim.isPresent() && playersLastChunkFaction.get(player.getUniqueId()) == null) {
            return;
        }

        if(claim.isPresent()) {
            Faction faction = Factions.getFactionsCommon().factionManager.getFaction(claim.get().getFaction()).get();
            player.showTitle(
                Title.title(
                    FactionUtil.getFactionDisplayName(faction),
                    Component.text("Now Entering").asComponent(),
                    titleShowTime
                )
            );
            
            playersLastChunkFaction.put(player.getUniqueId(), faction.getID());
        }
        else {
            player.showTitle(
                Title.title(
                    Component.text("Wilderness").color(TextColor.color(85, 255, 85)).asComponent(),
                    Component.text("Now Entering").asComponent(),
                    titleShowTime
                )
            );
            
            playersLastChunkFaction.put(player.getUniqueId(), null);
        }
    }
}
