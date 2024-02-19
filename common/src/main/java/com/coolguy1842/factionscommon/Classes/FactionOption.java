package com.coolguy1842.factionscommon.Classes;

import java.util.UUID;

public class FactionOption {
    private UUID faction;
    private Faction.Option key;
    private String value;

    public FactionOption(UUID faction, Faction.Option key, String value) {
        this.faction = faction;
        this.key = key;
        this.value = value;
    }

    public UUID getFaction() { return this.faction; }
    public Faction.Option getKey() { return this.key; }
    public String getValue() { return this.value; } 
    
    public void setValue(String value) { this.value = value; } 
}
