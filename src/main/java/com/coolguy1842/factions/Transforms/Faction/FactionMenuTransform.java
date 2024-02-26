package com.coolguy1842.factions.Transforms.Faction;

import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;

public class FactionMenuTransform implements Transform<ChestPane, PlayerViewer> {
    @Override
    public ChestPane apply(ChestPane pane, InterfaceView<ChestPane, PlayerViewer> viewer) {
        return pane;
    }
}
