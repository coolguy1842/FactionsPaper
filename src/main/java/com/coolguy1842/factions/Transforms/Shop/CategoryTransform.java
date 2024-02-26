package com.coolguy1842.factions.Transforms.Shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.interfaces.core.arguments.ArgumentKey;
import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Transforms.Shop.SellModeTransform.SellMode;
import com.coolguy1842.factions.Util.ItemUtil;
import com.coolguy1842.factions.Util.MessageUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factions.Util.ShopUtil;
import com.coolguy1842.factions.Util.ShopUtil.ShopCategory;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class CategoryTransform implements Transform<ChestPane, PlayerViewer> {
    @Override
    public ChestPane apply(ChestPane pane, InterfaceView<ChestPane, PlayerViewer> viewer) {
        String menu = viewer.arguments().getOrDefault(CategoriesTransform.menuArgumentKey, "categories");
        if(!menu.equals("category")) return pane;
        
        ShopCategory category = viewer.arguments().get(ArgumentKey.of("category", ShopCategory.class));

        int i = 0;
        for(Material material : category.items) {
            SellMode mode = viewer.arguments().getOrDefault(SellModeTransform.sellModeArgumentKey, SellMode.ONE);
            Long price = ShopUtil.getSellPrice(material).get();

            Long sellAmount = 0L;

            switch(viewer.arguments().getOrDefault(SellModeTransform.sellModeArgumentKey, SellMode.ONE)) {
            case ONE:
                if(PlayerUtil.getAmountItem(viewer.viewer().player(), material) >= 1) {
                    sellAmount = 1L;
                }

                break;
            case STACK:
                if(PlayerUtil.getAmountItem(viewer.viewer().player(), material) >= material.getMaxStackSize()) {
                    sellAmount = Long.valueOf(material.getMaxStackSize());
                }

                break;
            case ALL: sellAmount = Long.valueOf(PlayerUtil.getAmountItem(viewer.viewer().player(), material)); break;
            default: break;
            }

            Component lore;
            if(sellAmount == 0L) {
                lore = Component.text("Not enough!").color(TextColor.color(255, 0, 0));
            }
            else {
                lore = MessageUtil.format("Sell {} for ${}", Component.text(sellAmount), Component.text(price * sellAmount));
            }

            pane = pane.element(
                ItemStackElement.of(
                    ItemUtil.createItem(material,1, null, lore),
                    (clickHandler) -> {
                        ItemStack clickedItem = clickHandler.click().cause().getCurrentItem();
                        Component name = clickedItem.displayName();

                        Player player = viewer.viewer().player();
                        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());
                        
                        switch (mode) {
                        case ONE: {
                            if(PlayerUtil.getAmountItem(player, material) <= 0) {
                                player.sendMessage(MessageUtil.format("{} You don't have enough of that item.", Factions.getPrefix()));
                                return;
                            }

                            PlayerUtil.removeItemAmount(player, material, 1);

                            Factions.getFactionsCommon().playerManager.setPlayerBalance(player.getUniqueId(), factionPlayer.getBalance() + price);
                            player.sendMessage(MessageUtil.format("{} You sold one of {} for ${}!", Factions.getPrefix(), name, Component.text(price)));

                            break;
                        }
                        case STACK: {
                            Integer maxStack = clickedItem.getType().getMaxStackSize();
                            if(PlayerUtil.getAmountItem(player, material) < maxStack) {
                                player.sendMessage(MessageUtil.format("{} You don't have enough of that item.", Factions.getPrefix()));
                                return;
                            }

                            PlayerUtil.removeItemAmount(player, material, maxStack);

                            Factions.getFactionsCommon().playerManager.setPlayerBalance(player.getUniqueId(), factionPlayer.getBalance() + (price * maxStack));
                            player.sendMessage(MessageUtil.format("{} You sold a stack of {} for ${}!", Factions.getPrefix(), name, Component.text(price * maxStack)));

                            break;
                        }
                        case ALL: {
                            Integer amount = PlayerUtil.getAmountItem(player, material);
                            if(amount <= 0) {
                                player.sendMessage(MessageUtil.format("{} You don't have enough of that item.", Factions.getPrefix()));
                                return;
                            }

                            PlayerUtil.removeItemAmount(player, material, amount);
                            Factions.getFactionsCommon().playerManager.setPlayerBalance(player.getUniqueId(), factionPlayer.getBalance() + (price * amount));
                            player.sendMessage(MessageUtil.format("{} You sold {} of {} for ${}!", Factions.getPrefix(), Component.text(amount), name, Component.text(price * amount)));

                            break;
                        }
                        default: break;
                        }
                    }
                ),
                i % 9, i / 9
            );

            i++;
        }

        return pane;
    }
}
