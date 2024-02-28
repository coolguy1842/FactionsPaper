package com.coolguy1842.factionscommon.Managers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import java.nio.file.Path;

import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionOption;
import com.coolguy1842.factionscommon.Databases.FactionDatabase;

public class FactionManager {
    public FactionDatabase database;
    private HashMap<UUID, Faction> factions;
    private HashMap<UUID, List<FactionOption>> options;

    private HashMap<String, Faction> factionsByName;
    private List<Faction> factionsList;

    void addToCache(Faction faction) {
        factionsList.add(faction);

        factions.put(faction.getID(), faction);
        factionsByName.put(faction.getName(), faction);
    }
    
    void removeFromCache(Faction faction) {
        factionsList.remove(faction);
        
        factions.remove(faction.getID());
        factionsByName.remove(faction.getName());
    }
    
    
    public FactionManager(Path configPath) {
        database = new FactionDatabase(configPath);
        
        reload();
    }

    private void loadFactions() {
        for(Faction faction : database.getFactions()) {
            addToCache(faction);
        }
    }


    public Faction addFaction(UUID id, String name, UUID leader) {
        assertThat(id).isNotNull().withFailMessage("FactionManager#addFaction failed: id == null");

        Optional<Faction> factionOptional = database.addFaction(id, name, 0L, leader);
        assertThat(factionOptional).isPresent().withFailMessage("FactionManager#addFaction failed: faction with id: %s, name: %s not created.", id, name);

        Faction faction = factionOptional.get();
        addToCache(faction);

        return faction;
    }
    
    public void removeFaction(UUID id) {
        assertThat(id).isNotNull().withFailMessage("FactionManager#removeFaction failed: id == null");

        assertThat(database.removeFaction(id)).isTrue().withFailMessage("FactionManager#removeFaction failed: no faction with id: %s removed.", id);
        removeFromCache(factions.get(id));
    }


    public List<Faction> getFactions() { return factionsList; }
    public Optional<Faction> getFaction(UUID id) {
        if(id == null) return Optional.empty();
        if(factions.containsKey(id)) return Optional.of(factions.get(id));

        Optional<Faction> faction = database.getFaction(id);
        if(faction.isPresent()) addToCache(faction.get());

        return faction;
    }

    public Optional<Faction> getFaction(String name) {
        if(name == null) return Optional.empty();
        if(factionsByName.containsKey(name)) return Optional.of(factionsByName.get(name));

        Optional<Faction> faction = database.getFaction(name);
        if(faction.isPresent()) addToCache(faction.get());

        return faction;
    }


    public void setFactionName(UUID id, String name) {
        assertThat(id).isNotNull().withFailMessage("FactionManager#setFactionName failed: id == null");
        assertThat(name).isNotNull().withFailMessage("FactionManager#setFactionName failed: name == null");

        assertThat(factions).containsKey(id).withFailMessage("FactionManager#setFactionName failed: Faction with id: %s does not exist.", id);
        
        database.setFactionName(id, name);
        factions.get(id).setName(name);
    }

    public void setFactionBalance(UUID id, Long balance) {
        assertThat(id).isNotNull().withFailMessage("FactionManager#setFactioBalance failed: id == null");
        assertThat(balance).isNotNull().withFailMessage("FactionManager#setFactionBalance failed: balance == null");

        assertThat(factions).containsKey(id).withFailMessage("FactionManager#setFactionBalance failed: Faction with id: %s does not exist.", id);

        database.setFactionBalance(id, balance);
        factions.get(id).setBalance(balance);
    }

    public void setFactionLeader(UUID id, UUID leader) {
        assertThat(id).isNotNull().withFailMessage("FactionManager#setFactionLeader failed: id == null");
        assertThat(leader).isNotNull().withFailMessage("FactionManager#setFactionLeader failed: leader == null");

        assertThat(factions).containsKey(id).withFailMessage("FactionManager#setFactionLeader failed: Faction with id: %s does not exist.", id);

        database.setFactionLeader(id, leader);
        factions.get(id).setLeader(leader);
    }



    
    void addToCache(FactionOption option) {
        if(!options.containsKey(option.getFaction())) options.put(option.getFaction(), new ArrayList<>());
        options.get(option.getFaction()).add(option);
    }
    
    void removeFromCache(FactionOption option) {
        if(!options.containsKey(option.getFaction())) return;
        options.get(option.getFaction()).removeIf(x -> x.getKey().equals(option.getKey()));
    }
    
    private void loadOptions() {
        for(FactionOption option : database.getOptions()) {
            addToCache(option);
        }
    }

    public Optional<FactionOption> getOption(UUID faction, Faction.Option key) {
        assertThat(faction).isNotNull().withFailMessage("FactionManager#getOption failed: faction == null");
        assertThat(key).isNotNull().withFailMessage("FactionManager#getOption failed: key == null");

        assertThat(factions).containsKey(faction).withFailMessage("FactionManager#getOption failed: faction with id %s does not exist", faction);

        if(!options.containsKey(faction)) options.put(faction, new ArrayList<>());
        return options.get(faction).stream().filter(x -> x.getKey().equals(key)).findFirst();
    }

    public Optional<String> getOptionValue(UUID faction, Faction.Option key) {
        return getOptionValue(faction, key, Optional.empty());
    }

    public Optional<String> getOptionValue(UUID faction, Faction.Option key, Optional<String> defaultValue) {
        assertThat(faction).isNotNull().withFailMessage("FactionManager#getOptionValue failed: faction == null");
        assertThat(key).isNotNull().withFailMessage("FactionManager#getOptionValue failed: key == null");

        assertThat(factions).containsKey(faction).withFailMessage("FactionManager#getOptionValue failed: faction with id %s does not exist", faction);
        if(!options.containsKey(faction)) options.put(faction, new ArrayList<>());

        Optional<FactionOption> option = options.get(faction).stream().filter(x -> x.getKey().equals(key)).findFirst();
        if(!option.isPresent()) return defaultValue;
        return Optional.of(option.get().getValue());
    }

    public void setOption(UUID faction, Faction.Option key, String value) {
        assertThat(faction).isNotNull().withFailMessage("FactionManager#setOption failed: faction == null");
        assertThat(key).isNotNull().withFailMessage("FactionManager#setOption failed: key == null");

        assertThat(factions).containsKey(faction).withFailMessage("FactionManager#setOption failed: faction with id %s does not exist", faction);
        if(!options.containsKey(faction)) options.put(faction, new ArrayList<>());

        Optional<FactionOption> option = getOption(faction, key);

        database.setOption(faction, key, value);

        if(value == null) {
            if(option.isPresent()) {
                removeFromCache(option.get());
            }

            return;
        }

        
        if(option.isPresent()) option.get().setValue(value);
        else addToCache(new FactionOption(faction, key, value));
    }


    public void reload() {
        factionsList = new ArrayList<>();
        
        factions = new HashMap<>();
        factionsByName = new HashMap<>();

        options = new HashMap<>();

        this.loadFactions();
        this.loadOptions();
    }
    
    public void close() {
        database.close();
        database = null;

        factions = null;
        factionsByName = null;
        
        factionsList = null;
    }
}
