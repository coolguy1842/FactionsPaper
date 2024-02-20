package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Rank;

import net.kyori.adventure.text.Component;

public class FactionRankRemoveCommand {
    public static final class Requirement implements FactionRequirement.Interface {
        public Map<String, Component> getErrorMessages() {
            return Map.ofEntries(
                Map.entry("notPlayer", Component.text("Only players can use this!")),
                Map.entry("error", Component.text("Error"))
            );
        }

        @Override
        public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) {
            if(!(ctx.sender() instanceof Player)) {
                return getErrorMessages().get("notPlayer");
            }

            return getErrorMessages().get("error");
        }

        @Override
        public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) {
            return ctx.sender() instanceof Player;
        }
    }

    public static void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();

        Rank rank = ctx.get("name");
        FactionUtil.broadcast(
            player.getServer(), faction.getID(),
            MessageUtil.format("{} {} has removed the rank named {}.", Factions.getPrefix(), player.displayName(), Component.text(rank.getName()))
        );

        for(FactionPlayer fP : Factions.getFactionsCommon().playerManager.getPlayersWithRank(rank.getID())) {
            Factions.getFactionsCommon().playerManager.setPlayerRank(fP.getID(), null);

            Player p = Bukkit.getPlayer(fP.getID());
            if(p == null || !p.isOnline()) continue;
            
            PlayerUtil.updatePlayerPermissions(p);
        }

        Factions.getFactionsCommon().rankManager.removeRank(rank.getID());
    }
}
