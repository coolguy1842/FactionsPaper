package com.coolguy1842.factionscommon.Classes;

import java.util.UUID;

public class Invite {
    public enum InviteType {
        FACTION,
        ALLY
    }

    private UUID inviter;
    private UUID invited;
    private InviteType type;

    public Invite(UUID inviter, UUID invited, InviteType type) {
        this.inviter = inviter;
        this.invited = invited;

        this.type = type;
    }

    public UUID getInviter() { return inviter; }
    public UUID getInvited() { return invited; }
    
    public InviteType getType() { return type; }
}
