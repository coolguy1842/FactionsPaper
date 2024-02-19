package com.coolguy1842.factionscommon.Classes;

import java.util.UUID;

public class FactionPlayer {
    // dont change these variables without the manager otherwise weird things may happen
    private UUID uuid;
    private Long balance;
    
    private UUID faction;
    private UUID rank;

    public FactionPlayer(UUID uuid, Long balance, UUID faction, UUID rank) {
        this.uuid = uuid;
        this.balance = balance;
        
        this.faction = faction;
        this.rank = rank;
    }


    public UUID getID()      { return uuid;    }
    public Long getBalance() { return balance; }
    public UUID getFaction() { return faction; }
    public UUID getRank()    { return rank;    }
    
    public void setID(UUID uuid)         { this.uuid    = uuid;    }
    public void setBalance(Long balance) { this.balance = balance; }
    public void setFaction(UUID faction) { this.faction = faction; }
    public void setRank(UUID rank)       { this.rank    = rank;    }
}
