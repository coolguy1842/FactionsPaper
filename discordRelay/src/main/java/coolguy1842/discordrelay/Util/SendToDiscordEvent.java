package coolguy1842.discordrelay.Util;

import javax.annotation.Nonnull;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import coolguy1842.discordrelay.Globals;

public class SendToDiscordEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final String username;
    private final String avatar;
    @Nonnull private final String contents;
    private final Long channel;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public SendToDiscordEvent(String username, String avatar, @Nonnull String contents, Long channel) {
        super(true);
        
        this.username = username;
        this.avatar = avatar;
        
        this.contents = contents;

        this.channel = channel;
    }
    
    public SendToDiscordEvent(String username, String avatar, @Nonnull String contents) {
        super(true);
        
        this.username = username;
        this.avatar = avatar;
        
        this.contents = contents;
        
        this.channel = Globals.config.getLong("channelid");
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public String getUsername() {
        return this.username;
    }

    public String getAvatar() {
        return this.avatar;
    }
    
    public @Nonnull String getContents() {
        return this.contents;
    }
    
    public Long getChannel() {
        return this.channel;
    }
}