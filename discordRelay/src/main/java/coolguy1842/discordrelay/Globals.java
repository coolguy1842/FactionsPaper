package coolguy1842.discordrelay;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Webhook;

public class Globals {
    public static JavaPlugin plugin;
    public static JDA jda;
    public static FileConfiguration config;
    
    public static Long channelID;
    public static Webhook webhook;
}
