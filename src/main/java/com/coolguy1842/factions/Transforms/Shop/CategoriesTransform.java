package com.coolguy1842.factions.Transforms.Shop;

import org.incendo.interfaces.core.arguments.ArgumentKey;
import org.incendo.interfaces.core.arguments.HashMapInterfaceArguments;
import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;

import com.coolguy1842.factions.Util.ItemUtil;
import com.coolguy1842.factions.Util.ShopUtil;
import com.coolguy1842.factions.Util.ShopUtil.ShopCategory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

public class CategoriesTransform implements Transform<ChestPane, PlayerViewer> {
    public static ArgumentKey<String> menuArgumentKey = ArgumentKey.of("menu", String.class);
    public static ArgumentKey<ShopCategory> categoryArgumentKey = ArgumentKey.of("category", ShopCategory.class);

    @Override
    public ChestPane apply(ChestPane pane, InterfaceView<ChestPane, PlayerViewer> viewer) {
        String menu = viewer.arguments().getOrDefault(menuArgumentKey, "categories");
        if(menu.equals("category")) return pane;

        for(ShopCategory category : ShopUtil.getCategories()) {
            pane = pane.element(
                ItemStackElement.of(
                    ItemUtil.createItem(category.displayItem, 1, Component.text(category.name).style(Style.style().decoration(TextDecoration.ITALIC, false))),
                    (clickHandler) -> {
                        HashMapInterfaceArguments args = (HashMapInterfaceArguments)viewer.arguments();
                        args.set(menuArgumentKey, "category");
                        args.set(categoryArgumentKey, category);

                        clickHandler.view().backing().open(clickHandler.viewer(), args);
                    }
                ), category.slot % 9, category.slot / 9
            );
        }

        return pane;
    }
}