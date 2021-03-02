package de.maile.daniel.ams.listeners;

import de.maile.daniel.ams.mysql.AMSDatabase;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener
{
    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        ItemStack itemStack = event.getItem();

        if(itemStack == null)
            return;

        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if(itemStack.getType() != Material.PAPER)
                return;

            net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
            NBTTagCompound nbtTagCompound = nmsStack.getTag();

            if (nbtTagCompound == null)
                return;

            if (!nbtTagCompound.hasKey("ams"))
                return;

            String value = nbtTagCompound.getString("ams");

            if(value == null)
                return;

            if (!value.equals("gift"))
                return;

            if (!nbtTagCompound.hasKey("amount"))
                return;

            long amount = nbtTagCompound.getLong("amount");
            Player player = event.getPlayer();
            long spawnerBalance = AMSDatabase.getSpawners(player.getUniqueId());
            AMSDatabase.setSpawners(player.getUniqueId(), spawnerBalance + amount);

            if(itemStack.getAmount() > 1)
                itemStack.setAmount(itemStack.getAmount() - 1);
            else
                player.getInventory().removeItem(itemStack);
        }
    }
}
