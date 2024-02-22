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
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionOption;

public class FactionDatabase implements DatabaseHandler {
    @Override public String getName() { return "factions"; }

    private Database database;
    @Override public Database getDatabase() { return database; }

    public FactionDatabase(Path configPath) {
        database = new Database(configPath.resolve(getName() + ".db").toString());

        initTables();
    }

    public void initTables() {
        try {
            database.execute("""
                CREATE TABLE IF NOT EXISTS factions (
                    id CHAR(36) PRIMARY KEY,
                    name TEXT NOT NULL,
                    balance BIGINT NOT NULL,
                    leader CHAR(36) UNIQUE NOT NULL
                );                                  
            """);
            
            database.execute("""
                CREATE TABLE IF NOT EXISTS options (
                    faction CHAR(36) NOT NULL,
                    key TEXT NOT NULL,
                    value TEXT NOT NULL
                );  
            """);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Optional<Faction> addFaction(UUID id, String name, Long balance, UUID leader) {
        assertThat(id).isNotNull().withFailMessage("FactionDatabase#addFaction failed: id == null");
        assertThat(name).isNotNull().withFailMessage("FactionDatabase#addFaction failed: name == null");
        assertThat(balance).isNotNull().withFailMessage("FactionDatabase#addFaction failed: balance == null");
        assertThat(leader).isNotNull().withFailMessage("FactionDatabase#addFaction failed: leader == null");

        try {
            database.execute(
                "INSERT INTO factions(id, name, balance, leader) VALUES(?, ?, ?, ?)",
                id, name, balance, leader
            );

            return Optional.of(new Faction(id, name, balance, leader));
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Boolean removeFaction(UUID id) {
        assertThat(id).isNotNull().withFailMessage("FactionDatabase#removeFaction failed: id == null");

        try {
            database.execute("DELETE FROM factions WHERE id = ?", id);
            database.execute("DELETE FROM options WHERE faction = ?", id);

            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public List<Faction> getFactions() {
        List<Faction> out = new ArrayList<>();
        
        try {
            CachedRowSet rows = database.query("SELECT id FROM factions");

            while(rows.next()) {
                Optional<Faction> faction = getFaction(UUID.fromString(rows.getString("id")));
                if(!faction.isPresent()) continue;

                out.add(faction.get());
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return out;
    }
    
    public Optional<Faction> getFaction(String name) {
        assertThat(name).isNotNull().withFailMessage("FactionDatabase#getFaction failed: name == null");

        try(CachedRowSet rows = database.query("SELECT * FROM factions WHERE name = ?", name)) {
            if(rows == null || rows.size() <= 0) return Optional.empty();
            rows.next();

            return getFaction(UUID.fromString(rows.getString("id")));
        }
        catch (SQLException e) {
            e.printStackTrace();
            
            return Optional.empty();
        }
    }

    public Optional<Faction> getFaction(UUID id) {
        assertThat(id).isNotNull().withFailMessage("FactionDatabase#getFaction failed: id == null");

        try(CachedRowSet rows = database.query("SELECT * FROM factions WHERE id = ?", id)) {
            if(rows == null || rows.size() <= 0) return Optional.empty();
            
            rows.next();
            
            String name = rows.getString("name");
            Long balance = rows.getLong("balance");

            UUID leader = UUID.fromString(rows.getString("leader"));

            return Optional.of(new Faction(id, name, balance, leader));
        }
        catch (SQLException e) {
            e.printStackTrace();
            
            return Optional.empty();
        }
    }


    public void setFactionName(UUID id, String name) {
        assertThat(id).isNotNull().withFailMessage("FactionDatabase#setFactionName failed: id == null");
        assertThat(name).isNotNull().withFailMessage("FactionDatabase#setFactionName failed: name == null");

        try { database.execute("UPDATE factions SET name = ? WHERE id = ?", name, id); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    public void setFactionBalance(UUID id, Long balance) {
        assertThat(id).isNotNull().withFailMessage("FactionDatabase#setFactionBalance failed: id == null");
        assertThat(balance).isNotNull().withFailMessage("FactionDatabase#setFactionBalance failed: balance == null");
        
        try { database.execute("UPDATE factions SET balance = ? WHERE id = ?", balance, id); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    public void setFactionLeader(UUID id, UUID leader) {
        assertThat(id).isNotNull().withFailMessage("FactionDatabase#setFactionLeader failed: id == null");
        assertThat(leader).isNotNull().withFailMessage("FactionDatabase#setFactionLeader failed: leader == null");
        
        try { database.execute("UPDATE factions SET leader = ? WHERE id = ?", leader, id); }
        catch (SQLException e) { e.printStackTrace(); }
    }



    public List<FactionOption> getOptions() {
        List<FactionOption> out = new ArrayList<>();
        
        try(CachedRowSet rows = database.query("SELECT * FROM options")) {
            if(rows == null || rows.size() <= 0) return out;
            
            while(rows.next()) {
                out.add(new FactionOption(
                    UUID.fromString(rows.getString("faction")),
                    Faction.Option.valueOf(rows.getString("key")),
                    rows.getString("value"))
                );
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return out;
    }

    public List<FactionOption> getOptions(UUID faction) {
        List<FactionOption> out = new ArrayList<>();
        assertThat(faction).isNotNull().withFailMessage("FactionDatabase#getOptions failed: faction == null");

        try(CachedRowSet rows = database.query("SELECT * FROM options WHERE faction = ?", faction)) {
            if(rows == null || rows.size() <= 0) return out;
            
            while(rows.next()) {
                out.add(new FactionOption(
                    faction,
                    Faction.Option.valueOf(rows.getString("key")),
                    rows.getString("value"))
                );
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return out;
    }
    
    public Optional<FactionOption> getOption(UUID faction, Faction.Option key) {
        assertThat(faction).isNotNull().withFailMessage("FactionDatabase#getOption failed: faction == null");
        assertThat(key).isNotNull().withFailMessage("FactionDatabase#getOption failed: key == null");

        try(CachedRowSet rows = database.query("SELECT * FROM options WHERE faction = ? AND key = ?", faction, key.name())) {
            if(rows == null || rows.size() <= 0) return Optional.empty();
            
            rows.next();
            
            String value = rows.getString("value");
            return Optional.of(new FactionOption(faction, key, value));
        }
        catch (SQLException e) {
            e.printStackTrace();
            
            return Optional.empty();
        }
    }


    public void setOption(UUID faction, Faction.Option key, String value) {
        try {
            database.execute("""
                IF EXISTS (SELECT 1 FROM options WHERE faction = ? AND key = ?)  
                BEGIN  
                    UPDATE options SET value = ? WHERE faction = ? AND key = ?;  
                END 
                ELSE 
                BEGIN 
                    INSERT INTO options(faction, key, value) VALUES(?, ?, ?);
                END
            """, faction, key.name(), value, faction, key.name(), faction, key.name(), value);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    } 


    public void close() {
        database.disconnect();
        database = null;
    }
}
