package de.maile.daniel.ams.listeners;

import de.maile.daniel.ams.AMS;
import de.maile.daniel.ams.utils.Utils;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener
{
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        ItemStack itemStack = event.getItemInHand();

        if (itemStack == null)
            return;

        if (event.getItemInHand().getType() == Material.SPAWNER)
        {
            net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
            NBTTagCompound nbtTagCompound = nmsStack.getTag();

            if (nbtTagCompound == null)
                return;

            if (!nbtTagCompound.hasKey("ams"))
                return;

            String value = nbtTagCompound.getString("ams");

            if(value == null)
                return;

            if (value.equals("spawner"))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(event.getPlayer().getGameMode() != GameMode.SURVIVAL)
            return;

        if (event.getBlock().getType() == Material.SPAWNER)
        {

            if (AMS.INSTANCE.getConfig().getBoolean("allow_spawner_break"))
            {
                Utils.dropSpawner(event.getBlock().getLocation(), 1d);
            }
        }
    }
}
