package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Parsers.FactionPlayerParser;
import com.coolguy1842.factions.Parsers.RankParser;
import com.coolguy1842.factions.Parsers.FactionPlayerParser.ParserType;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Rank;

import net.kyori.adventure.text.Component;

public class FactionRankAssignCommand implements RankSubcommand {
    @Override
    public Builder<CommandSender> getCommand(Builder<CommandSender> baseCommand) {
        return
            baseCommand.literal("assign")
                .required("rank", RankParser.rankParser())
                    .required("player", FactionPlayerParser.withOptions(ParserType.INCLUDES_OFFLINE, ParserType.IN_SAME_FACTION, ParserType.INCLUDES_SELF))
                        .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                        .handler(ctx -> runCommand(ctx));
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player sender = (Player)ctx.sender();

        Rank rank = ctx.get("rank");
        FactionPlayer factionPlayer = ctx.get("player");
        OfflinePlayer player = Bukkit.getOfflinePlayer(factionPlayer.getID());
        
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();

        Factions.getFactionsCommon().playerManager.setPlayerRank(factionPlayer.getID(), rank.getID());

        FactionUtil.broadcast(
            sender.getServer(), faction.getID(),
            MessageUtil.format("{} {} assigned the rank of {} to {}.", FactionUtil.getFactionNameAsPrefix(faction), sender.displayName(), Component.text(player.getName()), Component.text(rank.getName()))
        );

        if(player.isOnline()) {
            PlayerUtil.updatePlayerPermissions(player.getPlayer());
        }
    }
}
