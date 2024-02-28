package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Option.OptionSubcommands;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
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
import com.coolguy1842.factionscommon.Classes.Faction.Option;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class FactionOptionSetCommand implements OptionSubcommand {
    public class Requirement implements Interface {
        public Map<String, Component> getErrorMessages() {
            return Map.ofEntries(
                Map.entry("invalidColour", Component.text("Invalid colour format. (Correct Usage: \"#hexhere\")")),
                Map.entry("error", Component.text("Error"))
            );
        }

        @Override
        public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) {
            Option option = ctx.get("option");

            switch(option) {
            case COLOUR: return getErrorMessages().get("invalidColour");
            default: break;
            }

            return getErrorMessages().get("error");
        }

        @Override
        public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) {
            Option option = ctx.get("option");
            String value = ctx.get("value");

            switch(option) {
            case COLOUR: return TextColor.fromHexString(value) != null;
            default: return false;
            }
        }
    }


    @Override
    public Builder<CommandSender> getCommand(Builder<CommandSender> baseCommand) {
        return 
            baseCommand.literal("set")
                .required("option", FactionOptionParser.factionOptionParser())
                    .required("value", StringParser.quotedStringParser())
                        .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement(), new Requirement()))
                        .handler(ctx -> runCommand(ctx));
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();

        Option option = ctx.get("option");
        String value = ctx.get("value");

        Factions.getFactionsCommon().factionManager.setOption(faction.getID(), option, value);
        switch (option) {
        case COLOUR: FactionUtil.updateFactionsPlayerTabNames(faction.getID()); break;
        default: break;
        }

        FactionUtil.broadcast(
            player.getServer(), faction.getID(),
            MessageUtil.format(
                "{} {} set the option {} to {}",
                Factions.getPrefix(), player.displayName(), Component.text(option.name().toLowerCase()), Component.text(value)
            )
        );
    }
}
