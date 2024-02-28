package coolguy1842.discordrelay;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import coolguy1842.discordrelay.Util.DiscordLogging;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

public final class DiscordRelay extends JavaPlugin implements Listener {
    MessageListener listener;

    @Override
    public void onEnable() {
        Globals.plugin = this;

        Globals.plugin.saveDefaultConfig();
        Globals.config = Globals.plugin.getConfig();

        Globals.jda = JDABuilder.createDefault(Globals.config.getString("token"))
                        .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS)
                        .build();

        try {
            Globals.jda.awaitReady();
        }
        catch(InterruptedException e) {
            return;
        }

        Long channelID = Globals.config.getLong("channelid", 0L);
        TextChannel channel = Globals.jda.getTextChannelById(channelID);
        if(channel == null) {
            this.getLogger().warning("Error getting channel with id " + channelID);
            return;
        }

        for(Webhook webhook : channel.retrieveWebhooks().complete()) {
            webhook.delete().queue();
        }

        Globals.webhook = channel.createWebhook("Relay").complete();

        DiscordLogging.info("Started");

        listener = new MessageListener();
        registerEvents();
    }

    @Override
    public void onDisable() {
        if(Globals.webhook != null) {
            Globals.webhook.delete().complete();
            Globals.webhook = null;
        }

        if(Globals.jda != null) {

            Globals.jda.removeEventListener(listener);
            Globals.jda.shutdownNow();

            Globals.jda = null;
        }
        
        if(Globals.webhook != null) {
            Globals.webhook = null;
        }

        Globals.config = null;
        Globals.plugin = null;
    }

    void registerEvents() {
        Globals.jda.addEventListener(listener);
        
        getServer().getPluginManager().registerEvents(listener, Globals.plugin);
    }
}
