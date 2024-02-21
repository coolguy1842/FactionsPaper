package com.coolguy1842.factionscommon.Managers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.nio.file.Path;

import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Databases.PlayerDatabase;

public class PlayerManager {
    public PlayerDatabase database;

    private HashMap<UUID, FactionPlayer> players;
    private List<FactionPlayer> playersList;

    void addToCache(FactionPlayer player) {
        playersList.add(player);

        players.put(player.getID(), player);
    }


    public PlayerManager(Path configPath) {
        database = new PlayerDatabase(configPath);
        reload();
    }
    
    private void loadPlayers() {
        for(FactionPlayer player : database.getPlayers()) {
            addToCache(player);
        }
    }


    public FactionPlayer addPlayer(UUID id) {
        assertThat(id != null).isTrue().withFailMessage("PlayerManager#addPlayer failed: id == null");

        Optional<FactionPlayer> player = database.addPlayer(id, 0L, null, null);
        assertThat(player.isPresent()).isTrue().withFailMessage("PlayerManager#addPlayer failed: player with UUID: %s not created.", id);
        addToCache(player.get());
        
        return player.get();
    }


    public List<FactionPlayer> getPlayers() { return playersList; }
    public List<FactionPlayer> getPlayersWithFaction(UUID faction) {
        assertThat(faction != null).isTrue().withFailMessage("PlayerManager#getPlayersWithFaction failed: faction == null");
        return playersList.stream().filter(x -> x.getFaction() != null && x.getFaction().equals(faction)).toList();
    }

    public List<FactionPlayer> getPlayersWithRank(UUID rank) {
        assertThat(rank != null).isTrue().withFailMessage("PlayerManager#getPlayersWithRank failed: rank == null");
        return playersList.stream().filter(x -> x.getRank() != null && x.getRank().equals(rank)).toList();
    }

    public Optional<FactionPlayer> getPlayer(UUID id) {
        assertThat(id != null).isTrue().withFailMessage("PlayerManager#getPlayer failed: id == null");
        if(players.containsKey(id)) return Optional.of(players.get(id));

        Optional<FactionPlayer> player = database.getPlayer(id);
        if(player.isPresent()) addToCache(player.get());

        return player;
    }


    public void setPlayerBalance(UUID id, Long balance) {
        assertThat(id != null).isTrue().withFailMessage("PlayerManager#setPlayerBalance failed: id == null");
        assertThat(balance != null).isTrue().withFailMessage("PlayerManager#setPlayerBalance failed: balance == null");

        assertThat(players).containsKey(id).withFailMessage("PlayerManager#setPlayerBalance failed: Player with UUID: %s does not exist.", id);
        
        database.setPlayerBalance(id, balance);
        players.get(id).setBalance(balance);
    }

    public void setPlayerFaction(UUID id, UUID faction) {
        assertThat(id != null).isTrue().withFailMessage("PlayerManager#setPlayerFaction failed: id == null");
        assertThat(players).containsKey(id).withFailMessage("PlayerManager#setPlayerFaction failed: Player with UUID: %s does not exist.", id);
        FactionPlayer player = players.get(id);
        
        database.setPlayerFaction(id, faction);
        if(faction == null) {
            database.setPlayerRank(id, null);   
            player.setRank(faction);
        }

        player.setFaction(faction);
    }

    public void setPlayerRank(UUID id, UUID rank) {
        assertThat(id != null).isTrue().withFailMessage("PlayerManager#setPlayerRank failed: id == null");
        assertThat(players).containsKey(id).withFailMessage("PlayerManager#setPlayerRank failed: Player with UUID: %s does not exist.", id);

        database.setPlayerRank(id, rank);
        players.get(id).setRank(rank);
    }

    
    public void reload() {
        players = new HashMap<>();
        playersList = new ArrayList<>();

        loadPlayers();
    }

    public void close() {
        database.close();
        database = null;

        players = null;
        playersList = null;
    }
}
