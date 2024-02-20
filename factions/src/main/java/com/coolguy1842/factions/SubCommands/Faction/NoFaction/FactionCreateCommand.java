package com.coolguy1842.factions.SubCommands.Faction.NoFaction;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.PlayerUtil.PlayerPermissions;
import com.coolguy1842.factions.interfaces.Subcommand;
import com.coolguy1842.factionscommon.Classes.Invite;

import net.kyori.adventure.text.Component;

public class FactionCreateCommand implements Subcommand {    
    private final class Requirement implements FactionRequirement.Interface {
        public Map<String, Component> getErrorMessages() {
            return Map.ofEntries(
                Map.entry("notPlayer", Component.text("Only players can use this!")),
                Map.entry("factionExists", Component.text("There already exists a faction named {}!")),
                Map.entry("error", Component.text("Error"))
            );
        }

        @Override
        public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) {
            if(!(ctx.sender() instanceof Player)) {
                return getErrorMessages().get("notPlayer");
            }
        
            String factionName = ctx.get("faction");
            if(Factions.getFactionsCommon().factionManager.getFaction(factionName).isPresent()) {
                return MessageUtil.format(getErrorMessages().get("factionExists"), Component.text(factionName));
            }

            return getErrorMessages().get("error");
        }

        @Override
        public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) {
            if(!(ctx.sender() instanceof Player)) return false;

            return !Factions.getFactionsCommon().factionManager.getFaction((String)ctx.get("faction")).isPresent();
        }
    }

    @Override public String getName() { return "create"; }
    @Override public String getDescription() { return "Creates a faction with the specified name!"; }
    @Override public Permission getPermission() { return PlayerPermissions.notInFaction; }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal(getName())
                .required("faction", StringParser.greedyStringParser())
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new Requirement()))
                .permission(getPermission())
                .handler(ctx -> runCommand(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        final String factionName = ctx.get("faction");
        Player player = (Player)ctx.sender();

        UUID factionID = UUID.randomUUID();

        // remove all invites where the player is invited
        for(Invite invite : Factions.getFactionsCommon().inviteManager.getInvitesWithInvited(player.getUniqueId())) {
            Factions.getFactionsCommon().inviteManager.removeInvite(invite.getInviter(), invite.getInvited());
        }

        // create the faction
        Factions.getFactionsCommon().factionManager.addFaction(factionID, factionName, player.getUniqueId());
        Factions.getFactionsCommon().playerManager.setPlayerFaction(player.getUniqueId(), factionID);

        ctx.sender().getServer().broadcast(
            MessageUtil.format(
                Component.text("{} {} created a faction named {}!"),
                Factions.getPrefix(),
                player.displayName(),
                Component.text(factionName)
            )
        );
        
        PlayerUtil.updatePlayerPermissions(player);
    }
}
