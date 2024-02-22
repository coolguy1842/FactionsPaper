package com.coolguy1842.factionscommon.Managers;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.coolguy1842.factionscommon.Classes.Vault;
import com.coolguy1842.factionscommon.Databases.VaultDatabase;

public class VaultManager {
    public VaultDatabase database;
    private Map<UUID, Vault> vaults;
    private Map<UUID, List<Vault>> vaultsInFactions;
    
    void addToCache(Vault vault) {
        vaults.put(vault.getID(), vault);

        if(!vaultsInFactions.containsKey(vault.getFaction())) vaultsInFactions.put(vault.getFaction(), new ArrayList<>());
        vaultsInFactions.get(vault.getFaction()).add(vault);
    }
    
    void removeFromCache(Vault vault) {
        vaults.remove(vault.getID());

        if(!vaultsInFactions.containsKey(vault.getFaction())) return;
        if(!vaultsInFactions.get(vault.getFaction()).contains(vault)) return;
        vaultsInFactions.get(vault.getFaction()).remove(vault);
    }
    
    
    public VaultManager(Path configPath) {
        database = new VaultDatabase(configPath);
        
        reload();
    }

    private void loadVaults() {
        for(Vault vault : database.getVaults()) {
            addToCache(vault);
        }
    }

    
    public List<Vault> getVaultsInFaction(UUID faction) {
        assertThat(faction).isNotNull().withFailMessage("VaultManager#getVaultsInFaction failed: faction == null");

        if(!vaultsInFactions.containsKey(faction)) return new ArrayList<>();
        return vaultsInFactions.get(faction);
    }


    public Optional<Vault> getVault(UUID id) {
        assertThat(id).isNotNull().withFailMessage("VaultManager#getVault failed: id == null");

        if(!vaults.containsKey(id)) return Optional.empty();
        return Optional.of(vaults.get(id));
    }
    
    public Optional<Vault> getVault(UUID faction, String name) {
        assertThat(faction).isNotNull().withFailMessage("VaultManager#getVault failed: faction == null");
        assertThat(name).isNotNull().withFailMessage("VaultManager#getVault failed: name == null");

        if(!vaultsInFactions.containsKey(faction)) return Optional.empty();
        return vaultsInFactions.get(faction).stream().filter(x -> x.getName().equals(name)).findFirst();
    }

    public Vault addVault(UUID id, UUID faction, String name, String contents) {
        assertThat(id).isNotNull().withFailMessage("VaultManager#addVault failed: id == null");
        assertThat(faction).isNotNull().withFailMessage("VaultManager#addVault failed: faction == null");
        assertThat(name).isNotNull().withFailMessage("VaultManager#addVault failed: name == null");
        assertThat(contents).isNotNull().withFailMessage("VaultManager#addVault failed: contents == null");
        
        Optional<Vault> vaultOptional = database.addVault(id, faction, name, contents);
        assertThat(vaultOptional).isPresent().withFailMessage("VaultManager#addVault failed: home with id: %s, faction: %s, name: %s not created.", id, faction, name);
        
        Vault vault = vaultOptional.get();
        addToCache(vault);

        return vault;
    }

    public void removeVault(UUID id) {
        assertThat(vaults).containsKey(id).withFailMessage("VaultManager#removeVault failed: vault with id: %s doesn't exist.", id);
        Vault vault = vaults.get(id);

        removeFromCache(vault);
        database.removeVault(id);
    }

    public void removeVault(UUID faction, String name) {
        Optional<Vault> vaultOptional = getVault(faction, name);
        assertThat(vaultOptional).isPresent().withFailMessage("VaultManager#removeVault failed: vault with faction: %s, name: %s doesn't exist.", faction, name);

        removeFromCache(vaultOptional.get());
        database.removeVault(vaultOptional.get().getID());
    }


    public void setVaultName(UUID id, String name) {
        assertThat(id).isNotNull().withFailMessage("VaultManager#removeHome failed: id == null");
        assertThat(name).isNotNull().withFailMessage("VaultManager#removeHome failed: name == null");

        assertThat(vaults).containsKey(id).withFailMessage("VaultManager#set failed: vault with id: %s doesn't exist.", id);
        Vault vault = vaults.get(id);

        vault.setName(name);
        database.setVaultName(id, name);
    }
    
    public void setHomeName(UUID faction, String currentName, String newName) {
        assertThat(faction).isNotNull().withFailMessage("VaultManager#setVaultName failed: faction == null");
        assertThat(currentName).isNotNull().withFailMessage("VaultManager#setVaultName failed: currentName == null");
        assertThat(newName).isNotNull().withFailMessage("VaultManager#setVaultName failed: newName == null");

        Optional<Vault> vaultOptional = getVault(faction, currentName);
        assertThat(vaultOptional).isPresent().withFailMessage("VaultManager#setVaultName failed: vault with faction: %s, name: %s doesn't exist.", faction, currentName);
        database.setVaultName(vaultOptional.get().getID(), newName);
    }


    public void setVaultContents(UUID id, String contents) {
        assertThat(id).isNotNull().withFailMessage("VaultManager#setVaultContents failed: id == null.", id);
        assertThat(contents).isNotNull().withFailMessage("VaultManager#setHomeLocation failed: contents == null.", id);

        assertThat(vaults).containsKey(id).withFailMessage("VaultManager#setVaultContents failed: vault with id: %s does not exist.", id);
        
        Vault vault = vaults.get(id);

        vault.setContents(contents);
        database.setVaultContents(id, contents);
    }
    
    public void setVaultContents(UUID faction, String name, String contents) {
        assertThat(faction).isNotNull().withFailMessage("VaultManager#setVaultContents failed: faction == null.", faction);
        assertThat(name).isNotNull().withFailMessage("VaultManager#setVaultContents failed: name == null.", name);
        assertThat(contents).isNotNull().withFailMessage("VaultManager#setVaultContents failed: contents == null.", contents);

        Optional<Vault> vaultOptional = getVault(faction, name);
        assertThat(vaultOptional).isPresent().withFailMessage("VaultManager#setVaultContents failed: vault with faction: %s, name: %s doesn't exist.", faction, name);
        
        database.setVaultContents(vaultOptional.get().getID(), contents);
    }


    public void reload() {
        vaults = new HashMap<>();
        vaultsInFactions = new HashMap<>();

        loadVaults();
    }

    public void close() {
        database.close();
        database = null;

        vaults = null;
        vaultsInFactions = null;
    }
}
