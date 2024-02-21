package com.coolguy1842.factionscommon.Managers;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.coolguy1842.factionscommon.Classes.Invite;
import com.coolguy1842.factionscommon.Classes.Invite.InviteType;
import com.coolguy1842.factionscommon.Databases.InviteDatabase;

public class InviteManager {
    public InviteDatabase database;
    private List<Invite> invitesList;

    void addToCache(Invite invite) {
        invitesList.add(invite);
    }
    
    void removeFromCache(Invite invite) {
        invitesList.remove(invite);
    }


    public InviteManager(Path configPath) {
        database = new InviteDatabase(configPath);

        reload();
    }

    private void loadInvites() {
        for(Invite invite : database.getInvites()) {
            addToCache(invite);
        }
    }



    public List<Invite> getInvites() { return invitesList; }
    public Optional<Invite> getInvite(UUID inviter, UUID invited) {
        assertThat(inviter != null).isTrue().withFailMessage("InviteManager#getInvite failed: inviter == null");
        assertThat(invited != null).isTrue().withFailMessage("InviteManager#getInvite failed: invited == null");

        return invitesList.stream().filter(x -> x.getInviter().equals(inviter) && x.getInvited().equals(invited)).findFirst();
    }

    public List<Invite> getInvitesWithInviter(UUID inviter) {
        assertThat(inviter != null).isTrue().withFailMessage("InviteManager#getInvitesWithInviter failed: inviter == null");
        return invitesList.stream().filter(x -> x.getInviter().equals(inviter)).toList();
    }

    public List<Invite> getInvitesWithInvited(UUID invited) {
        assertThat(invited != null).isTrue().withFailMessage("InviteManager#getInvitesWithInvited failed: invited == null");
        return invitesList.stream().filter(x -> x.getInvited().equals(invited)).toList();
    }


    public Invite addInvite(UUID inviter, UUID invited, InviteType type) {
        assertThat(inviter != null).isTrue().withFailMessage("InviteManager#addInvite failed: inviter == null");
        assertThat(invited != null).isTrue().withFailMessage("InviteManager#addInvite failed: invited == null");
        assertThat(type != null).isTrue().withFailMessage("InviteManager#addInvite failed: type == null");

        Optional<Invite> inviteOptional = database.addInvite(inviter, invited, type);
        assertThat(inviteOptional.isPresent()).isTrue().withFailMessage("InviteManager#addInvite failed: invite with inviter: %s, invited: %s, type: %s not created.", inviter, invited, type);

        Invite invite = inviteOptional.get();
        addToCache(invite);

        return invite;
    }

    
    public void removeInvitesWithInvited(UUID invited) {
        assertThat(invited != null).isTrue().withFailMessage("InviteManager#removeInvitesWithInvited failed: invited == null");

        assertThat(database.removeInviteWithInvited(invited)).isTrue().withFailMessage("InviteManager#removeInviteWithInvited failed with invited: %s", invited);
        (new ArrayList<>(invitesList)).stream().filter(invite -> invite.getInvited().equals(invited)).forEach(invite -> {
            removeFromCache(invite);
        });
    }
    
    public void removeInvitesWithInviter(UUID inviter) {
        assertThat(inviter != null).isTrue().withFailMessage("InviteManager#removeInvitesWithInviter failed: inviter == null");

        assertThat(database.removeInviteWithInviter(inviter)).isTrue().withFailMessage("InviteManager#removeInviteWithInviter failed with inviter: %s", inviter);
        (new ArrayList<>(invitesList)).stream().filter(invite -> invite.getInvited().equals(inviter)).forEach(invite -> {
            removeFromCache(invite);
        });
    }

    public void removeInvite(UUID inviter, UUID invited) {
        assertThat(inviter != null).isTrue().withFailMessage("InviteManager#removeInvite failed: inviter == null");
        assertThat(invited != null).isTrue().withFailMessage("InviteManager#removeInvite failed: invited == null");

        assertThat(database.removeInvite(inviter, invited)).isTrue().withFailMessage("InviteManager#removeInvite failed with inviter: %s & invited: %s", inviter, invited);
        removeFromCache(getInvite(inviter, invited).get());
    }
    

    public void reload() {
        invitesList = new ArrayList<>();
        loadInvites();
    }

    public void close() {
        database.close();
        database = null;

        invitesList = null;
    }
}
