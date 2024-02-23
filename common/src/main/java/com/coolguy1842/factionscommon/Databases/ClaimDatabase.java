package com.coolguy1842.factionscommon.Databases;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import java.nio.file.Path;

import javax.sql.rowset.CachedRowSet;

import com.coolguy1842.factionscommon.Classes.Claim;
import com.coolguy1842.factionscommon.Classes.Database;

public class ClaimDatabase implements DatabaseHandler {
    @Override public String getName() { return "claims"; }

    private Database database;
    @Override public Database getDatabase() { return database; }

    public ClaimDatabase(Path configPath) {
        database = new Database(configPath.resolve(getName() + ".db").toString());

        initTables();
    }

    public void initTables() {
        try {
            database.execute("""
                CREATE TABLE IF NOT EXISTS claims (
                    id CHAR(36) PRIMARY KEY,
                    faction CHAR(36) NOT NULL,
                    world CHAR(36) NOT NULL,
                    chunkKey BIGINT NOT NULL
                );
            """);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Optional<Claim> addClaim(UUID id, UUID faction, UUID world, Long chunkKey) {
        assertThat(id).isNotNull().withFailMessage("ClaimDatabase#addClaim failed: id == null");
        assertThat(faction).isNotNull().withFailMessage("ClaimDatabase#addClaim failed: faction == null");
        assertThat(world).isNotNull().withFailMessage("ClaimDatabase#addClaim failed: world == null");
        assertThat(chunkKey).isNotNull().withFailMessage("ClaimDatabase#addClaim failed: chunkKey == null");

        try {
            database.execute(
                "INSERT INTO claims(id, faction, world, chunkKey) VALUES(?, ?, ?, ?)",
                id, faction, world, chunkKey
            );

            return Optional.of(new Claim(id, faction, world, chunkKey));
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Boolean removeClaim(UUID id) {
        assertThat(id).isNotNull().withFailMessage("ClaimDatabase#removeClaim failed: id == null");

        try {
            database.execute("DELETE FROM claims WHERE id = ?", id);
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public List<Claim> getClaims() {
        List<Claim> out = new ArrayList<>();
        
        try {
            CachedRowSet rows = database.query("SELECT id FROM claims");

            while(rows.next()) {
                Optional<Claim> claim = getClaim(UUID.fromString(rows.getString("id")));
                if(!claim.isPresent()) continue;

                out.add(claim.get());
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return out;
    }
    
    public Optional<Claim> getClaim(UUID id) {
        assertThat(id).isNotNull().withFailMessage("ClaimDatabase#getClaim failed: id == null");

        try(CachedRowSet rows = database.query("SELECT * FROM claims WHERE id = ?", id)) {
            if(rows == null || rows.size() <= 0) return Optional.empty();
            
            rows.next();
            
            UUID faction = UUID.fromString(rows.getString("faction"));
            UUID world = UUID.fromString(rows.getString("world"));
            Long chunkKey = rows.getLong("chunkKey");

            return Optional.of(new Claim(id, faction, world, chunkKey));
        }
        catch (SQLException e) {
            e.printStackTrace();
            
            return Optional.empty();
        }
    }


    public void setClaimWorld(UUID id, UUID world) {
        assertThat(id).isNotNull().withFailMessage("ClaimDatabase#setClaimWorld failed: id == null");
        assertThat(world).isNotNull().withFailMessage("ClaimDatabase#setClaimWorld failed: world == null");

        try { database.execute("UPDATE claims SET world = ? WHERE id = ?", world, id); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    public void setClaimChunkKey(UUID id, Long chunkKey) {
        assertThat(id).isNotNull().withFailMessage("ClaimDatabase#setClaimChunkKey failed: id == null");
        assertThat(chunkKey).isNotNull().withFailMessage("ClaimDatabase#setClaimChunkKey failed: chunkKey == null");
        
        try { database.execute("UPDATE claims SET chunkKey = ? WHERE id = ?", chunkKey, id); }
        catch (SQLException e) { e.printStackTrace(); }
    }


    public void close() {
        database.disconnect();
        database = null;
    }
}
