package com.coolguy1842.factions.Transforms.Faction.SubTransforms;

import org.incendo.interfaces.core.arguments.ArgumentKey;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;

import com.coolguy1842.factions.Transforms.Faction.FactionMenuTransform;
import com.coolguy1842.factions.Transforms.Faction.FactionMenuTransform.MenuType;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;

public class PlayerTransform {
    public static ArgumentKey<FactionPlayer> playerArgumentKey = ArgumentKey.of("player", FactionPlayer.class);

    public static ChestPane apply(ChestPane pane, InterfaceView<ChestPane, PlayerViewer> view) {
        FactionPlayer player = view.arguments().get(playerArgumentKey);



        pane = FactionMenuTransform.addQuitButton(pane, view, MenuType.PLAYERS);
        return pane;
    }
}
