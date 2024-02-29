package coolguy1842.discordrelay.Util;

import javax.annotation.Nonnull;

import coolguy1842.discordrelay.Globals;
import net.dv8tion.jda.api.entities.Webhook;

public class DiscordUtil {
    public static void sendMessage(Webhook webhook, @Nonnull String message, String username, String avatar) {
        String safeMessage = message.replaceAll("_", "\\\\_");
        safeMessage = safeMessage.replaceAll("~", "\\\\~");
        
        if(safeMessage == null) return;

        webhook.sendMessage(safeMessage).setAvatarUrl(avatar).setUsername(username).queue();
    }
}
