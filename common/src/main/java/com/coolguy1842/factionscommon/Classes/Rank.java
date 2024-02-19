package com.coolguy1842.factionscommon.Classes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Rank {
    public enum RankPermissions {
        INVITE_PLAYER,
        ADMIN
    };

    private UUID id;
    private UUID faction;
    private String name;
    private Set<String> permissions;

    
    public Rank(UUID id, UUID faction, String name, String permissions) {
        this.id = id;
        this.faction = faction;
        this.name = name;

        if(permissions.length() <= 0) this.permissions = new HashSet<>();
        else this.permissions = new HashSet<String>(Arrays.asList(permissions.split(",")));
    }

    public Rank(UUID id, UUID faction, String name, Set<String> permissions) {
        this.id = id;
        this.faction = faction;
        this.name = name;
        this.permissions = permissions;
    }

    public UUID getID() { return id; }
    public UUID getFaction() { return faction; }
    public String getName() { return name; }
    
    public Set<String> getPermissions() { return permissions; }
    public boolean hasPermission(String permission) { return permissions.contains(permission); }
    
    
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
    public void setPermissions(String permissions) {
        if(permissions.length() <= 0) this.permissions = new HashSet<>();
        else this.permissions = new HashSet<String>(Arrays.asList(permissions.split(",")));
    }

    public void setName(String name) { this.name = name; }
}
