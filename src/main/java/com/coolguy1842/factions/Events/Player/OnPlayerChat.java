package com.coolguy1842.factions.Events.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.coolguy1842.factions.Util.MessageUtil;

import io.papermc.paper.event.player.AsyncChatEvent;

public class OnPlayerChat implements Listener {
    @EventHandler
    private void onChat(AsyncChatEvent e) {
        e.setCancelled(true);

        e.getPlayer().getServer().broadcast(MessageUtil.format("{}: {}", e.getPlayer().displayName(), e.message()));
    }
}
