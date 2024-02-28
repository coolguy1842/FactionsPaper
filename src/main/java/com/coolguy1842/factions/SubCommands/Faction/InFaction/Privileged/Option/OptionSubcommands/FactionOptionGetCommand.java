package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Option.OptionSubcommands;

import java.util.Optional;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Parsers.FactionOptionParser;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Faction.Option;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class FactionOptionGetCommand implements OptionSubcommand {
    @Override
    public Builder<CommandSender> getCommand(Builder<CommandSender> baseCommand) {
        return 
            baseCommand.literal("get")
                .required("option", FactionOptionParser.factionOptionParser())
                    .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                    .handler(ctx -> runCommand(ctx));
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();

        Option option = ctx.get("option");
        Optional<String> valueOptional = Factions.getFactionsCommon().factionManager.getOptionValue(faction.getID(), option, Optional.empty());

        String value;
        if(!valueOptional.isPresent()) {
            switch(option) {
            case COLOUR:
                value = TextColor.color(255, 255, 255).asHexString();
                break;
            default: value = "null"; break;
            }
        }
        else {
            value = valueOptional.get();
        }

        player.sendMessage(MessageUtil.format("{} Value of {} is set to {}", Factions.getPrefix(), Component.text(option.name().toLowerCase()), Component.text(value)));
    }
}
