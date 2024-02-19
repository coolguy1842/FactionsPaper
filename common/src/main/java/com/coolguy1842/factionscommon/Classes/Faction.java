package com.coolguy1842.factionscommon.Classes;

import java.util.UUID;

public class Faction {
    public enum Option {
        DEFAULT_RANK,
        COLOUR
    }

    private UUID id;
    private String name;

    private Long balance;
    private UUID leader;

    public Faction(UUID id, String name, Long balance, UUID leader) {
        this.id = id;
        this.name = name;
        this.leader = leader;
        this.balance = balance;
    }

    public UUID   getID()      { return this.id;      }
    public String getName()    { return this.name;    }
    public Long   getBalance() { return this.balance; }
    public UUID   getLeader()  { return this.leader;  }
    
    public void setID(UUID id)           { this.id      = id;      }
    public void setName(String name)     { this.name    = name;    }
    public void setBalance(Long balance) { this.balance = balance; }
    public void setLeader(UUID leader)   { this.leader  = leader;  }
}
