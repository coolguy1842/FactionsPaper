package com.coolguy1842.factions.Transforms.Shop.SubTransforms;

import org.incendo.interfaces.core.arguments.HashMapInterfaceArguments;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;

import com.coolguy1842.factions.Transforms.Shop.ShopMenuTransform;
import com.coolguy1842.factions.Util.ItemUtil;
import com.coolguy1842.factions.Util.ShopUtil;
import com.coolguy1842.factions.Util.ShopUtil.ShopCategory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

public class CategoriesTransform {
    public static ChestPane apply(ChestPane pane, InterfaceView<ChestPane, PlayerViewer> view) {
        if(view.arguments().getOrDefault(ShopMenuTransform.categoryArgumentKey, null) != null) return pane;

        for(ShopCategory category : ShopUtil.getCategories()) {
            pane = pane.element(
                ItemStackElement.of(
                    ItemUtil.createItem(category.displayItem, 1, Component.text(category.name).style(Style.style().decoration(TextDecoration.ITALIC, false))),
                    (clickHandler) -> {
                        HashMapInterfaceArguments args = (HashMapInterfaceArguments)view.arguments();
                        args.set(ShopMenuTransform.menuArgumentKey, ShopMenuTransform.MenuType.CATEGORY);
                        args.set(ShopMenuTransform.categoryArgumentKey, category);

                        clickHandler.view().backing().open(clickHandler.viewer(), args);
                    }
                ), category.slot % 9, category.slot / 9
            );
        }

        return pane;
    }
}