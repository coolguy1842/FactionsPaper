package com.coolguy1842.factions.Commands.TPA;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Managers.TPAManager;
import com.coolguy1842.factions.Managers.TPAManager.TPARequest;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement.Interface;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;

import net.kyori.adventure.text.Component;

public class TPDenyCommand {
    public static class Requirement implements Interface {
        public Map<String, Component> getErrorMessages() {
            return Map.ofEntries(
                Map.entry("noRequest", Component.text("You don't have any TPA requests!"))
            );
        }

        @Override
        public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) {
            return getErrorMessages().get("noRequest");
        }

        @Override
        public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) {
            Player player = (Player)ctx.sender();

            return TPAManager.getInstance().getRequest(player.getUniqueId()).isPresent();
        }
    }

    public static void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(
            commandManager.commandBuilder("tpdeny")
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement(), new Requirement()))
                .handler(ctx -> runCommand(ctx))
        );
    }

    public static void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();

        TPARequest request = TPAManager.getInstance().getRequest(player.getUniqueId()).get();
        Player sender = Bukkit.getPlayer(request.getSender());

        TPAManager.getInstance().removeRequest(player.getUniqueId());
        player.sendMessage(MessageUtil.format("{} You denied the request from {}!", Factions.getPrefix(), PlayerUtil.playerGlobalName(sender)));
        sender.sendMessage(MessageUtil.format("{} {} denied the teleport request.", Factions.getPrefix(), PlayerUtil.playerGlobalName(player)));
    }
}
