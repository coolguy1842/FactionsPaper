package com.coolguy1842.factions.Events.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.coolguy1842.factions.Util.PlayerUtil;

public class OnPlayerLeave implements Listener {
    @EventHandler
    private void onLeave(PlayerQuitEvent e) {
        PlayerUtil.removePlayerAttachment(e.getPlayer());
    }    
}
