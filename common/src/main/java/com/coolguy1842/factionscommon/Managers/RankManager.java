package com.coolguy1842.factionscommon.Managers;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import it.unimi.dsi.fastutil.Pair;

import com.coolguy1842.factionscommon.FactionsCommon;
import com.coolguy1842.factionscommon.Classes.Rank;
import com.coolguy1842.factionscommon.Databases.RankDatabase;

public class RankManager {
    public RankDatabase database;

    private Map<UUID, Rank> ranks;
    private Map<Pair<UUID, String>, Rank> ranksByFactionAndName;
    private List<Rank> ranksList;

    void addToCache(Rank rank) {
        ranks.put(rank.getID(), rank);
        ranksByFactionAndName.put(Pair.of(rank.getFaction(), rank.getName()), rank);

        ranksList.add(rank);
    }
    
    void removeFromCache(Rank rank) {
        ranks.remove(rank.getID());
        ranksByFactionAndName.remove(Pair.of(rank.getFaction(), rank.getName()));
        
        ranksList.remove(rank);
    }


    public RankManager(Path configPath) {
        database = new RankDatabase(configPath);

        reload();
    }

    private void loadRanks() {
        for(Rank rank : database.getRanks()) {
            addToCache(rank);
        }
    }

    
    public Rank addRank(UUID id, UUID faction, String name) {
        assertThat(id != null).isTrue().withFailMessage("RankManager#addRank failed: id == null");
        assertThat(faction != null).isTrue().withFailMessage("RankManager#addRank failed: faction == null");
        assertThat(name != null).isTrue().withFailMessage("RankManager#addRank failed: name == null");

        Optional<Rank> rankOptional = database.addRank(id, faction, name, "");
        assertThat(rankOptional).isPresent().withFailMessage("RankManager#addRank failed: rank with id: %s, faction: %s, name: %s not created.", id, faction, name);

        Rank rank = rankOptional.get();
        addToCache(rank);

        return rank;
    }
    
    public void removeRank(UUID id) {
        assertThat(id != null).isTrue().withFailMessage("RankManager#removeRank failed: id == null");

        assertThat(database.removeRank(id)).isTrue().withFailMessage("RankManager#removeRank failed: no rank with id: %s removed.", id);
        removeFromCache(ranks.get(id));
    }


    public List<Rank> getRanks() { return ranksList; }
    public List<Rank> getRanksInFaction(UUID faction) {
        assertThat(faction != null).isTrue().withFailMessage("RankManager#getRanksInFaction failed: faction == null");
        return ranksList.stream().filter(x -> x.getFaction().equals(faction)).toList();
    }

    public Optional<Rank> getRank(UUID id) {
        if(id == null) return Optional.empty();
        if(ranks.containsKey(id)) return Optional.of(ranks.get(id));

        Optional<Rank> rank = database.getRank(id);
        if(rank.isPresent()) addToCache(rank.get());

        return rank;
    }
    
    public Optional<Rank> getRank(UUID faction, String name) {
        assertThat(faction != null).isTrue().withFailMessage("RankManager#getRank failed: faction == null");
        assertThat(name != null).isTrue().withFailMessage("RankManager#getRank failed: name == null");
        Pair<UUID, String> pair = Pair.of(faction, name);

        if(ranksByFactionAndName.containsKey(pair)) return Optional.of(ranksByFactionAndName.get(pair));

        Optional<Rank> rank = database.getRank(faction, name);
        if(rank.isPresent()) addToCache(rank.get());

        return rank;
    }


    public void setRankName(UUID id, String name) {
        assertThat(id != null).isTrue().withFailMessage("RankManager#setRankName failed: id == null");
        assertThat(name != null).isTrue().withFailMessage("RankManager#setRankName failed: name == null");

        assertThat(getRank(id)).isPresent().withFailMessage("RankManager#setRankName failed: Rank with UUID: %s does not exist.", id);
        
        database.setRankName(id, name);
        ranks.get(id).setName(name);
    }
    

    public void setRankPermission(UUID id, String permission, boolean value) {
        assertThat(id).isNotNull().withFailMessage("RankManager#setRankPermission failed: id == null");
        assertThat(permission).isNotNull().withFailMessage("RankManager#setRankPermission failed: permission == null");

        assertThat(getRank(id)).isPresent().withFailMessage("RankManager#setRankPermissions failed: Rank with UUID: %s does not exist.", id);
        Rank rank = getRank(id).get();
        Set<String> permissions = rank.getPermissions();

        if(value) permissions.add(permission);
        else permissions.remove(permission);

        setRankPermissions(id, String.join(",", permissions));
    }

    // WILL NOT CHECK IF PERMISSIONS STRING IS VALID
    public void setRankPermissions(UUID id, String permissions) {
        assertThat(id != null).isTrue().withFailMessage("RankManager#setRankPermissions failed: id == null");
        assertThat(permissions != null).isTrue().withFailMessage("RankManager#setRankPermissions failed: permissions == null");

        assertThat(getRank(id)).isPresent().withFailMessage("RankManager#setRankPermissions failed: Rank with UUID: %s does not exist.", id);
        FactionsCommon.LOGGER.info("id: {}, permissions: {}", id, permissions);
        
        database.setRankPermissions(id, permissions);
        ranks.get(id).setPermissions(permissions);
    }


    public void reload() {
        ranks = new HashMap<>();
        ranksByFactionAndName = new HashMap<>();
        ranksList = new ArrayList<>();

        loadRanks();
    }

    public void close() {
        database.close();
        database = null;

        ranksList = null;
    }
}
