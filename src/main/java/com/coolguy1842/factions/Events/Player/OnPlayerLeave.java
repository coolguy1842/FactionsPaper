package com.coolguy1842.factions.Events.Player;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.coolguy1842.factions.Managers.TPAManager;
import com.coolguy1842.factions.Util.DiscordUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class OnPlayerLeave implements Listener {
    @EventHandler
    private void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        PlayerUtil.removePlayerAttachment(player);

        Component quitMessage = MessageUtil.format("{} left the game!", PlayerUtil.playerGlobalName(player));

        player.getServer().broadcast(quitMessage);
        DiscordUtil.sendToDiscord(PlainTextComponentSerializer.plainText().serialize(quitMessage), "Factions", DiscordUtil.getAvatar(player));


        TPAManager.getInstance().removeRequest(player.getUniqueId());

        for(UUID receiver : TPAManager.getInstance().getReceiversWithSender(player.getUniqueId())) {
            TPAManager.getInstance().removeRequest(receiver);
        }


        e.quitMessage(null);
    }    
}
