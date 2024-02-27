package com.coolguy1842.factions.Transforms.Shop.SubTransforms;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Transforms.Shop.SellModeTransform;
import com.coolguy1842.factions.Transforms.Shop.SellModeTransform.SellMode;
import com.coolguy1842.factions.Transforms.Shop.ShopMenuTransform;
import com.coolguy1842.factions.Util.ItemUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.ShopUtil;
import com.coolguy1842.factions.Util.ShopUtil.ShopCategory;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class CategoryTransform {
    private static Integer getSellAmount(SellMode mode, Player player, Material material) {
        switch (mode) {
        case ONE: return 1;
        case STACK: return material.getMaxStackSize();
        case ALL: return PlayerUtil.getAmountItem(player, material);
        default: return 0;
        }
    }

    public static ChestPane apply(ChestPane pane, InterfaceView<ChestPane, PlayerViewer> view) {
        ShopCategory category = view.arguments().get(ShopMenuTransform.categoryArgumentKey);
        Player player = view.viewer().player();

        int i = 0;
        for(Material material : category.items) {
            SellMode mode = view.arguments().getOrDefault(SellModeTransform.sellModeArgumentKey, SellMode.ONE);
            Long price = ShopUtil.getSellPrice(material).get();

            Component lore;
            {
                Integer sellAmount = getSellAmount(mode, player, material);
                if(PlayerUtil.getAmountItem(player, material) < sellAmount || sellAmount == 0) {
                    lore = Component.text("Not enough!").color(TextColor.color(255, 0, 0));
                }
                else {
                    lore = MessageUtil.format("Sell {} for ${}", Component.text(sellAmount), Component.text(price * sellAmount));    
                }
            }

            pane = pane.element(
                ItemStackElement.of(
                    ItemUtil.createItem(material,1, null, lore),
                    (clickHandler) -> {
                        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());

                        ItemStack clickedItem = clickHandler.click().cause().getCurrentItem();
                        Component name = clickedItem.displayName();
                        
                        // sellAmount might not have updated in time so get it again
                        Integer sellAmount = getSellAmount(mode, view.viewer().player(), material);
                        if(PlayerUtil.getAmountItem(player, material) < sellAmount || sellAmount == 0) {
                            player.sendMessage(MessageUtil.format("{} You don't have enough of that item.", Factions.getPrefix()));
                            return;
                        }

                        PlayerUtil.removeItemAmount(player, material, sellAmount);
                        
                        Long soldPrice = price * Long.valueOf(sellAmount);
                        Factions.getFactionsCommon().playerManager.setPlayerBalance(player.getUniqueId(), factionPlayer.getBalance() + soldPrice);
                        player.sendMessage(MessageUtil.format("{} You sold {} of {} for ${}!", Factions.getPrefix(), Component.text(sellAmount), name, Component.text(soldPrice)));
                    }
                ),
                i % 9, i / 9
            );

            i++;
        }

        return pane;
    }
}
