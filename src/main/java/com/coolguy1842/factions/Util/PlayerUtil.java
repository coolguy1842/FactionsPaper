package com.coolguy1842.factions.Util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.incendo.cloud.permission.Permission;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Rank;
import com.coolguy1842.factionscommon.Classes.Faction.Option;
import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

import net.kyori.adventure.text.Component;

public class PlayerUtil {
    private static Map<UUID, PermissionAttachment> perms = new HashMap<UUID, PermissionAttachment>();


    public static FactionPlayer createFactionPlayer(UUID playerID) {
        return Factions.getFactionsCommon().playerManager.addPlayer(playerID);
    }

    public static FactionPlayer getFactionPlayer(UUID playerID) {
        Optional<FactionPlayer> factionPlayerOptional = Factions.getFactionsCommon().playerManager.getPlayer(playerID);

        if(factionPlayerOptional.isPresent()) return factionPlayerOptional.get();
        assertThat(Factions.getPlugin().getServer().getOfflinePlayer(playerID)).isNotNull().withFailMessage("Player with UUID %s not found!", playerID);

        return createFactionPlayer(playerID);
    }


    public static Boolean playerHasPermission(Player player, Permission permission) {
        FactionPlayer factionPlayer = getFactionPlayer(player.getUniqueId());
        Optional<Faction> factionOptional = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction());

        if(permission.equals(PlayerPermissions.notInFaction)) return !factionOptional.isPresent();
        if(permission.equals(PlayerPermissions.inFaction) && !factionOptional.isPresent()) return false;
        
        Boolean isLeader = factionOptional.isPresent() ? factionOptional.get().getLeader().equals(factionPlayer.getID()) : false;

        if(permission.equals(PlayerPermissions.leader) && !isLeader) return false;
        if(permission.equals(PlayerPermissions.notLeader) && isLeader) return false;


        Optional<Rank> rankOptional = Factions.getFactionsCommon().rankManager.getRank(factionPlayer.getRank());
        for(RankPermission rankPermission : RankPermission.values()) {
            Permission perm = PlayerPermissions.rankPermission(rankPermission);
            Permission notPerm = PlayerPermissions.notRankPermission(rankPermission);

            if(permission.equals(perm)) {
                if(isLeader) return true;
                
                if(!rankOptional.isPresent()) return false;
                if(rankOptional.isPresent() && rankOptional.get().hasPermission(rankPermission)) return true;
            }
            
            if(permission.equals(notPerm)) {
                if(isLeader) return false;
                
                if(!rankOptional.isPresent()) return true;
                if(rankOptional.isPresent() && rankOptional.get().hasPermission(rankPermission)) return false;
            }
        }

