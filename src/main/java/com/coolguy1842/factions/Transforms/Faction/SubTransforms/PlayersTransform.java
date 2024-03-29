package com.coolguy1842.factions.Transforms.Faction.SubTransforms;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.interfaces.core.arguments.ArgumentKey;
import org.incendo.interfaces.core.arguments.HashMapInterfaceArguments;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.joml.Math;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Transforms.Faction.FactionMenuTransform;
import com.coolguy1842.factions.Transforms.Faction.FactionMenuTransform.MenuType;
import com.coolguy1842.factions.Transforms.Faction.SubTransforms.PlayerTransform.SelectedPlayerType;
import com.coolguy1842.factions.Util.ItemUtil;
import com.coolguy1842.factions.Util.PlayerUtil;
import com.coolguy1842.factionscommon.Classes.FactionPlayer;

import net.kyori.adventure.text.Component;

public class PlayersTransform {
    public static ItemStack nextPageItem = ItemUtil.createSkull(Component.text("Next"), "http://textures.minecraft.net/texture/2671c4c04337c38a5c7f31a5c751f991e96c03df730cdbee99320655c19d");
    public static ItemStack previousPageItem = ItemUtil.createSkull(Component.text("Previous"), "http://textures.minecraft.net/texture/93971124be89ac7dc9c929fe9b6efa7a07ce37ce1da2df691bf8663467477c7");


    public static ArgumentKey<Integer> playerPageArgumentKey = ArgumentKey.of("playerPage", Integer.class);

    public static ChestPane apply(ChestPane pane, InterfaceView<ChestPane, PlayerViewer> view) {
        Integer page = view.arguments().getOrDefault(playerPageArgumentKey, 0);

        Integer maxPlayersPerPage = 9 * (pane.rows() - 2);
        Integer pageOffset = page * maxPlayersPerPage;

        Player player = view.viewer().player();
        FactionPlayer factionPlayer = PlayerUtil.getFactionPlayer(player.getUniqueId());


        SelectedPlayerType selectedPlayerType = view.arguments().getOrDefault(PlayerTransform.selectedPlayerTypeArgumentKey, SelectedPlayerType.IN_FACTION);

        List<FactionPlayer> players;

        // List<FactionPlayer> players = Factions.getFactionsCommon().playerManager.getPlayersWithFaction(factionPlayer.getFaction());

        switch(selectedPlayerType) {
        case IN_FACTION: players = Factions.getFactionsCommon().playerManager.getPlayersWithFaction(factionPlayer.getFaction()); break;
        case NOT_IN_FACTION:
            players = new ArrayList<>();

            for(Player p : Bukkit.getOnlinePlayers()) {
                FactionPlayer fP = PlayerUtil.getFactionPlayer(p.getUniqueId());
                if(fP.getFaction() != null) continue;
                
                players.add(fP);
            }
            break;
        default: players = new ArrayList<>(); break;
        }

        if(pageOffset >= players.size() && players.size() > 0) {
            HashMapInterfaceArguments args = (HashMapInterfaceArguments)view.arguments();
            args.set(playerPageArgumentKey, 0);

            view.backing().open(view.viewer(), args);
        }


        for(Integer i = pageOffset; i < Math.min(pageOffset + maxPlayersPerPage, players.size()); i++) {
            FactionPlayer fP = players.get(i);
            OfflinePlayer p = Bukkit.getOfflinePlayer(fP.getID());

            pane = pane.element(
                ItemStackElement.of(
                    ItemUtil.createSkull(Component.text(p.getName()), p.getPlayerProfile()),
                    (clickHandler) -> {
                        HashMapInterfaceArguments args = (HashMapInterfaceArguments)clickHandler.view().arguments();
                        args.set(FactionMenuTransform.menuArgumentKey, MenuType.PLAYER);
                        args.set(PlayerTransform.playerArgumentKey, fP);
            
                        view.backing().open(clickHandler.viewer(), args);
                    }
                ),
                (i - pageOffset) % 9, (i - pageOffset) / 9
            );
        }


        if(pageOffset > 0) {
            pane = pane.element(
                ItemStackElement.of(
                    previousPageItem,
                    (clickHandler) -> {
                        HashMapInterfaceArguments args = (HashMapInterfaceArguments)view.arguments();
                        args.set(playerPageArgumentKey, args.getOrDefault(playerPageArgumentKey, 1) - 1);
            
                        view.backing().open(view.viewer(), args);
                    }
                ),
                3, pane.rows() - 1
            );
        }

        if(pageOffset + maxPlayersPerPage < players.size()) {
            pane = pane.element(
                ItemStackElement.of(
                    nextPageItem,
                    (clickHandler) -> {
                        HashMapInterfaceArguments args = (HashMapInterfaceArguments)view.arguments();
                        args.set(playerPageArgumentKey, args.getOrDefault(playerPageArgumentKey, 0) + 1);
            
                        view.backing().open(view.viewer(), args);
                    }
                ),
                5, pane.rows() - 1
            );
        }


        switch(view.arguments().getOrDefault(PlayerTransform.selectedPlayerTypeArgumentKey, PlayerTransform.SelectedPlayerType.IN_FACTION)) {
        case IN_FACTION:
            pane = pane.element(
                ItemStackElement.of(
                    ItemUtil.createItem(Material.GREEN_WOOL, 1, Component.text("Showing Players in your Faction")),
                    (clickHandler) -> {
                        HashMapInterfaceArguments args = (HashMapInterfaceArguments)view.arguments();
                        args.set(PlayerTransform.selectedPlayerTypeArgumentKey, PlayerTransform.SelectedPlayerType.NOT_IN_FACTION);

                        view.backing().open(view.viewer(), args);
                    }
                ),
                8, pane.rows() - 1
            );

            break;
        case NOT_IN_FACTION:
            pane = pane.element(
                ItemStackElement.of(
                    ItemUtil.createItem(Material.RED_WOOL, 1, Component.text("Showing Players not in your Faction")),
                    (clickHandler) -> {
                        HashMapInterfaceArguments args = (HashMapInterfaceArguments)view.arguments();
                        args.set(PlayerTransform.selectedPlayerTypeArgumentKey, PlayerTransform.SelectedPlayerType.IN_FACTION);

                        view.backing().open(view.viewer(), args);
                    }
                ),
                8, pane.rows() - 1
            );

            break;
        default: break;
        }

        pane = FactionMenuTransform.addQuitButton(pane, view, MenuType.MENU);
        return pane;
    }
}