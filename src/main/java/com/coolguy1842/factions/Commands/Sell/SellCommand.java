package com.coolguy1842.factions.Commands.Sell;

import java.util.Map;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Parsers.ItemAmountParser;
import com.coolguy1842.factions.Requirements.Faction.DefaultFactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement;
import com.coolguy1842.factions.Requirements.Faction.FactionRequirement.Interface;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.ShopUtil;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;

import net.kyori.adventure.text.Component;

public class SellCommand {
    public static class Requirement implements Interface {
        public Map<String, Component> getErrorMessages() {
            return Map.ofEntries(
                Map.entry("invalidItem", Component.text("You cannot sell that item!"))
            );
        }

        @Override
        public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) {
            return getErrorMessages().get("invalidItem");
        }

        @Override
        public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) {
            Player player = (Player)ctx.sender();
            ItemStack toSell = player.getInventory().getItemInMainHand();
    
            Optional<Long> sellPriceOptional = ShopUtil.getSellPrice(toSell.getType());
            return sellPriceOptional.isPresent();
        }
    }

    public static void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(
            commandManager.commandBuilder("sell")
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement(), new Requirement()))
                .optional("amount", ItemAmountParser.itemAmountParser())
                    .handler(ctx -> runSellAmountCommand(ctx))
        );
        
        commandManager.command(
            commandManager.commandBuilder("sell")
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement(), new Requirement()))
                .literal("all")
                    .handler(ctx -> runSellAllCommand(ctx))
        );
    }


    public static void runSellAmountCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());

        ItemStack toSell = player.getInventory().getItemInMainHand();
        Component toSellName = toSell.displayName();

        Long sellPrice = ShopUtil.getSellPrice(toSell.getType()).get();
        Integer sellAmount = ctx.getOrDefault("amount", toSell.getAmount());

        PlayerUtil.removeItemAmount(player, toSell.getType(), sellAmount);
        
        Long soldPrice = sellPrice * sellAmount;
        Factions.getFactionsCommon().playerManager.setPlayerBalance(factionPlayer.getID(), factionPlayer.getBalance() + soldPrice);

        player.sendMessage(MessageUtil.format("{} You sold {} of {} for ${}!", Factions.getPrefix(), Component.text(sellAmount), toSellName, Component.text(soldPrice)));
    }

    public static void runSellAllCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());

        Material toSell = player.getInventory().getItemInMainHand().getType();
        Component toSellName = player.getInventory().getItemInMainHand().displayName();

        Integer sellAmount = PlayerUtil.getAmountItem(player, toSell);
        Long soldPrice = ShopUtil.getSellPrice(toSell).get() * sellAmount;
        
        player.getInventory().remove(toSell);

        Factions.getFactionsCommon().playerManager.setPlayerBalance(factionPlayer.getID(), factionPlayer.getBalance() + soldPrice);
        player.sendMessage(MessageUtil.format("{} You sold {} of {} for ${}!", Factions.getPrefix(), Component.text(sellAmount), toSellName, Component.text(soldPrice)));
    }
}
