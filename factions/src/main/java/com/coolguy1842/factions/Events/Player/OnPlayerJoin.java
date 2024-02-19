package com.coolguy1842.factions.Events.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.coolguy1842.factions.Util.PlayerUtil;

public class OnPlayerJoin implements Listener {
    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        PlayerUtil.updatePlayerPermissions(e.getPlayer());

        
    }
}
