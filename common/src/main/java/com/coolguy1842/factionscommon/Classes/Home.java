package com.coolguy1842.factionscommon.Classes;

import java.util.UUID;

public class Home {
    public static enum OwnerType {
        FACTION,
        PLAYER
    }

    private UUID id;
    private String name;
    private String location;
    private UUID owner;
    private OwnerType ownerType;

    public Home(UUID id, String name, String location, UUID owner, OwnerType ownerType) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.owner = owner;
        this.ownerType = ownerType;
    }


    public UUID      getID       () { return id; }
    public String    getName     () { return name; }
    public String    getLocation () { return location; }
    public UUID      getOwner    () { return owner; }
    public OwnerType getOwnerType() { return ownerType; }

    public void setID       (UUID id)             { this.id        = id;        }
    public void setName     (String name)         { this.name      = name;      }
    public void setLocation (String location)     { this.location  = location;  }
    public void setOwner    (UUID owner)          { this.owner     = owner;     }
    public void setOwnerType(OwnerType ownerType) { this.ownerType = ownerType; }
}
