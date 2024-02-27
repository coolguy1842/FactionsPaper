package com.coolguy1842.factions.Transforms.Shop;

import org.incendo.interfaces.core.arguments.ArgumentKey;
import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;

import com.coolguy1842.factions.Transforms.Shop.SubTransforms.CategoriesTransform;
import com.coolguy1842.factions.Transforms.Shop.SubTransforms.CategoryTransform;
import com.coolguy1842.factions.Util.ShopUtil.ShopCategory;

public class ShopMenuTransform implements Transform<ChestPane, PlayerViewer> {
    public static enum MenuType {
        CATEGORIES,
        CATEGORY
    }
    
    public static ArgumentKey<MenuType> menuArgumentKey = ArgumentKey.of("menu", MenuType.class);
    public static ArgumentKey<ShopCategory> categoryArgumentKey = ArgumentKey.of("category", ShopCategory.class);

    @Override
    public ChestPane apply(ChestPane pane, InterfaceView<ChestPane, PlayerViewer> viewer) {
        MenuType menu = viewer.arguments().getOrDefault(menuArgumentKey, MenuType.CATEGORIES);

        switch (menu) {
        case CATEGORIES: return CategoriesTransform.apply(pane, viewer);
        case CATEGORY: return CategoryTransform.apply(pane, viewer);
        default: return pane;
        }
    }
}
