package com.coolguy1842.factions.Util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.coolguy1842.factions.Factions;

import coolguy1842.discordrelay.Util.SendToDiscordEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class DiscordUtil {
    public static String getAvatar(OfflinePlayer player) {
        return String.format("https://mc-heads.net/avatar/%s", player.getUniqueId());
    }

    public static void sendToDiscord(String contents, String username, String avatar) {
        if(contents == null) return;
        
        Bukkit.getScheduler().runTaskAsynchronously(Factions.getPlugin(), () -> Bukkit.getPluginManager().callEvent(new SendToDiscordEvent(username, avatar, contents)));
    }
    

    public static void sendToDiscord(String contents, Player player) {
        if(contents == null) return;

        String name = PlainTextComponentSerializer.plainText().serialize(PlayerUtil.playerGlobalName(player));
        String avatar = getAvatar(player);

        Bukkit.getScheduler().runTaskAsynchronously(Factions.getPlugin(), () -> Bukkit.getPluginManager().callEvent(new SendToDiscordEvent(name, avatar, contents)));
    }
}
