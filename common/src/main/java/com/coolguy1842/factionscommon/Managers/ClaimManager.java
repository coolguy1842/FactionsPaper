package com.coolguy1842.factionscommon.Managers;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.coolguy1842.factionscommon.Classes.Claim;
import com.coolguy1842.factionscommon.Databases.ClaimDatabase;

public class ClaimManager {
    public ClaimDatabase database;
    private Map<UUID, Claim> claims;
    private Map<UUID, List<Claim>> claimsInFactions;
    private Map<Integer, Claim> claimsByLocationHash;
    
    void addToCache(Claim claim) {
        claims.put(claim.getID(), claim);
        claimsByLocationHash.put(claim.getLocationHash(), claim);

        if(!claimsInFactions.containsKey(claim.getFaction())) claimsInFactions.put(claim.getFaction(), new ArrayList<>());
        claimsInFactions.get(claim.getFaction()).add(claim);
    }
    
    void removeFromCache(Claim claim) {
        claims.remove(claim.getID());
        claimsByLocationHash.remove(claim.getLocationHash());

        if(!claims.containsKey(claim.getFaction())) return;
        if(!claimsInFactions.get(claim.getFaction()).contains(claim)) return;
        claimsInFactions.get(claim.getFaction()).remove(claim);
    }
    
    
    public ClaimManager(Path configPath) {
        database = new ClaimDatabase(configPath);
        
        reload();
    }

    private void loadVaults() {
        for(Claim claim : database.getClaims()) {
            addToCache(claim);
        }
    }

    
    public List<Claim> getClaimsInFaction(UUID faction) {
        assertThat(faction).isNotNull().withFailMessage("ClaimManager#getClaimsInFaction failed: faction == null");

        if(!claimsInFactions.containsKey(faction)) return new ArrayList<>();
        return claimsInFactions.get(faction);
    }


    public Optional<Claim> getClaim(UUID id) {
        assertThat(id).isNotNull().withFailMessage("ClaimManager#getVault failed: id == null");

        if(!claims.containsKey(id)) return Optional.empty();
        return Optional.of(claims.get(id));
    }
    
    public Optional<Claim> getClaim(UUID world, Long chunkKey) {
        assertThat(world).isNotNull().withFailMessage("ClaimManager#getClaim failed: world == null");
        assertThat(chunkKey).isNotNull().withFailMessage("ClaimManager#getClaim failed: chunkKey == null");

        Integer locationHash = Claim.getLocationHash(world, chunkKey);
        if(!claimsByLocationHash.containsKey(locationHash)) return Optional.empty();
        return Optional.of(claimsByLocationHash.get(locationHash));
    }

    public Claim addClaim(UUID id, UUID faction, UUID world, Long chunkKey) {
        assertThat(id).isNotNull().withFailMessage("ClaimManager#addClaim failed: id == null");
        assertThat(faction).isNotNull().withFailMessage("ClaimManager#addClaim failed: faction == null");
        assertThat(world).isNotNull().withFailMessage("ClaimManager#addClaim failed: world == null");
        assertThat(chunkKey).isNotNull().withFailMessage("ClaimManager#addClaim failed: chunkKey == null");
        
        Optional<Claim> claimOptional = database.addClaim(id, faction, world, chunkKey);
        assertThat(claimOptional).isPresent().withFailMessage("ClaimManager#addClaim failed: claim with id: %s, faction: %s, world: %s, chunkID: %d not created.", id, faction, world, chunkKey);
        
        Claim claim = claimOptional.get();
        addToCache(claim);

        return claim;
    }

    public void removeClaim(UUID id) {
        assertThat(id).isNotNull().withFailMessage("ClaimManager#removeClaim failed: id == null");

        assertThat(claims).containsKey(id).withFailMessage("ClaimManager#removeClaim failed: claim with id: %s doesn't exist.", id);
        Claim claim = claims.get(id);

        removeFromCache(claim);
        database.removeClaim(id);
    }

    public void removeClaim(UUID world, Long chunkKey) {
        assertThat(world).isNotNull().withFailMessage("ClaimManager#removeClaim failed: world == null");
        assertThat(chunkKey).isNotNull().withFailMessage("ClaimManager#removeClaim failed: chunkKey == null");

        Optional<Claim> claimOptional = getClaim(world, chunkKey);
        assertThat(claimOptional).isPresent().withFailMessage("ClaimManager#removeClaim failed: claim with chunkKey: %d doesn't exist.", chunkKey);

        removeFromCache(claimOptional.get());
        database.removeClaim(claimOptional.get().getID());
    }


    public void setClaimLocation(UUID id, UUID world, Long chunkKey) {
        assertThat(id).isNotNull().withFailMessage("ClaimManager#setClaimLocation failed: id == null");
        assertThat(world).isNotNull().withFailMessage("ClaimManager#setClaimLocation failed: world == null");
        assertThat(chunkKey).isNotNull().withFailMessage("ClaimManager#setClaimLocation failed: chunkKey == null");

        assertThat(claims).containsKey(id).withFailMessage("ClaimManager#setClaimLocation failed: claim with id: %s doesn't exist.", id);
        Claim claim = claims.get(id);

        Integer oldLocationHash = claim.getLocationHash();
        claim.setWorld(world);
        claim.setChunkKey(chunkKey);

        database.setClaimWorld(id, world);
        database.setClaimChunkKey(id, chunkKey);
        
        claimsByLocationHash.remove(oldLocationHash);
        claimsByLocationHash.put(claim.getLocationHash(), claim);
    }
    
    public void setClaimLocation(UUID currentWorld, Long currentChunkKey, UUID world, Long chunkKey) {
        assertThat(currentWorld).isNotNull().withFailMessage("ClaimManager#setClaimLocation failed: currentWorld == null");
        assertThat(currentChunkKey).isNotNull().withFailMessage("ClaimManager#setClaimLocation failed: currentChunkKey == null");

        assertThat(world).isNotNull().withFailMessage("ClaimManager#setClaimLocation failed: world == null");
        assertThat(chunkKey).isNotNull().withFailMessage("ClaimManager#setClaimLocation failed: chunkKey == null");

        Optional<Claim> claimOptional = getClaim(currentWorld, currentChunkKey);
        assertThat(claimOptional).isPresent().withFailMessage("ClaimManager#setVaultName failed: cllaim with world: %s, chunkKey: %d doesn't exist.", currentWorld, currentChunkKey);
        
        Claim claim = claimOptional.get();

        Integer oldLocationHash = claim.getLocationHash();
        setClaimLocation(claim.getID(), world, chunkKey);
        
        claimsByLocationHash.remove(oldLocationHash);
        claimsByLocationHash.put(claim.getLocationHash(), claim);
    }


    public void reload() {
        claims = new HashMap<>();
        claimsByLocationHash = new HashMap<>();
        claimsInFactions = new HashMap<>();

        loadVaults();
    }

    public void close() {
        database.close();
        database = null;

        claims = null;
        claimsByLocationHash = null;
        claimsInFactions = null;
    }
}
