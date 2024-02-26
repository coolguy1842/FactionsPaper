package com.coolguy1842.factions.Transforms.Shop;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.incendo.interfaces.core.arguments.ArgumentKey;
import org.incendo.interfaces.core.arguments.HashMapInterfaceArguments;
import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;

import com.coolguy1842.factions.Util.ItemUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class SellModeTransform implements Transform<ChestPane, PlayerViewer> {
    public static enum SellMode {
        ONE,
        STACK,
        ALL
    }

    public static ArgumentKey<SellMode> sellModeArgumentKey = ArgumentKey.of("sellMode", SellMode.class);
    public static ItemStack arrowSkull = ItemUtil.createSkull(Component.empty(), "http://textures.minecraft.net/texture/5cdbf613bc0b885587294857af0cf12c01153264ac25e15773a9d8bcdc3f00a9");

    @Override
    public ChestPane apply(ChestPane pane, InterfaceView<ChestPane, PlayerViewer> viewer) {
        SellMode sellMode = viewer.arguments().getOrDefault(sellModeArgumentKey, SellMode.ONE);
        Component displayName;

        switch (sellMode) {
        case ONE: displayName = Component.text("Sell One"); break;
        case STACK: displayName = Component.text("Sell Stack"); break;
        case ALL: displayName = Component.text("Sell All"); break;
        default: displayName = Component.empty(); break;
        }

        ItemMeta meta = arrowSkull.getItemMeta();
        meta.displayName(displayName.decoration(TextDecoration.ITALIC, false).color(TextColor.color(255, 255, 255)));
        
        arrowSkull.setItemMeta(meta);

        pane = pane.element(
            ItemStackElement.of(
                arrowSkull,
                (clickHandler) -> {
                    HashMapInterfaceArguments args = (HashMapInterfaceArguments)viewer.arguments();
                    if(clickHandler.cause().isLeftClick()) {
                        switch(sellMode) {
                        case ONE: args.set(sellModeArgumentKey, SellMode.STACK); break;
                        case STACK: args.set(sellModeArgumentKey, SellMode.ALL); break;
                        case ALL: args.set(sellModeArgumentKey, SellMode.ONE); break;
                        default: break;
                        }        
                    }
                    else if(clickHandler.cause().isRightClick()) {
                        switch(sellMode) {
                        case ONE: args.set(sellModeArgumentKey, SellMode.ALL); break;
                        case STACK: args.set(sellModeArgumentKey, SellMode.ONE); break;
                        case ALL: args.set(sellModeArgumentKey, SellMode.STACK); break;
                        default: break;
                        }        
                    }

                    clickHandler.view().backing().open(clickHandler.viewer(), args);
                }
            ),
            4, pane.rows() - 1
        );


        return pane;
    }
}
