/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.ams;

import de.maile.daniel.ams.mysql.AMSDatabase;
import de.maile.daniel.ams.mysql.MoneyDatabase;
import de.maile.daniel.ams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AMSUpgradeInventory
{

    public static final String AMS_UPGRADE_INVENTORY_NAME = "§8AMS-Upgrades";

    private static int BACK_POS = 0;
    private static int EFFICIENCY_START = 10;
    private static int EFFICIENCY_END = 16;
    private static int OFFLINE_START = 19;
    private static int OFFLINE_END = 25;

    public static final double[] efficiencyUpgradeEfficiency = {0.05d, 0.15d, 0.25d, 0.5d, 0.75d, 1d, 2d};
    public static final double[] efficiencyUpgradeCost = {1d, 2d, 3d, 4d, 5d, 6d, 7d};
    public static final double[] offlineUpgradeEfficiency = {0.02d, 0.05d, 0.10d, 0.20d, 0.30d, 0.40d, 0.50d};
    public static final double[] offlineUpgradeCost = {1d, 2d, 3d, 4d, 5d, 6d, 7d};

    public static void openInventory(Player player)
    {
        Inventory gui = Bukkit.createInventory(player, 36, AMS_UPGRADE_INVENTORY_NAME);
        updateInv(player, gui);
        player.openInventory(gui);
    }

    private static void updateInv(Player player, Inventory gui)
    {
        int offlineLevelBought = AMSDatabase.getOfflineUpgradeLevel(player.getUniqueId());
        int efficiencyLevelBought = AMSDatabase.getEfficiencyUpgradeLevel(player.getUniqueId());

        for (int i = 0; i < 36; i++)
        {
            if (i == BACK_POS)
                gui.setItem(i, Utils.getBackHead("§c<-- Zurück zur AMS"));
            else if (i >= EFFICIENCY_START && i <= EFFICIENCY_END)
                gui.setItem(i, getEfficiencyUpgradeByID(i - EFFICIENCY_START, efficiencyLevelBought));
            else if (i >= OFFLINE_START && i <= OFFLINE_END)
                gui.setItem(i, getOfflineUpgradeByID(i - OFFLINE_START, offlineLevelBought));
            else
                gui.setItem(i, Utils.getInventoryPlaceholderItem());
        }
    }

    public static void clicked(Player player, int slot, ItemStack clickedItem, Inventory inventory, ClickType clickType)
    {
        int offlineLevelBought = AMSDatabase.getOfflineUpgradeLevel(player.getUniqueId());
        int efficiencyLevelBought = AMSDatabase.getEfficiencyUpgradeLevel(player.getUniqueId());

        if (slot == BACK_POS)
        {
            AMSInventory.openInventory(player);
        }
        else if (slot >= EFFICIENCY_START && slot <= EFFICIENCY_END)
        {
            int level = slot - EFFICIENCY_START + 1;
            if(efficiencyLevelBought - level >= 0)
            {
                player.sendMessage("§cDu hast dieses Upgrade bereits gekauft");
            }
            else if(efficiencyLevelBought - level == -1)
            {
                double playerBalance = MoneyDatabase.getBalance(player.getUniqueId());
                if(playerBalance < efficiencyUpgradeCost[level - 1])
                {
                    player.sendMessage("§cDu hast nicht genug Geld, um dieses Upgrade zu kaufen");
                }
                else
                {
                    AMSDatabase.setEfficiencyUpgradeLevel(player.getUniqueId(), level);
                    MoneyDatabase.setBalance(player.getUniqueId(), playerBalance - efficiencyUpgradeCost[level - 1]);
                    updateInv(player, inventory);
                    player.sendMessage("§aDu Offline Gem " + Utils.getRoman(level) + " gekauft");
                }
            }
            else
            {
                player.sendMessage("§cDu musst erst das vorherige Level dieses Upgrades kaufen, um das nächste freizuschalten");
            }
        }
        else if (slot >= OFFLINE_START && slot <= OFFLINE_END)
        {
            int level = slot - OFFLINE_START + 1;
            if(offlineLevelBought - level >= 0)
            {
                player.sendMessage("§cDu hast dieses Upgrade bereits gekauft");
            }
            else if(offlineLevelBought - level == -1)
            {
                double playerBalance = MoneyDatabase.getBalance(player.getUniqueId());
                if(playerBalance < offlineUpgradeCost[level - 1])
                {
                    player.sendMessage("§cDu hast nicht genug Geld, um dieses Upgrade zu kaufen");
                }
                else
                {
                    AMSDatabase.setOfflineUpgradeLevel(player.getUniqueId(), level);
                    MoneyDatabase.setBalance(player.getUniqueId(), playerBalance - offlineUpgradeCost[level - 1]);
                    updateInv(player, inventory);
                    player.sendMessage("§aDu Offline Gem " + Utils.getRoman(level) + " gekauft");
                }
            }
            else
            {
                player.sendMessage("§cDu musst erst das vorherige Level dieses Upgrades kaufen, um das nächste freizuschalten");
            }
        }
    }

    private static ItemStack getEfficiencyUpgradeByID(int id, int boughtLevel)
    {
        String costString;
        if(id + 1 <= boughtLevel)
            costString = "§aGekauft";
        else if(id + 1 == boughtLevel + 1)
            costString = "§e" + Utils.doubleToString(efficiencyUpgradeCost[id], 0) + "$";
        else
            costString = "§cZuerst Level " + Utils.getRoman(id) + " kaufen";

        return Utils.createItem(Material.DIAMOND, id + 1, (byte) 0, id + 1 <= boughtLevel,"§aEffizienz " + Utils.getRoman(id + 1),
                "§7Mit diesem Upgrade produziert", "§7deine AMS §a" + Utils.doubleToString(efficiencyUpgradeEfficiency[id] * 100, 0)
                        + "% mehr", "",
                "§7Preis: " + costString);
    }

    private static ItemStack getOfflineUpgradeByID(int id, int boughtLevel)
    {
        String costString;
        if(id + 1 <= boughtLevel)
            costString = "§aGekauft";
        else if(id + 1 == boughtLevel + 1)
            costString = "§e" + Utils.doubleToString(offlineUpgradeCost[id], 0) + "$";
        else
            costString = "§cZuerst Level " + Utils.getRoman(id) + " kaufen";

        return Utils.createItem(Material.EMERALD, id + 1, (byte) 0, id + 1 <= boughtLevel, "§aOffline Gem " + Utils.getRoman(id + 1),
                "§7Mit diesem Upgrade produziert", "§7deine AMS §a" + Utils.doubleToString(offlineUpgradeEfficiency[id] * 100, 0)
                        + "% §7von deinem",
                "§7normalen Wert, während du offline bist", "",
                "§7Preis: " + costString);
    }
}
