package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands.Permission;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.BooleanParser;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Parsers.RankParser;
import com.coolguy1842.factions.Parsers.RankPermissionParser;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands.RankSubcommand;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Rank;
import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

import net.kyori.adventure.text.Component;

public class FactionRankPermissionSetCommand implements RankSubcommand {
    @Override
    public Builder<CommandSender> getCommand(Builder<CommandSender> baseCommand) {
        return 
            baseCommand.literal("permission")
                .literal("set")
                    .required("rank", RankParser.rankParser())
                        .required("permission", RankPermissionParser.rankPermissionParser())
                            .required("value", BooleanParser.booleanParser())
                                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                                .handler(ctx -> runCommand(ctx));
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();

        Rank rank = ctx.get("rank");
        RankPermission permission = ctx.get("permission");
        Boolean value = ctx.get("value");

        Factions.getFactionsCommon().rankManager.setRankPermission(rank.getID(), permission.name(), value);
        FactionUtil.broadcast(
            player.getServer(), faction.getID(),
            MessageUtil.format(
                "{} {} has set the permission of rank {} to {}!",
                Factions.getPrefix(), player.displayName(), Component.text(rank.getName()), Component.text(value ? "true" : "false")
            )
        );


        for(FactionPlayer fP : Factions.getFactionsCommon().playerManager.getPlayersWithRank(rank.getID())) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(fP.getID());

            if(!p.isOnline()) continue;
            PlayerUtil.updatePlayerPermissions(p.getPlayer());
        }
    }
}
