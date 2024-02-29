package com.coolguy1842.factions.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TPAManager {
    private static TPAManager _instance;

    public static enum TPARequestType {
        TPA,
        TPAHERE
    }

    public static class TPARequest {
        private UUID sender;
        private TPARequestType type;

        public TPARequest(UUID sender, TPARequestType type) {
            this.sender = sender;
            this.type = type;
        }


        public UUID getSender() { return sender; }
        public TPARequestType getType() { return type; }
    }


    // reciever, sender
    private Map<UUID, TPARequest> tpaRequests;


    private TPAManager() {
        this.tpaRequests = new HashMap<>();
    }


    public List<UUID> getReceiversWithSender(UUID sender) {
        List<UUID> out = new ArrayList<>();

        for(Map.Entry<UUID, TPARequest> entry : tpaRequests.entrySet()) {
            if(entry.getValue().getSender().equals(sender)) {
                out.add(entry.getKey());
            }
        }

        return out;
    }

    public Optional<TPARequest> getRequest(UUID receiver) {
        if(!this.tpaRequests.containsKey(receiver)) return Optional.empty();

        return Optional.of(this.tpaRequests.get(receiver));
    }
    
    public void newRequest(UUID receiver, UUID sender, TPARequestType requestType) {
        this.tpaRequests.put(receiver, new TPARequest(sender, requestType));
    }

    public void removeRequest(UUID receiver) {
        this.tpaRequests.remove(receiver);
    }




    public static TPAManager getInstance() {
        if(_instance == null) _instance = new TPAManager();
        return _instance;
    }


    public void close() {
        tpaRequests.clear();
        tpaRequests = null;

        _instance = null;
    }
}
