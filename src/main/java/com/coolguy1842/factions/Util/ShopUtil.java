package com.coolguy1842.factions.Util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.coolguy1842.factions.Factions;

public class ShopUtil {
    public static class ShopCategory {
        public final String name;
        public final Material displayItem;

        public final List<Material> items;

        public ShopCategory(String name, Material displayItem, List<Material> items) {
            this.name = name;
            this.displayItem = displayItem;
            this.items = items;
        }
    }
    
    private static List<ShopCategory> categories;
    private static Map<Material, Long> sellPrices;
    

    public static void initCategories() {
        categories = new ArrayList<>();
        sellPrices = new HashMap<>();

        InputStream stream = Factions.getPlugin().getResource("assets/sell_prices.yml");
        InputStreamReader streamReader = new InputStreamReader(stream);

        YamlConfiguration config = YamlConfiguration.loadConfiguration(streamReader);

        for(String categoryName : config.getKeys(false)) {
            ConfigurationSection categorySection = config.getConfigurationSection(categoryName);
            Material displayItem = Material.getMaterial(categorySection.getString("display_item"));

            List<Material> items = new ArrayList<>();
            
            ConfigurationSection categoryItemsSection = categorySection.getConfigurationSection("prices");
            for(String itemName : categoryItemsSection.getKeys(false)) {
                List<Material> materials;
                switch (itemName) {
                case "ANY_WOOL":
                    materials = List.of(
                        Material.RED_WOOL,
                        Material.ORANGE_WOOL,
                        Material.YELLOW_WOOL,
                        Material.LIME_WOOL,
                        Material.GREEN_WOOL,
                        Material.CYAN_WOOL,
                        Material.LIGHT_BLUE_WOOL,
                        Material.BLUE_WOOL,
                        Material.PINK_WOOL,
                        Material.MAGENTA_WOOL,
                        Material.PURPLE_WOOL,
                        Material.LIGHT_GRAY_WOOL,
                        Material.GRAY_WOOL,
                        Material.BLACK_WOOL,
                        Material.BROWN_WOOL,
                        Material.WHITE_WOOL
                    );

                    break;
                default: materials = List.of(Material.valueOf(itemName)); break;
                }

                Long itemPrice = categoryItemsSection.getLong(itemName);
                for(Material material : materials) {
                    sellPrices.put(material, itemPrice);
                    items.add(material);
                }
            }

            categories.add(new ShopCategory(categoryName, displayItem, items));
        }
    }


    public static Optional<ShopCategory> getCategory(String category) {
        if(categories == null || categories.size() <= 0) initCategories();

        return categories.stream().filter(x -> x.name.equals(category)).findFirst();
    }

    public static Optional<Long> getSellPrice(Material item) {
        if(categories == null || categories.size() <= 0) initCategories();

        if(!sellPrices.containsKey(item)) return Optional.empty();
        return Optional.of(sellPrices.get(item));
    }
}
