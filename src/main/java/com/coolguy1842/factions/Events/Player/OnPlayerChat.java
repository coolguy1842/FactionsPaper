package com.coolguy1842.factions.Events.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.coolguy1842.factions.Util.DiscordUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;

public class OnPlayerChat implements Listener {
    @EventHandler
    private void onChat(AsyncChatEvent e) {
        e.setCancelled(true);

        e.getPlayer().getServer().broadcast(MessageUtil.format("{}: {}", PlayerUtil.playerGlobalName(e.getPlayer()), e.message()));
        DiscordUtil.sendToDiscord(((TextComponent)e.message()).content(), e.getPlayer());
    }
}
