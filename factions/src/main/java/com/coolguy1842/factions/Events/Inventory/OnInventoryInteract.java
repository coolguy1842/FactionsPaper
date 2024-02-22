package com.coolguy1842.factions.Events.Inventory;

import java.util.Optional;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;

import com.coolguy1842.factions.Factions;
import com.coolguy1842.factions.Util.VaultUtil;
import com.coolguy1842.factionscommon.Classes.Vault;

public class OnInventoryInteract implements Listener {
    @EventHandler
    private void onInteract(InventoryInteractEvent e) {
        Optional<Vault> vaultOptional = VaultUtil.getVaultFromInventory(e.getInventory());

        if(!vaultOptional.isPresent()) return;
        Vault vault = vaultOptional.get();

        Factions.getFactionsCommon().vaultManager.setVaultContents(vault.getID(), VaultUtil.serializeInventory(e.getInventory()));
    }
}
