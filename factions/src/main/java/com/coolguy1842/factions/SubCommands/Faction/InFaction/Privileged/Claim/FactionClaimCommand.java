package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Claim;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement.Interface;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factions.interfaces.Subcommand;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

import net.kyori.adventure.text.Component;

public class FactionClaimCommand implements Subcommand {
    public static final Long claimFee = 10L;

    public class Requirement implements Interface {
        public Map<String, Component> getErrorMessages() {
            return Map.ofEntries(
                Map.entry("alreadyClaimed", Component.text("This chunk is already claimed!")),
                Map.entry("lowBalance", Component.text(String.format("Your faction doesn't have enough money! {}/%d", claimFee)))
            );
        }

        @Override
        public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) {
            Player player = (Player)ctx.sender();
            FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
            Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();
            
            if(faction.getBalance() < claimFee) {
                return MessageUtil.format(getErrorMessages().get("lowBalance"), Component.text(faction.getBalance().toString()));
            }

            return getErrorMessages().get("alreadyClaimed");
        }

        @Override
        public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) {
            Player player = (Player)ctx.sender();
            FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
            Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();

            if(faction.getBalance() < claimFee) {
                return false;
            }
            
            return !Factions.getFactionsCommon().claimManager.getClaim(player.getWorld().getUID(), player.getChunk().getChunkKey()).isPresent();
        }
    }

    @Override public String getName() { return "claim"; }
    @Override public String getDescription() { return "Claims the chunk you are in!"; }
    @Override public Permission getPermission() {
        return Permission.allOf(PlayerPermissions.inFaction, PlayerPermissions.rankPermission(RankPermission.CLAIM));
    }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal(getName())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement(), new Requirement()))
                .permission(getPermission())
                .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();

        Factions.getFactionsCommon().claimManager.addClaim(UUID.randomUUID(), faction.getID(), player.getWorld().getUID(), player.getChunk().getChunkKey());
        Factions.getFactionsCommon().factionManager.setFactionBalance(faction.getID(), faction.getBalance() - claimFee);

        FactionUtil.broadcast(
            player.getServer(), faction.getID(),
            MessageUtil.format("{} {} has claimed a chunk!", Factions.getPrefix(), player.displayName())
        );
    }
}
