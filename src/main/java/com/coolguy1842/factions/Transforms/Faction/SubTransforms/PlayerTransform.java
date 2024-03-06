package com.coolguy1842.factions.Transforms.Faction.SubTransforms;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.incendo.interfaces.core.arguments.ArgumentKey;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Transforms.Faction.FactionMenuTransform;
import com.coolguy1842.factions.Transforms.Faction.FactionMenuTransform.MenuType;
import com.coolguy1842.factions.Util.ItemUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.Faction;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;
import com.coolguy1842.factionscommon.Classes.Rank.RankPermission;

import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;

public class PlayerTransform {
    enum SelectedPlayerType {
        IN_FACTION,
        NOT_IN_FACTION
    }

    

    
    public static ArgumentKey<FactionPlayer> playerArgumentKey = ArgumentKey.of("player", FactionPlayer.class);
    public static ArgumentKey<SelectedPlayerType> selectedPlayerTypeArgumentKey = ArgumentKey.of("selectedPlayerType", SelectedPlayerType.class);


    public static List<Pair<Function<Boolean, Pair<FactionPlayer, FactionPlayer>>, ItemStackElement<ChestPane>>> playerNotInFactionButtons;

    // public static List<Pair<RankPermission, ItemStackElement<ChestPane>>> playerInFactionButtons = new ArrayList<>(
    public static List<Pair<Function<Pair<FactionPlayer, FactionPlayer>, Boolean>, ItemStackElement<ChestPane>>> playerInFactionButtons = new ArrayList<>(
        List.of(
            Pair.of(
                (pair) -> {
                    FactionPlayer sender = pair.first();
                    Faction faction = Factions.getFactionsCommon().factionManager.getFaction(sender.getFaction()).get();

                    FactionPlayer selected = pair.second();

                    return
                        !faction.getLeader().equals(selected.getID()) &&
                        PlayerUtil.playerHasPermission(Bukkit.getPlayer(sender.getID()), PlayerUtil.PlayerPermissions.rankPermission(RankPermission.KICK));
                },
                ItemStackElement.of(
                    ItemUtil.createItem(Material.LEATHER_BOOTS, 1, Component.text("Kick"))
                )
            ),
            Pair.of(
                (pair) -> {
                    FactionPlayer sender = pair.first();
                    return PlayerUtil.playerHasPermission(Bukkit.getPlayer(sender.getID()), PlayerUtil.PlayerPermissions.rankPermission(RankPermission.ADMIN));
                },
                ItemStackElement.of(
                    ItemUtil.createItem(Material.GOLDEN_HELMET, 1, Component.text("Set Rank"))
                )
            )
        )
    );


    public static ChestPane apply(ChestPane pane, InterfaceView<ChestPane, PlayerViewer> view) {
        switch(view.arguments().getOrDefault(selectedPlayerTypeArgumentKey, SelectedPlayerType.IN_FACTION)) {
        case IN_FACTION: pane = applyInFaction(pane, view); break;
        case NOT_IN_FACTION: pane = applyNotInFaction(pane, view); break;
        default: break;
        }

        pane = FactionMenuTransform.addQuitButton(pane, view, MenuType.PLAYERS);
        return pane;
    }






    public static ChestPane applyInFaction(ChestPane pane, InterfaceView<ChestPane, PlayerViewer> view) {
        Player viewerPlayer = view.viewer().player();
        FactionPlayer viewerFactionPlayer = PlayerUtil.getFactionPlayer(viewerPlayer.getUniqueId());

        FactionPlayer player = view.arguments().get(playerArgumentKey);

        Integer i = 0;
        for(Pair<Function<Pair<FactionPlayer, FactionPlayer>, Boolean>, ItemStackElement<ChestPane>> pair : playerInFactionButtons) {
            if(pair.first().apply(Pair.of(viewerFactionPlayer, player))) {
                pane = pane.element(pair.second(), i % 9, i / 9);
                i++;
            }
        }

        return pane;
    }

    
    public static ChestPane applyNotInFaction(ChestPane pane, InterfaceView<ChestPane, PlayerViewer> view) {
        return pane;
    }
}
