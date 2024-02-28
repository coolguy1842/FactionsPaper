package com.coolguy1842.factions.Events.Player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.coolguy1842.factions.Util.DiscordUtil;
import com.coolguy1842.factions.Util.PlayerUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class OnPlayerDeath implements Listener {
    @EventHandler
    private void onLeave(PlayerDeathEvent e) {
        Player player = e.getPlayer();
        
        Component deathMessage = e.deathMessage();
        deathMessage = deathMessage.replaceText(TextReplacementConfig.builder().match(player.getName()).replacement(PlayerUtil.playerGlobalName(player)).build());

        e.deathMessage(deathMessage);
        DiscordUtil.sendToDiscord(PlainTextComponentSerializer.plainText().serialize(deathMessage), "Factions", DiscordUtil.getAvatar(player));
    }    
}
