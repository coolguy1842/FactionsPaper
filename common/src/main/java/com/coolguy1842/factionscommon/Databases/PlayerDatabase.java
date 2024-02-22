package com.coolguy1842.factionscommon.Databases;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import java.nio.file.Path;

import com.coolguy1842.factionscommon.Classes.Database;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;

public class PlayerDatabase implements DatabaseHandler {
    @Override public String getName() { return "players"; }
    
    private Database database;
    @Override public Database getDatabase() { return database; }

    public PlayerDatabase(Path configPath) {
        database = new Database(configPath.resolve(getName() + ".db").toString());

        initTables();
    }

    public void initTables() {
        try {
            database.execute("""
                CREATE TABLE IF NOT EXISTS players (
                    balance BIGINT NOT NULL,
                    faction CHAR(36),
                    rank CHAR(36),
                    id CHAR(36) PRIMARY KEY
                );
            """);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<FactionPlayer> addPlayer(UUID id, Long balance, UUID faction, UUID rank) {
        assertThat(id).isNotNull().withFailMessage("PlayerDatabase#addPlayer failed: id == null");
        assertThat(balance).isNotNull().withFailMessage("PlayerDatabase#addPlayer failed: balance == null");

        try {
            database.execute(
                "INSERT INTO players(id, balance, faction, rank) VALUES(?, ?, ?, ?)",
                id, balance, faction, rank
            );

            return Optional.of(new FactionPlayer(id, balance, faction, rank));
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    
    public List<FactionPlayer> getPlayers() {
        List<FactionPlayer> out = new ArrayList<>();
        
        try {
            CachedRowSet rows = database.query("SELECT id FROM players");

            while(rows.next()) {
                Optional<FactionPlayer> player = getPlayer(UUID.fromString(rows.getString("id")));
                if(!player.isPresent()) continue;

                out.add(player.get());
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return out;
    }
    
    public Optional<FactionPlayer> getPlayer(UUID id) {
        assertThat(id).isNotNull().withFailMessage("PlayerDatabase#getPlayer failed: id == null");

        try (CachedRowSet rows = database.query("SELECT * FROM players WHERE id = ?", id)) {
            if(rows == null || rows.size() <= 0) return Optional.empty();
            
            rows.next();
            Long balance = rows.getLong("balance");

            String factionStr = rows.getString("faction");
            String rankStr    = rows.getString("rank");

            UUID faction = null;
            UUID rank    = null;

            if(factionStr != null) faction = UUID.fromString(factionStr);
            if(rankStr    != null) rank    = UUID.fromString(rankStr   );

            return Optional.of(new FactionPlayer(id, balance, faction, rank));
        } catch (SQLException e) {
            e.printStackTrace();
            
            return Optional.empty();
        }
    }

    public void setPlayerBalance(UUID id, Long balance) {
        assertThat(id).isNotNull().withFailMessage("PlayerDatabase#setPlayerBalance failed: id == null");
        assertThat(balance).isNotNull().withFailMessage("PlayerDatabase#setPlayerBalance failed: balance == null");

        try { database.execute("UPDATE players SET balance = ? WHERE id = ?", balance, id); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    public void setPlayerFaction(UUID id, UUID faction) {
        assertThat(id).isNotNull().withFailMessage("PlayerDatabase#setPlayerFaction failed: id == null");
        
        try { database.execute("UPDATE players SET faction = ? WHERE id = ?", faction, id); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    public void setPlayerRank(UUID id, UUID rank) {
        assertThat(id).isNotNull().withFailMessage("PlayerDatabase#setPlayerRank failed: id == null");
        
        try { database.execute("UPDATE players SET rank = ? WHERE id = ?", rank, id); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    
    public void close() {
        database.disconnect();
        database = null;
    }
}
