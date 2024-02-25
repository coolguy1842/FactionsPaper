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
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.processors.requirements.Requirements;

import com.coolguy1842.factions.Factions;
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
                Map.entry("invalidItem", Component.text("You cannot sell that item!")),
                Map.entry("notEnough", Component.text("You don't have enough of that item for the amount you specified!"))
            );
        }

        @Override
        public @NonNull Component errorMessage(final @NonNull CommandContext<CommandSender> ctx) {
            Player player = (Player)ctx.sender();
            ItemStack toSell = player.getInventory().getItemInMainHand();
    
            Optional<Long> sellPriceOptional = ShopUtil.getSellPrice(toSell.getType());
            if(!sellPriceOptional.isPresent()) return getErrorMessages().get("invalidItem");

            return getErrorMessages().get("notEnough");
        }

        @Override
        public boolean evaluateRequirement(final @NonNull CommandContext<CommandSender> ctx) {
            Player player = (Player)ctx.sender();
            ItemStack toSell = player.getInventory().getItemInMainHand();
    
            Optional<Long> sellPriceOptional = ShopUtil.getSellPrice(toSell.getType());
            if(!sellPriceOptional.isPresent()) return false;

            Integer amount = ctx.getOrDefault("amount", toSell.getAmount());
            return toSell.getAmount() >= amount;
        }
    }

    public static void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(
            commandManager.commandBuilder("sell")
                .meta(FactionRequirement.REQUIREMENT_KEY, Requirements.of(new DefaultFactionRequirement(), new Requirement()))
                .optional("amount", IntegerParser.integerParser(1))
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

        if(toSell.getAmount() == sellAmount) {
            player.getInventory().setItemInMainHand(null);
        }
        else {
            toSell.setAmount(toSell.getAmount() - sellAmount);
        }

        Long soldPrice = sellPrice * sellAmount;
        Factions.getFactionsCommon().playerManager.setPlayerBalance(factionPlayer.getID(), factionPlayer.getBalance() + soldPrice);

        player.sendMessage(MessageUtil.format("{} You sold {} of {} for ${}!", Factions.getPrefix(), Component.text(sellAmount), toSellName, Component.text(soldPrice)));
    }

    public static void runSellAllCommand(CommandContext<CommandSender> ctx) {
        Player player = (Player)ctx.sender();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());

        Material toSell = player.getInventory().getItemInMainHand().getType();
        Component toSellName = player.getInventory().getItemInMainHand().displayName();

        Integer sellAmount = getPlayerAmountItem(player, toSell);
        Long soldPrice = ShopUtil.getSellPrice(toSell).get() * sellAmount;
        
        player.getInventory().remove(toSell);

        Factions.getFactionsCommon().playerManager.setPlayerBalance(factionPlayer.getID(), factionPlayer.getBalance() + soldPrice);
        player.sendMessage(MessageUtil.format("{} You sold {} of {} for ${}!", Factions.getPrefix(), Component.text(sellAmount), toSellName, Component.text(soldPrice)));
    }


    public static Integer getPlayerAmountItem(Player player, Material item) {
        Integer out = 0;
        for(ItemStack itemStack : player.getInventory().getContents()) {
            if(itemStack == null || itemStack.isEmpty()) continue;
            if(itemStack.getType().equals(item)) out += itemStack.getAmount();
        }

        return out;
    }
}
