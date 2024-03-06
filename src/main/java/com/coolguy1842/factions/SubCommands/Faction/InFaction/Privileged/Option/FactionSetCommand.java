package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.CommandExecutionHandler;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Interfaces.Subcommand;
import com.coolguy1842.factions.Parsers.FactionOptionValueParser;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Rank;
import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

import com.coolguy1842.factionscommon.Classes.Faction.Option;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class FactionSetCommand implements Subcommand {
    @Override public String getName() { return "set"; }
    @Override public String getDescription() { return "Sets the value of the specified option"; }
    @Override public Permission getPermission() { return Permission.allOf(PlayerUtil.PlayerPermissions.inFaction, PlayerUtil.PlayerPermissions.rankPermission(RankPermission.ADMIN)); }

    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        List<Builder<CommandSender>> commands = new ArrayList<>();
        
        for(Option option : Option.values()) {
            commands.add(
                baseCommand.literal(getName())
                    .literal(option.name().toLowerCase())
                        .optional("value", FactionOptionValueParser.factionOptionValueParser(option))
                            .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                            .permission(getPermission())
                            .handler(ctx -> runCommand(ctx))
                            .prependHandler(new CommandExecutionHandler<CommandSender>() {
                                @Override public void execute(@NonNull CommandContext<CommandSender> ctx) {
                                    ctx.set("option", option);
                                }
                            })
            );
        }

        return commands;
    }


    public static Map<Option, Object> defaultOptionValues = new HashMap<>();

    static {
        defaultOptionValues.put(Option.COLOUR, TextColor.color(255, 255, 255));
        defaultOptionValues.put(Option.DEFAULT_RANK, null);
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();

        Option option = ctx.get("option");
        Object value = ctx.getOrDefault("value", defaultOptionValues.get(option));

        String valueName = "";

        switch (option) {
        case COLOUR: {
            if(value == null) break;
            
            TextColor colour = (TextColor)value;
            
            Factions.getFactionsCommon().factionManager.setOption(faction.getID(), option, colour.asHexString());
            FactionUtil.updateFactionsPlayerTabNames(faction.getID());

            valueName = colour.asHexString();

            break;
        }
        case DEFAULT_RANK: {
            if(value == null) {
                Factions.getFactionsCommon().factionManager.setOption(faction.getID(), option, null);
                break;
            }

            Rank rank = (Rank)value;
            Factions.getFactionsCommon().factionManager.setOption(faction.getID(), option, rank.getID().toString());

            valueName = rank.getName();

            break;
        }
        default: break;
        }

        
        FactionUtil.broadcast(
            player.getServer(), faction.getID(),
            MessageUtil.format(
                "{} {} set the option {} to {}",
                FactionUtil.getFactionNameAsPrefix(faction), player.displayName(), Component.text(option.name().toLowerCase()), Component.text(value != null ? valueName : "null")
            )
        );
    }
}
