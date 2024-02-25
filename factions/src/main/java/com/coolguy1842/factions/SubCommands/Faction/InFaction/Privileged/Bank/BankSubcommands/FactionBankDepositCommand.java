package com.coolguy1842.factions.SubCommands.Faction.InFaction.Privileged.Bank.BankSubcommands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command.Builder;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Parsers.BalanceParser;
import com.coolguy1842.factions.Parsers.BalanceParser.ParserType;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Util.FactionUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;

import net.kyori.adventure.text.Component;

public class FactionBankDepositCommand implements BankSubcommand {
    @Override
    public List<Builder<CommandSender>> getCommands(Builder<CommandSender> baseCommand) {
        return List.of(
            baseCommand.literal("deposit")
                .required("amount", BalanceParser.balanceParser(ParserType.PLAYER))
                    .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                    .handler(ctx -> runCommand(ctx)),
            baseCommand.literal("deposit")
                .literal("all")
                    .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement()))
                    .handler(ctx -> runCommandAll(ctx))
        );
    }

    @Override
    public void runCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();

        Long amount = ctx.get("amount");
        
        Factions.getFactionsCommon().factionManager.setFactionBalance(faction.getID(), faction.getBalance() + amount);
        Factions.getFactionsCommon().playerManager.setPlayerBalance(factionPlayer.getID(), factionPlayer.getBalance() - amount);
     
        FactionUtil.broadcast(
            player.getServer(), faction.getID(),
            MessageUtil.format("{} {} has deposited ${}!", Factions.getPrefix(), player.displayName(), Component.text(amount))
        );
    }

    
    public void runCommandAll(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
        Faction faction = Factions.getFactionsCommon().factionManager.getFaction(factionPlayer.getFaction()).get();
        
        Long amount = factionPlayer.getBalance();
        Factions.getFactionsCommon().factionManager.setFactionBalance(faction.getID(), faction.getBalance() + amount);
        Factions.getFactionsCommon().playerManager.setPlayerBalance(factionPlayer.getID(), 0L);
     
        FactionUtil.broadcast(
            player.getServer(), faction.getID(),
            MessageUtil.format("{} {} has deposited ${}!", Factions.getPrefix(), player.displayName(), Component.text(amount))
        );
    }
}
