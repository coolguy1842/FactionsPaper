package com.coolguy1842.factions.Transforms.Faction.SubTransforms;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.incendo.interfaces.core.arguments.HashMapInterfaceArguments;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;

import com.coolguy1842.factions.Transforms.Faction.FactionMenuTransform;
import com.coolguy1842.factions.Util.ItemUtil;

import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;

public class MenuTransform {
    private static Map<Pair<Integer, Integer>, Pair<FactionMenuTransform.MenuType, ItemStack>> menuItems = Map.ofEntries(
        Map.entry(
            Pair.of(0, 0),
            Pair.of(FactionMenuTransform.MenuType.PLAYERS, ItemUtil.createItem(Material.PLAYER_HEAD, 1, Component.text("Players")))
        ),
        Map.entry(
            Pair.of(4, 0),
            Pair.of(FactionMenuTransform.MenuType.RANKS, ItemUtil.createItem(Material.GOLDEN_HELMET, 1, Component.text("Ranks")))
        ),
        Map.entry(
            Pair.of(8, 0),
            Pair.of(FactionMenuTransform.MenuType.ALLIES, ItemUtil.createItem(Material.DIAMOND_SWORD, 1, Component.text("Allies")))
        ),

        Map.entry(
            Pair.of(0, 2),
            Pair.of(FactionMenuTransform.MenuType.VAULTS, ItemUtil.createItem(Material.CHEST, 1, Component.text("Vault")))
        ),
        Map.entry(
            Pair.of(4, 2),
            Pair.of(FactionMenuTransform.MenuType.HOMES, ItemUtil.createItem(Material.OAK_DOOR, 1, Component.text("Homes")))
        ),
        Map.entry(
            Pair.of(8, 2),
            Pair.of(FactionMenuTransform.MenuType.INVITES, ItemUtil.createItem(Material.PAPER, 1, Component.text("Invites")))
        ),

        Map.entry(
            Pair.of(0, 4),
            Pair.of(FactionMenuTransform.MenuType.SETTINGS, ItemUtil.createItem(Material.FURNACE, 1, Component.text("Settings")))
        ),
        Map.entry(
            Pair.of(4, 4),
            Pair.of(FactionMenuTransform.MenuType.CLAIMS, ItemUtil.createItem(Material.GRASS_BLOCK, 1, Component.text("Claims")))
        ),
        Map.entry(
            Pair.of(8, 4),
            Pair.of(FactionMenuTransform.MenuType.BANK, ItemUtil.createItem(Material.GOLD_BLOCK, 1, Component.text("Bank")))
        )
    );

    public static ChestPane apply(ChestPane pane, InterfaceView<ChestPane, PlayerViewer> view) {
        for(Map.Entry<Pair<Integer, Integer>, Pair<FactionMenuTransform.MenuType, ItemStack>> entry : menuItems.entrySet()) {
            pane = pane.element(
                ItemStackElement.of(
                    entry.getValue().second(),
                    (clickHandler) -> {
                        HashMapInterfaceArguments args = (HashMapInterfaceArguments)view.arguments();
                        args.set(FactionMenuTransform.menuArgumentKey, entry.getValue().left());
    
                        clickHandler.view().backing().open(clickHandler.viewer(), args);
                    }
                ),
                entry.getKey().left(), entry.getKey().right()
            );
        }

        return pane;
    }
}
