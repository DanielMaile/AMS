package de.maile.daniel.ams.listeners;

import de.maile.daniel.ams.AMS;
import de.maile.daniel.ams.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class EntityDeathListener implements Listener
{
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        if(event.getEntity().getType() == EntityType.PLAYER)
        {
            if(AMS.INSTANCE.getConfig().getBoolean("drops.player.enabled"))
                Utils.dropSpawner(event.getEntity().getLocation(), AMS.INSTANCE.getConfig().getDouble("drops.player.chance"));
        }
        else
        {
            if(AMS.INSTANCE.getConfig().getBoolean("drops.entity.enabled"))
                Utils.dropSpawner(event.getEntity().getLocation(), AMS.INSTANCE.getConfig().getDouble("drops.entity.chance"));
        }
    }


}
