package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Leader;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Interfaces.Subcommand;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.DiscordUtil;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class FactionRenameCommand implements Subcommand {
    public static final class Requirement implements FactionRequirement.Interface {
        public Map<String, Component> getErrorMessages() {
            return Map.ofEntries(
                Map.entry("factionExists", Component.text("A faction named that already exists!"))
            );
        }

        @Override
        public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) {
            return getErrorMessages().get("factionExists");
        }

        @Override
        public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) {
            String factionName = ctx.get("name");
            return !Factions.getFactionsCommon().factionManager.getFaction(factionName).isPresent();
        }
    }

    @Override public String getName() { return "rename"; }
    @Override public String getDescription() { return "Renames the faction you are in!"; }
    @Override public Permission getPermission() {
        return Permission.allOf(PlayerPermissions.inFaction, PlayerPermissions.leader);
    }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal(getName())
                .required("name", StringParser.greedyStringParser())
                    .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement(), new Requirement()))
                    .permission(getPermission())
                    .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());

        String oldName = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get().getName();
        String name = ctx.get("name");

        Factions.getFactionsCommon().factionManager.setFactionName(factionPlayer.getFaction(), name);

        player.getServer().broadcast(
            MessageUtil.format("{} {} renamed their faction from {} to {}!", Factions.getPrefix(), player.displayName(), Component.text(oldName), Component.text(name))
        );


        String playerName = PlainTextComponentSerializer.plainText().serialize(player.displayName()); 
        DiscordUtil.sendToDiscord(String.format("%s renamed their faction from %s to %s!", playerName, oldName, name), "Factions", DiscordUtil.getAvatar(player));
    
        FactionUtil.updateFactionsPlayerTabNames(factionPlayer.getFaction());
    }
}
