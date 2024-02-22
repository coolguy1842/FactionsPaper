package com.coolguy1842.factionscommon.Classes;

import java.util.Objects;
import java.util.UUID;

public class Claim {
    private UUID id;
    private UUID faction;

    private UUID world;
    private Long chunkKey;

    
    public Claim(UUID id, UUID faction, UUID world, Long chunkKey) {
        this.id = id;
        this.faction = faction;

        this.world = world;
        this.chunkKey = chunkKey;
    }


    public UUID getID      () { return id;       }
    public UUID getFaction () { return faction;  }
    public UUID getWorld   () { return world;    }
    public Long getChunkKey() { return chunkKey; }
    
    public void setWorld   (UUID world) { this.world    = world;    }
    public void setChunkKey(Long chunkKey)     { this.chunkKey = chunkKey; }
    public Integer getLocationHash() { return Objects.hash(world, chunkKey); }


    public static Integer getLocationHash(UUID worldID, Long chunkKey) {
        return Objects.hash(worldID, chunkKey);
    }
}
