package com.coolguy1842.factions.Transforms.Faction;

import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;

public class FactionMenuTransform implements Transform<ChestPane, PlayerViewer> {
    public static enum MenuType {
        MENU,
        SETTINGS,
        PLAYERS,
        RANKS,
        VAULTS,
        HOMES,
        CLAIMS,
        ALLIES,
        INVITES,
        BANK
    }


    @Override
    public ChestPane apply(ChestPane pane, InterfaceView<ChestPane, PlayerViewer> viewer) {
        return pane;
    }
}
