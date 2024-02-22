package com.coolguy1842.factionscommon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coolguy1842.factionscommon.Managers.FactionManager;
import com.coolguy1842.factionscommon.Managers.HomeManager;
import com.coolguy1842.factionscommon.Managers.InviteManager;
import com.coolguy1842.factionscommon.Managers.PlayerManager;
import com.coolguy1842.factionscommon.Managers.RankManager;
import com.coolguy1842.factionscommon.Managers.VaultManager;

public class FactionsCommon {
    private Path configPath;
    public static final Logger LOGGER = LoggerFactory.getLogger("factions-common");

    public FactionManager factionManager;
    public PlayerManager playerManager;
    public RankManager rankManager;
    public InviteManager inviteManager;
    public HomeManager homeManager;
    public VaultManager vaultManager;

 
    public FactionsCommon(Path configPath) {
        this.configPath = configPath;
        this.reload();
    }


    public void reload() {
        LOGGER.info("Starting...");

        try { Files.createDirectories(configPath);  }
        catch(IOException e) { e.printStackTrace(); }

        if(factionManager != null) factionManager.close();
        if(playerManager  != null) playerManager .close();
        if(rankManager    != null) rankManager   .close();
        if(inviteManager  != null) inviteManager .close();
        if(homeManager    != null) homeManager   .close();
        if(vaultManager   != null) vaultManager  .close();

        factionManager = new FactionManager(configPath);
        playerManager  = new PlayerManager (configPath);
        rankManager    = new RankManager   (configPath);
        inviteManager  = new InviteManager (configPath);
        homeManager    = new HomeManager   (configPath);
        vaultManager   = new VaultManager  (configPath);

        LOGGER.info("Started");
    }


    public void close() {
        LOGGER.info("Stopping...");

        vaultManager  .close();
        homeManager   .close();
        inviteManager .close();
        rankManager   .close();
        playerManager .close();
        factionManager.close();
        
        vaultManager   = null;
        homeManager    = null;
        inviteManager  = null;
        rankManager    = null;
        playerManager  = null;
        factionManager = null;

        LOGGER.info("Stopped");
    }
}
