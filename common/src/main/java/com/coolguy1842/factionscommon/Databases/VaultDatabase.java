package com.coolguy1842.factionscommon.Databases;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import java.nio.file.Path;

import javax.sql.rowset.CachedRowSet;

import com.coolguy1842.factionscommon.Classes.Database;
import com.coolguy1842.factionscommon.Classes.Vault;

public class VaultDatabase implements DatabaseHandler {
    @Override public String getName() { return "vaults"; }

    private Database database;
    @Override public Database getDatabase() { return database; }

    public VaultDatabase(Path configPath) {
        database = new Database(configPath.resolve(getName() + ".db").toString());

        initTables();
    }

    public void initTables() {
        try {
            database.execute("""
                CREATE TABLE IF NOT EXISTS vaults (
                    contents TEXT NOT NULL,
                    faction CHAR(36) NOT NULL,
                    id CHAR(36) PRIMARY KEY,
                    name TEXT NOT NULL
                );
            """);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Optional<Vault> addVault(UUID id, UUID faction, String name, String contents) {
        assertThat(id).isNotNull().withFailMessage("VaultDatabase#addVault failed: id == null");
        assertThat(faction).isNotNull().withFailMessage("VaultDatabase#addVault failed: faction == null");
        assertThat(name).isNotNull().withFailMessage("VaultDatabase#addVault failed: name == null");
        assertThat(contents).isNotNull().withFailMessage("VaultDatabase#addVault failed: contents == null");

        try {
            database.execute(
                "INSERT INTO vaults(id, faction, name, contents) VALUES(?, ?, ?, ?)",
                id, faction, name, contents
            );

            return Optional.of(new Vault(id, faction, name, contents));
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Boolean removeVault(UUID id) {
        assertThat(id).isNotNull().withFailMessage("VaultDatabase#removeVault failed: id == null");

        try {
            database.execute("DELETE FROM vaults WHERE id = ?", id);
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public List<Vault> getVaults() {
        List<Vault> out = new ArrayList<>();
        
        try {
            CachedRowSet rows = database.query("SELECT id FROM vaults");

            while(rows.next()) {
                Optional<Vault> vault = getVault(UUID.fromString(rows.getString("id")));
                if(!vault.isPresent()) continue;

                out.add(vault.get());
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return out;
    }
    
    public Optional<Vault> getVault(UUID id) {
        assertThat(id).isNotNull().withFailMessage("VaultDatabase#getVault failed: id == null");

        try(CachedRowSet rows = database.query("SELECT * FROM vaults WHERE id = ?", id)) {
            if(rows == null || rows.size() <= 0) return Optional.empty();
            
            rows.next();
            
            UUID faction = UUID.fromString(rows.getString("faction"));
            String name = rows.getString("name");
            String contents = rows.getString("contents");

            return Optional.of(new Vault(id, faction, name, contents));
        }
        catch (SQLException e) {
            e.printStackTrace();
            
            return Optional.empty();
        }
    }


    public void setVaultName(UUID id, String name) {
        assertThat(id).isNotNull().withFailMessage("VaultDatabase#setVaultName failed: id == null");
        assertThat(name).isNotNull().withFailMessage("VaultDatabase#setVaultName failed: name == null");

        try { database.execute("UPDATE vaults SET name = ? WHERE id = ?", name, id); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    public void setVaultContents(UUID id, String contents) {
        assertThat(id).isNotNull().withFailMessage("VaultDatabase#setVaultContents failed: id == null");
        assertThat(contents).isNotNull().withFailMessage("VaultDatabase#setVaultContents failed: contents == null");
        
        try { database.execute("UPDATE vaults SET contents = ? WHERE id = ?", contents, id); }
        catch (SQLException e) { e.printStackTrace(); }
    }


    public void close() {
        database.disconnect();
        database = null;
    }
}
