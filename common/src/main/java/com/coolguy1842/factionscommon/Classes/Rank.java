package com.coolguy1842.factionscommon.Classes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Rank {
    public enum RankPermission {
        ADMIN,
        INVITE,
        KICK,
        SET_HOME,
        DEL_HOME,
        CREATE_VAULT,
        REMOVE_VAULT,
        OPEN_VAULT,
        CLAIM,
        UNCLAIM,
        WITHDRAW
    };

    private UUID id;
    private UUID faction;
    private String name;
    private Set<RankPermission> permissions;

    
    public Rank(UUID id, UUID faction, String name, String permissions) {
        this.id = id;
        this.faction = faction;
        this.name = name;

        setPermissions(permissions);
    }

    public Rank(UUID id, UUID faction, String name, Set<RankPermission> permissions) {
        this.id = id;
        this.faction = faction;
        this.name = name;
        
        setPermissions(permissions);
    }

    public UUID getID() { return id; }
    public UUID getFaction() { return faction; }
    public String getName() { return name; }
    
    public Set<RankPermission> getPermissions() { return permissions; }
    public boolean hasPermission(String permission) { return permissions.contains(RankPermission.valueOf(permission)); }
    public boolean hasPermission(RankPermission permission) { return permissions.contains(permission); }
    
    
    public void setPermissions(Set<RankPermission> permissions) { this.permissions = permissions; }
    public void setPermissions(String permissions) {
        if(permissions.length() <= 0) this.permissions = new HashSet<>();
        else this.permissions = new HashSet<RankPermission>(Arrays.asList(permissions.split(",")).stream().map(x -> RankPermission.valueOf(x)).toList());
    }

    public void setName(String name) { this.name = name; }
}
