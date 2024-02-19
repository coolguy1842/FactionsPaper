package com.coolguy1842.factionscommon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coolguy1842.factionscommon.Managers.FactionManager;
import com.coolguy1842.factionscommon.Managers.InviteManager;
import com.coolguy1842.factionscommon.Managers.PlayerManager;
import com.coolguy1842.factionscommon.Managers.RankManager;

public class FactionsCommon {
    private Path configPath;
    public static final Logger LOGGER = LoggerFactory.getLogger("factions-common");

    public PlayerManager playerManager;
    public RankManager rankManager;
    public InviteManager inviteManager;
    public FactionManager factionManager;

 
    public FactionsCommon(Path configPath) {
        this.configPath = configPath;
        this.reload();
    }


    public void reload() {
        LOGGER.info("Starting...");

        try { Files.createDirectories(configPath);  }
        catch(IOException e) { e.printStackTrace(); }

        if(factionManager != null) factionManager.close();
        if(inviteManager  != null) inviteManager.close();
        if(rankManager    != null) rankManager.close();
        if(playerManager  != null) playerManager.close();

        factionManager = new FactionManager(configPath);
        inviteManager  = new InviteManager(configPath);
        rankManager    = new RankManager(configPath);
        playerManager  = new PlayerManager(configPath);

        LOGGER.info("Started");
    }


    public void close() {
        LOGGER.info("Stopping...");

        playerManager.close();
        rankManager.close();
        inviteManager.close();
        factionManager.close();
        
        playerManager = null;
        rankManager = null;
        inviteManager = null;
        factionManager = null;

        LOGGER.info("Stopped");
    }
}
