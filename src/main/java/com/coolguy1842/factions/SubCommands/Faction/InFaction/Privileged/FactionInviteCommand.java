package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Interfaces.Subcommand;
import com.coolguy1842.factions.Parsers.FactionPlayerParser;
import com.coolguy1842.factions.Parsers.FactionPlayerParser.ParserType;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Invite.InviteType;
import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class FactionInviteCommand implements Subcommand {    
    @Override public String getName() { return "invite"; }
    @Override public String getDescription() { return "Invites the specified player to the faction!"; }
    @Override public Permission getPermission() {
        return Permission.allOf(PlayerPermissions.inFaction, PlayerPermissions.rankPermission(RankPermission.INVITE));
    }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal(getName())
                .required("player", FactionPlayerParser.withOptions(ParserType.NOT_IN_FACTION, ParserType.HAS_NO_INVITE))
                    .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                    .permission(getPermission())
                    .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();

        FactionPlayer invited = ctx.get("player");
        Player invitedPlayer = Bukkit.getPlayer(invited.getID());

        Factions.getFactionsCommon().inviteManager.addInvite(faction.getID(), invited.getID(), InviteType.FACTION);

        FactionUtil.broadcast(
            ctx.sender().getServer(), factionPlayer.getFaction(),
            MessageUtil.format("{} {} has invited {} to the faction!", FactionUtil.getFactionNameAsPrefix(faction), player.displayName(), invitedPlayer.displayName())
        );

        invitedPlayer.sendMessage(
            MessageUtil.format(
                "{} You have been invited to {}!\n{}",
                Factions.getPrefix(), Component.text(faction.getName()),
                MessageUtil.getAcceptDeny(
                    ClickEvent.runCommand("/f accept " + faction.getName()),
                    HoverEvent.showText(MessageUtil.format("Accept invite from {}?", Component.text(faction.getName()))),

                    ClickEvent.runCommand("/f reject " + faction.getName()),
                    HoverEvent.showText(MessageUtil.format("Reject invite from {}?", Component.text(faction.getName())))
                )
            )
        );
    }
}
