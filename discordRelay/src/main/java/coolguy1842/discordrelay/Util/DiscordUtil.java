package coolguy1842.discordrelay.Util;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.entities.Webhook;

public class DiscordUtil {
    public static void sendMessage(Webhook webhook, @Nonnull String message, String username, String avatar) {
        webhook.sendMessage(message).setAvatarUrl(avatar).setUsername(username).queue();
    }
}
