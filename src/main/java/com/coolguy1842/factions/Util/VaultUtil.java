package com.coolguy1842.factions.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factionscommon.Classes.Vault;

import net.kyori.adventure.text.Component;

public class VaultUtil {
    private static Map<UUID, Inventory> vaultInventories;
    private static Map<Inventory, UUID> inventoryToVault;


    public static void resetVaultInventories() {
        if(vaultInventories != null) {
            for(Inventory inventory : vaultInventories.values()) {
                inventory.close();
            }
        }

        vaultInventories = new HashMap<>(); 
        inventoryToVault = new HashMap<>();
    }

    public static void removeVaultInventory(Vault vault) {
        if(!vaultInventories.containsKey(vault.getID())) return;

        Inventory inventory = vaultInventories.get(vault.getID());
        inventory.close();

        inventoryToVault.remove(inventory);
        vaultInventories.remove(vault.getID());
    }


    public static Inventory getVaultInventory(Vault vault) {
        if(vaultInventories == null) resetVaultInventories();
        if(!vaultInventories.containsKey(vault.getID())) {
            Inventory inventory = deserializeInventory(Component.text(vault.getName()), vault.getContents());
            
            vaultInventories.put(vault.getID(), inventory);
            inventoryToVault.put(inventory, vault.getID());
        }
        
        return vaultInventories.get(vault.getID());
    }
    
    public static Optional<Vault> getVaultFromInventory(Inventory inventory) {
        if(inventoryToVault == null) resetVaultInventories();
        if(!inventoryToVault.containsKey(inventory)) return Optional.empty();

        Optional<Vault> vault = Factions.getFactionsCommon().vaultManager.getVault(inventoryToVault.get(inventory));
        return vault;
    }



    public static Inventory newVaultInventory(String name) {
        return Bukkit.createInventory(null, 54, Component.text(name));
    }

    public static String serializeInventory(Inventory inventory) {
        YamlConfiguration config = new YamlConfiguration();
        
        config.set("size", inventory.getSize());
        for(Integer i = 0; i < inventory.getSize(); i++) {
            config.set(i.toString(), inventory.getItem(i));
        }

        return config.saveToString();
    }
    
    public static Inventory deserializeInventory(Component name, String serialized) {
        YamlConfiguration config = new YamlConfiguration();

        try {
            config.loadFromString(serialized);
        }
        catch (InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }

        Inventory inv = Bukkit.createInventory(null, config.getInt("size"), name);
        for(Integer i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, config.getItemStack(i.toString()));
        }

        return inv;
    }
}
