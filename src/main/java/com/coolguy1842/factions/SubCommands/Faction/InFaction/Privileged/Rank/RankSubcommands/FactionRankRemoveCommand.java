package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Parsers.RankParser;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Rank;

import net.kyori.adventure.text.Component;

public class FactionRankRemoveCommand implements RankSubcommand {
    @Override
    public Builder<CommandSender> getCommand(Builder<CommandSender> baseCommand) {
        return 
            baseCommand.literal("remove")
                .required("name", RankParser.rankParser())
                    .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                    .handler(ctx -> runCommand(ctx));
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
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
