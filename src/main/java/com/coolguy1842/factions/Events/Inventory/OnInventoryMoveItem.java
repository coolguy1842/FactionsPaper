package com.coolguy1842.factions.Events.Inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class OnInventoryMoveItem implements Listener {
    @EventHandler
    private void onMove(InventoryMoveItemEvent e) {
        switch (e.getDestination().getType()) {
        case BARREL: case CHEST:
            break;
        default: return;
        }

        
        // e.getDestination().getLocation()
        // Factions.getPlugin().getLogger().info("moved");
    }
}
