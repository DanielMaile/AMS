package de.maile.daniel.ams.listeners;

import de.maile.daniel.ams.AMS;
import de.maile.daniel.ams.utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener
{
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if(event.getItemInHand().getType() == Material.SPAWNER)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(event.getBlock().getType() == Material.SPAWNER)
        {
            if(AMS.INSTANCE.getConfig().getBoolean("allow_spawner_break"))
            {
                Utils.dropSpawner(event.getBlock().getLocation(), 1d);
            }
        }
    }
}
