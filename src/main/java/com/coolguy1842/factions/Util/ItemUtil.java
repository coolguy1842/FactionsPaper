package com.coolguy1842.factions.Util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import com.destroystokyo.paper.profile.PlayerProfile;

import net.kyori.adventure.text.Component;

public class ItemUtil {
    public static ItemStack createItem(Material material, Integer count, @Nullable Component name, Component... lore) {
        ItemStack item = new ItemStack(material, count);

        ItemMeta meta = item.getItemMeta();
        if(name != null) meta.displayName(name);
        if(lore != null) meta.lore(List.of(lore));

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createSkull(@Nullable Component name, String texture) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta)item.getItemMeta();
        if(name != null) meta.displayName(name);

        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();

        try {
            textures.setSkin(new URL(texture));
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    
        profile.setTextures(textures);
        meta.setPlayerProfile(profile);

        item.setItemMeta(meta);
        return item;
    }
}
