/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.ams;

import de.maile.daniel.ams.AMS;
import de.maile.daniel.ams.mysql.AMSDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class AMSManager
{
    public static void startUpdating()
    {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(AMS.INSTANCE, new Runnable()
        {
            @Override
            public void run()
            {
                List<UUID> uuids = AMSDatabase.getAllUUIDs();
                for (UUID uuid : uuids)
                {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null)
                        AMSDatabase.incOnlineTime(uuid);
                    else
                        AMSDatabase.incOfflineTime(uuid);
                }
            }
        }, 0L, 20L);
    }

    public static void updateOnlineBalance(Player player)
    {
        long onlineTime = AMSDatabase.getOnlineTime(player.getUniqueId());
        double balance = AMSDatabase.getBalance(player.getUniqueId());
        double perSecondGeneration = getPerSecondGeneration(player);

        AMSDatabase.setBalance(player.getUniqueId(), balance + (onlineTime * perSecondGeneration));
        AMSDatabase.setOnlineTime(player.getUniqueId(), 0L);
    }

    public static double getAndUpdateOfflineBalance(Player player)
    {
        long offlineTime = AMSDatabase.getOfflineTime(player.getUniqueId());
        double perSecondGeneration = getPerSecondGeneration(player);
        double balance = AMSDatabase.getBalance(player.getUniqueId());

        int offlineLevelBought = AMSDatabase.getOfflineUpgradeLevel(player.getUniqueId());
        double offlineMultiplier = offlineLevelBought == 0 ? 0 : AMSUpgradeInventory.offlineUpgradeEfficiency[offlineLevelBought - 1];

        double offlineGeneration = (offlineTime * perSecondGeneration * offlineMultiplier);

        AMSDatabase.setBalance(player.getUniqueId(), balance + offlineGeneration);
        AMSDatabase.setOfflineTime(player.getUniqueId(), 0L);
        return offlineGeneration;
    }

    public static double getPerSecondGeneration(Player player)
    {
        long spawnerAmount = AMSDatabase.getSpawners(player.getUniqueId());
        int efficiencyLevelBought = AMSDatabase.getEfficiencyUpgradeLevel(player.getUniqueId());
        double efficiencyMultiplier = efficiencyLevelBought == 0 ? 0 : AMSUpgradeInventory.efficiencyUpgradeEfficiency[efficiencyLevelBought - 1];

        return spawnerAmount * 0.02d * (1 + efficiencyMultiplier);
    }
}
