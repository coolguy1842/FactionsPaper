package com.coolguy1842.factionscommon.Databases;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.sql.rowset.CachedRowSet;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import com.coolguy1842.factionscommon.Classes.Database;
import com.coolguy1842.factionscommon.Classes.Home;
import com.coolguy1842.factionscommon.Classes.Home.OwnerType;

public class HomeDatabase implements DatabaseHandler {
    @Override public String getName() { return "homes"; }

    private Database database;
    @Override public Database getDatabase() { return database; }

    public HomeDatabase(Path configPath) {
        database = new Database(configPath.resolve(getName() + ".db").toString());

        initTables();
    }

    public void initTables() {
        try {
            database.execute("""
                CREATE TABLE IF NOT EXISTS homes (
                    id CHAR(36) PRIMARY KEY,
                    name TEXT NOT NULL,
                    location TEXT NOT NULL,
                    owner CHAR(36) NOT NULL,
                    ownerType TEXT NOT NULL
                );  
            """);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Home> getHomes() {
        List<Home> out = new ArrayList<>();
        try(CachedRowSet rows = database.query("SELECT * FROM homes")) {
            if(rows == null || rows.size() <= 0) return out;
            
            while(rows.next()) {
                UUID id = UUID.fromString(rows.getString("id"));
                String name = rows.getString("name");
                String location = rows.getString("location");

                UUID owner = UUID.fromString(rows.getString("owner"));
                OwnerType ownerType = OwnerType.valueOf(rows.getString("ownerType"));
                

                out.add(new Home(id, name, location, owner, ownerType));
            }
        }
        catch (SQLException e) { e.printStackTrace();}

        return out;
    }


    public Optional<Home> addHome(UUID id, String name, String location, UUID owner, OwnerType ownerType) {
        assertThat(id != null).isTrue().withFailMessage("HomeDatabase#addHome failed: id == null");
        assertThat(name != null).isTrue().withFailMessage("HomeDatabase#addHome failed: name == null");
        assertThat(location != null).isTrue().withFailMessage("HomeDatabase#addHome failed: location == null");
        assertThat(owner != null).isTrue().withFailMessage("HomeDatabase#addHome failed: owner == null");
        assertThat(ownerType != null).isTrue().withFailMessage("HomeDatabase#addHome failed: ownerType == null");

        try {
            database.execute(
                "INSERT INTO homes(id, name, location, owner, ownerType) VALUES(?, ?, ?, ?, ?)",
                id, name, location, owner, ownerType
            );

            return Optional.of(new Home(id, name, location, owner, ownerType));
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Boolean removeHome(UUID id) {
        assertThat(id != null).isTrue().withFailMessage("HomeDatabase#removeHome failed: id == null");

        try {
            database.execute("DELETE FROM homes WHERE id = ?", id);
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public void setHomeName(UUID id, String name) {
        assertThat(id != null).isTrue().withFailMessage("HomeDatabase#setHomeName failed: id == null");
        assertThat(name != null).isTrue().withFailMessage("HomeDatabase#setHomeName failed: name == null");

        try {
            database.execute("UPDATE homes SET name = ? WHERE id = ?", name, id);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void setHomeLocation(UUID id, String location) {
        assertThat(id != null).isTrue().withFailMessage("HomeDatabase#setHomeName failed: id == null");
        assertThat(location != null).isTrue().withFailMessage("HomeDatabase#setHomeName failed: location == null");

        try {
            database.execute("UPDATE homes SET location = ? WHERE id = ?", location, id);
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
