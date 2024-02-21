package com.coolguy1842.factionscommon.Managers;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.coolguy1842.factionscommon.Classes.Home;
import com.coolguy1842.factionscommon.Classes.Home.OwnerType;
import com.coolguy1842.factionscommon.Databases.HomeDatabase;

public class HomeManager {
    public HomeDatabase database;
    private Map<UUID, Home> homes;
    private Map<UUID, List<Home>> homesFromOwners;    
    
    void addToCache(Home home) {
        homes.put(home.getID(), home);

        if(!homesFromOwners.containsKey(home.getOwner())) homesFromOwners.put(home.getOwner(), new ArrayList<>());
        homesFromOwners.get(home.getOwner()).add(home);
    }
    
    void removeFromCache(Home home) {
        homes.remove(home.getID());

        if(!homesFromOwners.get(home.getOwner()).contains(home)) return;
        homesFromOwners.get(home.getOwner()).remove(home);
    }
    
    
    public HomeManager(Path configPath) {
        database = new HomeDatabase(configPath);
        
        reload();
    }

    private void loadHomes() {
        for(Home home : database.getHomes()) {
            addToCache(home);
        }
    }

    
    public List<Home> getHomesWithOwner(UUID owner) {
        assertThat(owner != null).isTrue().withFailMessage("HomeManager#getHomesWithOwner failed: owner == null");

        if(!homesFromOwners.containsKey(owner)) return new ArrayList<>();
        return homesFromOwners.get(owner);
    }


    public Optional<Home> getHome(UUID id) {
        assertThat(id != null).isTrue().withFailMessage("HomeManager#getHome failed: id == null");

        if(!homes.containsKey(id)) return Optional.empty();
        return Optional.of(homes.get(id));
    }
    
    public Optional<Home> getHome(UUID owner, String name) {
        assertThat(owner != null).isTrue().withFailMessage("HomeManager#getHome failed: owner == null");
        assertThat(name != null).isTrue().withFailMessage("HomeManager#getHome failed: name == null");

        if(!homesFromOwners.containsKey(owner)) return Optional.empty();
        return homesFromOwners.get(owner).stream().filter(x -> x.getName().equals(name)).findFirst();
    }

    public Home addHome(UUID id, String name, String location, UUID owner, OwnerType ownerType) {
        assertThat(id != null).isTrue().withFailMessage("HomeManager#addHome failed: id == null");
        assertThat(name != null).isTrue().withFailMessage("HomeManager#addHome failed: name == null");
        assertThat(location != null).isTrue().withFailMessage("HomeManager#addHome failed: location == null");
        assertThat(owner != null).isTrue().withFailMessage("HomeManager#addHome failed: owner == null");
        assertThat(ownerType != null).isTrue().withFailMessage("HomeManager#addHome failed: ownerType == null");

        Optional<Home> homeOptional = database.addHome(id, name, location, owner, ownerType);
        assertThat(homeOptional.isPresent()).isTrue().withFailMessage("HomeManager#addHome failed: home with id: %s, name: %s, owner: %s not created.", id, name, owner);
        
        Home home = homeOptional.get();
        addToCache(home);

        return home;
    }

    public void removeHome(UUID id) {
        Home home = homes.get(id);

        removeFromCache(home);
        database.removeHome(id);
    }

    public void removeHome(UUID owner, String name) {
        Optional<Home> homeOptional = getHome(owner, name);
        assertThat(homeOptional.isPresent()).isTrue().withFailMessage("HomeManager#removeHome failed: home with owner: %s, name: %s doesn't exist.", owner, name);

        removeFromCache(homeOptional.get());
        database.removeHome(homeOptional.get().getID());
    }


    public void setHomeName(UUID id, String name) {
        Home home = homes.get(id);

        home.setName(name);
        database.setHomeName(id, name);
    }
    
    public void setHomeName(UUID owner, String currentName, String newName) {
        Optional<Home> homeOptional = getHome(owner, currentName);
        assertThat(homeOptional.isPresent()).isTrue().withFailMessage("HomeManager#setHomeName failed: home with owner: %s, name: %s doesn't exist.", owner, currentName);
        setHomeName(homeOptional.get().getID(), newName);
    }


    public void setHomeLocation(UUID id, String location) {
        assertThat(id).isNotNull().withFailMessage("HomeManager#setHomeLocation failed: id == null.", id);
        assertThat(location).isNotNull().withFailMessage("HomeManager#setHomeLocation failed: location == null.", id);

        assertThat(homes).containsKey(id).withFailMessage("HomeManager#setHomeLocation failed: Home with id: %s does not exist.", id);
        
        Home home = homes.get(id);

        home.setLocation(location);
        database.setHomeLocation(id, location);
    }
    
    public void setHomeLocation(UUID owner, String name, String location) {
        Optional<Home> homeOptional = getHome(owner, name);
        assertThat(homeOptional.isPresent()).isTrue().withFailMessage("HomeManager#setHomeLocation failed: home with owner: %s, name: %s doesn't exist.", owner, name);
        setHomeLocation(homeOptional.get().getID(), location);
    }


    public void reload() {
        homes = new HashMap<>();
        homesFromOwners = new HashMap<>();

        loadHomes();
    }

    public void close() {
        database.close();
        database = null;

        homes = null;
        homesFromOwners = null;
    }
}
