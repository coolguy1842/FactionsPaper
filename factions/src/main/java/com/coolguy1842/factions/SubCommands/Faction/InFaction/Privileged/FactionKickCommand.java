package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Parsers.FactionPlayerParser;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factions.Util.RankUtil.RankPermission;
import com.coolguy1842.factions.interfaces.Subcommand;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;

import net.kyori.adventure.text.Component;

public class FactionKickCommand implements Subcommand {    
    private final class Requirement implements FactionRequirement.Interface {
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

    @Override public String getName() { return "kick"; }
    @Override public String getDescription() { return "Kicks the specified player from the faction!"; }
    @Override public Permission getPermission() {
        return Permission.allOf(PlayerPermissions.inFaction, PlayerPermissions.rankPermission(RankPermission.KICK));
    }

    @Override
    public Builder<CommandSender> getCommand(Builder<CommandSender> baseCommand) {
        return
            baseCommand.literal(getName())
                .required("player", FactionPlayerParser.inSameFactionNotLeader(true))
                    .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new Requirement()))
                    .permission(getPermission())
                    .handler(ctx -> runCommand(ctx));
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();

        FactionPlayer kicked = ctx.get("player");
        OfflinePlayer kickedPlayer = Bukkit.getOfflinePlayer(kicked.getID());

        Factions.getFactionsCommon().playerManager.setPlayerFaction(kicked.getID(), null);

        FactionUtil.broadcast(
            ctx.sender().getServer(), factionPlayer.getFaction(),
            MessageUtil.format("{} {} kicked {} from the faction!", Factions.getPrefix(), Component.text(player.getName()), Component.text(kickedPlayer.getName()))
        );

        if(!kickedPlayer.isOnline()) return;

        PlayerUtil.updatePlayerPermissions(kickedPlayer.getPlayer());
        kickedPlayer.getPlayer().sendMessage(MessageUtil.format("{} You have been kicked from {}.", Factions.getPrefix(), Component.text(faction.getName())));
    }
}
