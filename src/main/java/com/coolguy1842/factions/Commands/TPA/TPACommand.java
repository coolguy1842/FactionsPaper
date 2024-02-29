package com.coolguy1842.factions.Commands.TPA; 

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Managers.TPAManager;
import com.coolguy1842.factions.Managers.TPAManager.TPARequestType;
import com.coolguy1842.factions.Parsers.TPAPlayerParser;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class TPACommand {
    public static void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(
            commandManager.commandBuilder("tpa")
                .required("player", TPAPlayerParser.tpaPlayerParser(TPARequestType.TPA))
                    .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                    .handler(ctx -> runCommand(ctx))
        );
    }

    public static void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();

        Player receiver = ctx.get("player");
        TPAManager.getInstance().newRequest(receiver.getUniqueId(), player.getUniqueId(), TPARequestType.TPA);

        player.sendMessage(MessageUtil.format("{} You have sent a TPA request to {}!", Factions.getPrefix(), PlayerUtil.playerGlobalName(receiver)));
        receiver.sendMessage(
            MessageUtil.format(
                "{} {} has sent you a TPA request!\n{}",
                Factions.getPrefix(), PlayerUtil.playerGlobalName(player),
                MessageUtil.getAcceptDeny(
                    ClickEvent.runCommand("/tpaccept"), HoverEvent.showText(MessageUtil.format("Accept request from {}?", Component.text(player.getName()))),
                    ClickEvent.runCommand("/tpdeny"), HoverEvent.showText(MessageUtil.format("Deny request from {}?", Component.text(player.getName())))
                )
            )
        );

    }
}
