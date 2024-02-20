package com.coolguy1842.factions.Events.Player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;

public class OnPlayerLeave implements Listener {
    @EventHandler
    private void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        PlayerUtil.removePlayerAttachment(player);

        player.getServer().broadcast(MessageUtil.format("{} left the game!", player.displayName()));
        e.quitMessage(null);
    }    
}
