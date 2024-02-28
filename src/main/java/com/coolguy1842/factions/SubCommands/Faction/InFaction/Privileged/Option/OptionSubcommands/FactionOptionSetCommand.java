package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Option.OptionSubcommands;

import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Parsers.FactionOptionParser;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement.Interface;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Rank;
import com.coolguy1842.factionscommon.Classes.Faction.Option;
import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class FactionOptionSetCommand implements OptionSubcommand {
    public class Requirement implements Interface {
        public Map<String, Component> getErrorMessages() {
            return Map.ofEntries(
                Map.entry("invalidColour", Component.text("Invalid colour format. (Correct Usage: \"#hexhere\")")),
                Map.entry("invalidDefaultRank", Component.text("Invalid rank.")),
                Map.entry("error", Component.text("Error"))
            );
        }

        @Override
        public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) {
            Option option = ctx.get("option");

            switch(option) {
            case COLOUR: return getErrorMessages().get("invalidColour");
            case DEFAULT_RANK: return getErrorMessages().get("invalidDefaultRank");
            default: break;
            }

            return getErrorMessages().get("error");
        }

        @Override
        public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) {
            Player player = (Player)ctx.sender();
            FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
            Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();
            
            Option option = ctx.get("option");
            @Nullable String value = ctx.getOrDefault("value", null);

            switch(option) {
            case COLOUR:
                if(value == null) return true;
                return TextColor.fromHexString(value) != null;
            case DEFAULT_RANK:
                if(value == null) return true;
                return Factions.getFactionsCommon().rankManager.getRank(faction.getID(), value).isPresent();
            default: return false;
            }
        }
    }


    @Override
    public Permission getPermission() { return PlayerUtil.PlayerPermissions.rankPermission(RankPermission.ADMIN); }

    @Override
    public Builder<CommandSender> getCommand(Builder<CommandSender> baseCommand) {
        return 
            baseCommand.literal("set")
                .required("option", FactionOptionParser.factionOptionParser())
                    .optional("value", StringParser.quotedStringParser())
                        .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement(), new Requirement()))
                        .permission(getPermission())
                        .handler(ctx -> runCommand(ctx));
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();

        Option option = ctx.get("option");
        @Nullable String value = ctx.getOrDefault("value", null);

        switch (option) {
        case COLOUR:
            if(value == null) {
                value = "#ffffff";
            }

            Factions.getFactionsCommon().factionManager.setOption(faction.getID(), option, value);
            FactionUtil.updateFactionsPlayerTabNames(faction.getID());
            break;
        case DEFAULT_RANK:
            if(value == null) {
                Factions.getFactionsCommon().factionManager.setOption(faction.getID(), option, null);
            }
            else {
                Rank rank = Factions.getFactionsCommon().rankManager.getRank(faction.getID(), value).get();
                Factions.getFactionsCommon().factionManager.setOption(faction.getID(), option, rank.getID().toString());
            }

            break;
        default: break;
        }

        FactionUtil.broadcast(
            player.getServer(), faction.getID(),
            MessageUtil.format(
                "{} {} set the option {} to {}",
                Factions.getPrefix(), player.displayName(), Component.text(option.name().toLowerCase()), Component.text(value == null ? "null" : value)
            )
        );
    }
}
