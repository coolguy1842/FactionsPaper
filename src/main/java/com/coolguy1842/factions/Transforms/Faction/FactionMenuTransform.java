package com.coolguy1842.factions.Transforms.Faction;

import org.bukkit.Material;
import org.incendo.interfaces.core.arguments.ArgumentKey;
import org.incendo.interfaces.core.arguments.HashMapInterfaceArguments;
import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;

import com.coolguy1842.factions.Transforms.Faction.SubTransforms.MenuTransform;
import com.coolguy1842.factions.Transforms.Faction.SubTransforms.PlayerTransform;
import com.coolguy1842.factions.Transforms.Faction.SubTransforms.PlayersTransform;
import com.coolguy1842.factions.Util.ItemUtil;

import net.kyori.adventure.text.Component;

public class FactionMenuTransform implements Transform<ChestPane, PlayerViewer> {
    public static enum MenuType {
        MENU,


        PLAYERS, PLAYER,
        RANKS,
        ALLIES,

        VAULTS,
        HOMES,
        INVITES,
        
        SETTINGS,
        CLAIMS,
        BANK
    }

    public static ArgumentKey<MenuType> menuArgumentKey = ArgumentKey.of("menu", MenuType.class);

    @Override
    public ChestPane apply(ChestPane pane, InterfaceView<ChestPane, PlayerViewer> view) {
        MenuType menu = view.arguments().getOrDefault(menuArgumentKey, MenuType.MENU);
        
        switch(menu) {
        case MENU: return MenuTransform.apply(pane, view);
        case PLAYERS: return PlayersTransform.apply(pane, view);
        case PLAYER: return PlayerTransform.apply(pane, view);
        default: return pane;
        }
    }


    public static ChestPane addQuitButton(ChestPane pane, InterfaceView<ChestPane, PlayerViewer> view, MenuType menu) {
        return pane.element(
            ItemStackElement.of(
                ItemUtil.createItem(Material.BARRIER, 1, Component.text("Back")),
                (clickHandler) -> {
                    HashMapInterfaceArguments args = (HashMapInterfaceArguments)view.arguments();
                    args.set(FactionMenuTransform.menuArgumentKey, menu);
                    args.set(PlayersTransform.playerPageArgumentKey, 0);

                    clickHandler.view().backing().open(clickHandler.viewer(), args);
                }
            ),
            4, pane.rows() - 1
        );
    }
}
