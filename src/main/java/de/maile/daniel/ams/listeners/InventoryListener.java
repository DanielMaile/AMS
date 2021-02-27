/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.listeners;

import de.maile.daniel.ams.ams.AMSInventory;
import de.maile.daniel.ams.ams.AMSUpgradeInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener
{
    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        if(event.getView().getTitle().equals(AMSInventory.AMS_INVENTORY_NAME))
        {
            AMSInventory.clicked((Player) event.getWhoClicked(), event.getSlot(), event.getCurrentItem(), event.getClickedInventory(), event.getClick());
            event.setCancelled(true);
        }
        else if(event.getView().getTitle().equals(AMSUpgradeInventory.AMS_UPGRADE_INVENTORY_NAME))
        {
            AMSUpgradeInventory.clicked((Player) event.getWhoClicked(), event.getSlot(), event.getCurrentItem(), event.getClickedInventory(), event.getClick());
            event.setCancelled(true);
        }
    }
}