        return true;
    } 



    public static final class PlayerPermissions {
        public static final Permission inFaction = Permission.of("in_faction");
        public static final Permission notInFaction = Permission.of("not_in_faction");
        public static final Permission leader = Permission.of("leader");
        public static final Permission notLeader = Permission.of("not_leader");

        public static Permission rankPermission(RankPermission permission) {
            return Permission.of("rank_permission_" + permission.name());
        }

        public static Permission notRankPermission(RankPermission permission) {
            return Permission.of("not_rank_permission_" + permission.name());
        }
    }

    public static void updatePlayerPermissions(Player player) {
        if(!perms.containsKey(player.getUniqueId())) perms.put(player.getUniqueId(), player.addAttachment(Factions.getPlugin()));
        PermissionAttachment attachment = perms.get(player.getUniqueId());

        attachment.setPermission(PlayerPermissions.notInFaction.permissionString(), playerHasPermission(player, PlayerPermissions.notInFaction));
        attachment.setPermission(PlayerPermissions.inFaction.permissionString(), playerHasPermission(player, PlayerPermissions.inFaction));
        
        attachment.setPermission(PlayerPermissions.notLeader.permissionString(), playerHasPermission(player, PlayerPermissions.notLeader));
        attachment.setPermission(PlayerPermissions.leader.permissionString(), playerHasPermission(player, PlayerPermissions.leader));


        for(RankPermission rankPermission : RankPermission.values()) {
            Permission perm = PlayerPermissions.rankPermission(rankPermission);
            Permission notPerm = PlayerPermissions.notRankPermission(rankPermission);

            Boolean hasPerm = playerHasPermission(player, perm);
            attachment.setPermission(notPerm.permissionString(), !hasPerm);
            attachment.setPermission(perm.permissionString(), hasPerm);
        }

        player.updateCommands();
    }

    public static void removePlayerAttachment(Player player) {
        if(!perms.containsKey(player.getUniqueId())) return;

        player.removeAttachment(perms.get(player.getUniqueId()));
        perms.remove(player.getUniqueId());
    }


    public static void acceptInvite(Server server, UUID inviterFaction, UUID invitedID) {
        Player player = server.getPlayer(invitedID);
        if(player == null) return;

        Factions.getFactionsCommon().inviteManager.removeInvitesWithInvited(invitedID);
        Factions.getFactionsCommon().playerManager.setPlayerFaction(invitedID, inviterFaction);

        Optional<String> defaultRank = Factions.getFactionsCommon().factionManager.getOptionValue(inviterFaction, Option.DEFAULT_RANK);
        if(defaultRank.isPresent()) {
            Factions.getFactionsCommon().playerManager.setPlayerRank(invitedID, UUID.fromString(defaultRank.get()));;
        }

        PlayerUtil.updatePlayerPermissions(player);
        FactionUtil.updateFactionsPlayerTabNames(inviterFaction);

        FactionUtil.broadcast(
            player.getServer(), inviterFaction,
            MessageUtil.format("{} {} has joined the faction!", FactionUtil.getFactionNameAsPrefix(Factions.getFactionsCommon().factionManager.getFaction(inviterFaction).get()), Component.text(player.getName()))
        );
    }
    
    public static void rejectInvite(Server server, UUID inviterFaction, UUID invitedID) {
        Player player = server.getPlayer(invitedID);
        if(player == null) return;

        Optional<Faction> factionOptional = Factions.getFactionsCommon().factionManager.getFaction(inviterFaction);
        if(!factionOptional.isPresent()) return;

        Factions.getFactionsCommon().inviteManager.removeInvitesWithInvited(invitedID);

        FactionUtil.broadcast(
            player.getServer(), inviterFaction,
            MessageUtil.format("{} {} rejected the invite to the faction!", FactionUtil.getFactionNameAsPrefix(Factions.getFactionsCommon().factionManager.getFaction(inviterFaction).get()), Component.text(player.getName()))
        );

        player.sendMessage(MessageUtil.format("{} You have rejected the invite to {}!", Factions.getPrefix(), Component.text(factionOptional.get().getName())));
    }


    public static void teleportPlayer(Player player, Location location) {
        if(player == null || !player.isOnline()) return;

        if(!player.isInsideVehicle()) {
            player.setFallDistance(-Float.MAX_VALUE);
            player.teleportAsync(location, TeleportCause.PLUGIN);

            return;
        }
        
        Bukkit.getScheduler().runTaskLater(Factions.getPlugin(), () -> {
            Entity vehicle = player.getVehicle();
            List<Entity> passengers = vehicle.getPassengers();
            
            for(Entity passenger : passengers) {
                passenger.leaveVehicle();
                
                if(passenger instanceof Player) {
                    ((Player)passenger).hideEntity(Factions.getPlugin(), vehicle);
                }

                passenger.teleport(location, TeleportCause.PLUGIN);
            }
            
            vehicle.teleport(location, TeleportCause.PLUGIN);

            Bukkit.getScheduler().runTaskLater(Factions.getPlugin(), () -> {
                for(Entity passenger : passengers) {
                    if(passenger instanceof Player) {
                        ((Player)passenger).showEntity(Factions.getPlugin(), vehicle);
                    }

                    vehicle.addPassenger(passenger);
                }
            }, 4);
        }, 0);
    }

    
    public static Integer getAmountItem(Player player, Material item) {
        Integer out = 0;
        for(ItemStack itemStack : player.getInventory().getContents()) {
            if(itemStack == null || itemStack.isEmpty()) continue;
            if(itemStack.getType().equals(item)) out += itemStack.getAmount();
        }

        return out;
    }
    
    public static void removeItemAmount(Player player, Material item, Integer amount) {
        Integer slot = -1;
        for(ItemStack itemStack : player.getInventory().getContents()) {
            slot++;
            
            if(itemStack == null || itemStack.isEmpty()) continue;
            if(!itemStack.getType().equals(item)) continue;

            Integer itemAmount = itemStack.getAmount();
            if(amount > itemAmount) {
                player.getInventory().clear(slot);
            }
            else {
                itemStack.setAmount(itemStack.getAmount() - amount);
                return;
            }

            amount -= itemAmount;
        }
    }


    public static Component playerGlobalName(OfflinePlayer player) {
        FactionPlayer factionPlayer = getFactionPlayer(player.getUniqueId());
        
        Optional<Faction> faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction());
        if(faction.isPresent()) {
            return MessageUtil.format("{} {}", FactionUtil.getFactionNameAsPrefix(faction.get()), Component.text(player.getName()));   
        }

        return Component.text(player.getName());
    }


    public static void updatePlayerTabName(Player player) {
        player.playerListName(playerGlobalName(player));
    }
}
