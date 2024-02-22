package com.coolguy1842.factionscommon.Classes;

import java.util.UUID;

public class Vault {
    private UUID id;
    private UUID faction;

    private String name;
    private String contents;

    public Vault(UUID id, UUID faction, String name, String contents) {
        this.id = id;
        this.faction = faction;
        this.name = name;
        this.contents = contents;
    }

    public UUID   getID      () { return id;       }
    public UUID   getFaction () { return faction;  }
    public String getName    () { return name;     }
    public String getContents() { return contents; }
    
    public void setFaction (UUID faction)    { this.faction  = faction;  }
    public void setName    (String name)     { this.name     = name;     }
    public void setContents(String contents) { this.contents = contents; }
}
