package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Parsers.RankParser;
import com.coolguy1842.factions.Parsers.RankPermissionParser;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Rank.RankSubcommands.RankSubcommand;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factionscommon.Classes.Rank;
import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

import net.kyori.adventure.text.Component;

public class FactionRankPermissionGetCommand implements RankSubcommand {
    @Override
    public Builder<CommandSender> getCommand(Builder<CommandSender> baseCommand) {
        return 
            baseCommand.literal("permission")
                .literal("get")
                    .required("rank", RankParser.rankParser())
                        .required("permission", RankPermissionParser.rankPermissionParser())
                            .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                            .handler(ctx -> runCommand(ctx));
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();

        Rank rank = ctx.get("rank");
        RankPermission permission = ctx.get("permission");

        Boolean value = rank.hasPermission(permission.name());

        player.sendMessage(
            MessageUtil.format(
                "{} Permission {} of rank {} is set to {}!",
                Factions.getPrefix(), Component.text(permission.name().toLowerCase()), Component.text(rank.getName()), Component.text(value ? "true" : "false")
            )
        );
    }
}
