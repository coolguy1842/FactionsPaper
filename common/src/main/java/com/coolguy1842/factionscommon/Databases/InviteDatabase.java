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
import com.coolguy1842.factionscommon.Classes.Invite;
import com.coolguy1842.factionscommon.Classes.Invite.InviteType;

public class InviteDatabase {
    public Database database;
    public InviteDatabase(Path configPath) {
        database = new Database(configPath.resolve("invites.db").toString());

        initTables();
    }

    private void initTables() {
        try {
            database.execute("""
                CREATE TABLE IF NOT EXISTS invites (
                    invited CHAR(36) NOT NULL,
                    inviter CHAR(36) NOT NULL,
                    type TEXT NOT NULL
                );                  
            """);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Invite> getInvites() {
        List<Invite> out = new ArrayList<>();
        try(CachedRowSet rows = database.query("SELECT * FROM invites")) {
            if(rows == null || rows.size() <= 0) return out;
            
            rows.next();

            UUID inviter = UUID.fromString(rows.getString("inviter"));
            UUID invited = UUID.fromString(rows.getString("invited"));

            InviteType type = InviteType.valueOf(rows.getString("type"));

            out.add(new Invite(inviter, invited, type));
        }
        catch (SQLException e) { e.printStackTrace();}

        return out;
    }


    public Optional<Invite> addInvite(UUID inviter, UUID invited, InviteType type) {
        assertThat(inviter != null).isTrue().withFailMessage("InviteDatabase#addInvite failed: inviter == null");
        assertThat(invited != null).isTrue().withFailMessage("InviteDatabase#addInvite failed: invited == null");
        assertThat(type != null).isTrue().withFailMessage("InviteDatabase#addInvite failed: type == null");

        try {
            database.execute(
                "INSERT INTO invites(inviter, invited, type) VALUES(?, ?, ?)",
                inviter, invited, type
            );

            return Optional.of(new Invite(inviter, invited, type));
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    
    public Boolean removeInviteWithInvited(UUID invited) {
        assertThat(invited != null).isTrue().withFailMessage("InviteDatabase#removeInviteWithInvited failed: invited == null");

        try {
            database.execute("DELETE FROM invites WHERE invited = ?", invited);
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Boolean removeInviteWithInviter(UUID inviter) {
        assertThat(inviter != null).isTrue().withFailMessage("InviteDatabase#removeInviteWithInviter failed: inviter == null");

        try {
            database.execute("DELETE FROM invites WHERE inviter = ?", inviter);
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Boolean removeInvite(UUID inviter, UUID invited) {
        assertThat(inviter != null).isTrue().withFailMessage("InviteDatabase#addInvite failed: inviter == null");
        assertThat(invited != null).isTrue().withFailMessage("InviteDatabase#addInvite failed: invited == null");

        try {
            database.execute("DELETE FROM invites WHERE inviter = ? AND invited = ?", inviter, invited);
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    

    public void close() {
        database.disconnect();
        database = null;
    }
}
