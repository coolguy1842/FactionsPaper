package com.coolguy1842.factionscommon.Databases;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.sql.rowset.CachedRowSet;

import com.coolguy1842.factionscommon.Classes.Database;
import com.coolguy1842.factionscommon.Classes.Rank;

public class RankDatabase implements DatabaseHandler {
    @Override public String getName() { return "ranks"; }
    
    private Database database;
    @Override public Database getDatabase() { return database; }

    public RankDatabase(Path configPath) {
        database = new Database(configPath.resolve(getName() + ".db").toString());

        initTables();
    }

    public void initTables() {
        try {
            database.execute("""
                CREATE TABLE IF NOT EXISTS ranks (
                    id CHAR(36) NOT NULL,
                    faction CHAR(36) NOT NULL,
                    name TEXT NOT NULL,
                    permissions TEXT NOT NULL
                );                  
            """);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public Optional<Rank> addRank(UUID id, UUID faction, String name, String permissions) {
        assertThat(id).isNotNull().withFailMessage("RankDatabase#addRank failed: id == null");
        assertThat(faction).isNotNull().withFailMessage("RankDatabase#addRank failed: faction == null");
        assertThat(name).isNotNull().withFailMessage("RankDatabase#addRank failed: name == null");
        assertThat(permissions).isNotNull().withFailMessage("RankDatabase#addRank failed: permissions == null");

        try {
            database.execute(
                "INSERT INTO ranks(id, faction, name, permissions) VALUES(?, ?, ?, ?)",
                id, faction, name, permissions
            );

            return Optional.of(new Rank(id, faction, name, permissions));
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Boolean removeRank(UUID id) {
        assertThat(id).isNotNull().withFailMessage("RankDatabase#removeRank failed: id == null");

        try {
            database.execute(
                "DELETE FROM ranks WHERE id = ?",
                id
            );

            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Rank> getRanks() {
        List<Rank> out = new ArrayList<>();
        
        try {
            CachedRowSet rows = database.query("SELECT id FROM ranks");

            while(rows.next()) {
                Optional<Rank> rank = getRank(UUID.fromString(rows.getString("id")));
                if(!rank.isPresent()) continue;

                out.add(rank.get());
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return out;
    }
    
    public Optional<Rank> getRank(UUID faction, String name) {
        assertThat(faction).isNotNull().withFailMessage("RankDatabase#getRank failed: faction == null");
        assertThat(name).isNotNull().withFailMessage("RankDatabase#getRank failed: name == null");

        try(CachedRowSet rows = database.query("SELECT id FROM ranks WHERE faction = ? AND name = ?", faction, name)) {
            if(rows == null || rows.size() <= 0) return Optional.empty();
            rows.next();

            return getRank(UUID.fromString(rows.getString("id")));
        }
        catch (SQLException e) {
            e.printStackTrace();
            
            return Optional.empty();
        }
    }

    public Optional<Rank> getRank(UUID id) {
        assertThat(id).isNotNull().withFailMessage("RankDatabase#getRank failed: id == null");

        try (CachedRowSet rows = database.query("SELECT * FROM ranks WHERE id = ?", id)) {
            if(rows == null || rows.size() <= 0) return Optional.empty();
            
            rows.next();
            UUID faction = UUID.fromString(rows.getString("faction"));
            String name = rows.getString("name");
            String permissionsStr = rows.getString("permissions");

            Set<String> permissions = new HashSet<String>(Arrays.asList(permissionsStr.split(",")));

            return Optional.of(new Rank(id, faction, name, permissions));
        } catch (SQLException e) {
            e.printStackTrace();
            
            return Optional.empty();
        }
    }

    
    public void setRankName(UUID id, String name) {
        assertThat(id).isNotNull().withFailMessage("RankDatabase#setRankName failed: id == null");
        assertThat(name).isNotNull().withFailMessage("RankDatabase#setRankName failed: name == null");
        
        try { database.execute("UPDATE ranks SET name = ? WHERE id = ?", name, id); }
        catch (SQLException e) { e.printStackTrace(); }
    }
    
    public void setRankPermissions(UUID id, String permissions) {
        assertThat(id).isNotNull().withFailMessage("RankDatabase#setRankPermissions failed: id == null");
        assertThat(permissions).isNotNull().withFailMessage("RankDatabase#setRankPermissions failed: permissions == null");
        
        try { database.execute("UPDATE ranks SET permissions = ? WHERE id = ?", permissions, id); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    
    public void close() {
        database.disconnect();
        database = null;
    }
}
