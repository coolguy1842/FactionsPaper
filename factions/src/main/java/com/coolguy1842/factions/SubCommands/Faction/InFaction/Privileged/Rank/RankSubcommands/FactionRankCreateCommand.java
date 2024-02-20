package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands;

import java.util.Map;
import java.util.UUID;

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

import net.kyori.adventure.text.Component;

public class FactionRankCreateCommand {
    public static final class Requirement implements FactionRequirement.Interface {
        public Map<String, Component> getErrorMessages() {
            return Map.ofEntries(
                Map.entry("notPlayer", Component.text("Only players can use this!")),
                Map.entry("rankExists", Component.text("Rank named {} already exists!")),
                Map.entry("error", Component.text("Error"))
            );
        }

        @Override
        public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) {
            if(!(ctx.sender() instanceof Player)) {
                return getErrorMessages().get("notPlayer");
            }

            Player sender = (Player)ctx.sender();
            FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(sender.getUniqueId());
            
            String rankName = ctx.get("name");
            if(Factions.getFactionsCommon().rankManager.getRank(factionPlayer.getFaction(), rankName).isPresent()) {
                return MessageUtil.format(getErrorMessages().get("rankExists"), Component.text(rankName));
            }


            return getErrorMessages().get("error");
        }

        @Override
        public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) {
            if(!(ctx.sender() instanceof Player)) {
                return false;
            }
            
            Player sender = (Player)ctx.sender();
            FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(sender.getUniqueId());
            
            String rankName = ctx.get("name");
            return !Factions.getFactionsCommon().rankManager.getRank(factionPlayer.getFaction(), rankName).isPresent();
        }
    }

    public static void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();


        String rankName = ctx.get("name");
        Factions.getFactionsCommon().rankManager.addRank(UUID.randomUUID(), faction.getID(), rankName);

        FactionUtil.broadcast(
            player.getServer(), faction.getID(),
            MessageUtil.format("{} {} has created a new rank named {}!", Factions.getPrefix(), player.displayName(), Component.text(rankName))
        );
    }
}
