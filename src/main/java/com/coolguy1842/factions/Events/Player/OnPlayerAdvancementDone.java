package com.coolguy1842.factions.Events.Player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import com.coolguy1842.factions.Util.DiscordUtil;
import com.coolguy1842.factions.Util.PlayerUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class OnPlayerAdvancementDone implements Listener {
    @EventHandler
    private void onLeave(PlayerAdvancementDoneEvent e) {
        Player player = e.getPlayer();
        
        Component message = e.message();
        message = message.replaceText(TextReplacementConfig.builder().match(player.getName()).replacement(PlayerUtil.playerGlobalName(player)).build());

        e.message(message);
        DiscordUtil.sendToDiscord(PlainTextComponentSerializer.plainText().serialize(message), "Factions", DiscordUtil.getAvatar(player));
    }    
}
