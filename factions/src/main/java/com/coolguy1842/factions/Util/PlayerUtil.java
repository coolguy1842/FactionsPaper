package com.coolguy1842.factions.Util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.incendo.cloud.permission.Permission;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;

public class PlayerUtil {
    private static Map<UUID, PermissionAttachment> perms = new HashMap<UUID, PermissionAttachment>();


    public static FactionPlayer createFactionPlayer(UUID playerID) {
        return Factions.getFactionsCommon().playerManager.addPlayer(playerID);
    }

    public static FactionPlayer getFactionPlayer(UUID playerID) {
        Optional<FactionPlayer> factionPlayerOptional = Factions.getFactionsCommon().playerManager.getPlayer(playerID);

        if(factionPlayerOptional.isPresent()) return factionPlayerOptional.get();
        assertThat(Factions.getPlugin().getServer().getPlayer(playerID)).isNotNull().withFailMessage("Player with UUID %s not found!", playerID);

        return createFactionPlayer(playerID);
    }


    public static final class Permissions {
        public static final Permission inFactionPermission = Permission.of("in_faction");
        public static final Permission notInFactionPermission = Permission.of("not_in_faction");
        public static final Permission leaderPermission = Permission.of("leader"); 
    }

    public static void updatePlayerPermissions(Player player) {
        FactionPlayer factionPlayer = getFactionPlayer(player.getUniqueId());
        Optional<Faction> factionOptional = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction());

        if(!perms.containsKey(player.getUniqueId())) perms.put(player.getUniqueId(), player.addAttachment(Factions.getPlugin()));
        PermissionAttachment attachment = perms.get(player.getUniqueId());

        attachment.setPermission(Permissions.notInFactionPermission.permissionString(), factionPlayer.getFaction() == null);
        attachment.setPermission(Permissions.inFactionPermission.permissionString(), factionPlayer.getFaction() != null);

        if(factionOptional.isPresent()) {
            attachment.setPermission(Permissions.leaderPermission.permissionString(), factionOptional.get().getLeader().equals(player.getUniqueId()));
        }
        else attachment.unsetPermission(Permissions.leaderPermission.permissionString());

        player.updateCommands();
    }

    public static void removePlayerAttachment(Player player) {
        if(!perms.containsKey(player.getUniqueId())) return;

        player.removeAttachment(perms.get(player.getUniqueId()));
        perms.remove(player.getUniqueId());
    }
}
