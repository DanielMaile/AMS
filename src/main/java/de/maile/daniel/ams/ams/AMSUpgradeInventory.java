/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.ams;

import de.maile.daniel.ams.AMS;
import de.maile.daniel.ams.mysql.AMSDatabase;
import de.maile.daniel.ams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AMSUpgradeInventory
{

    public static final String AMS_UPGRADE_INVENTORY_NAME = "§8AMS-Upgrades";

    private static final int BACK_POS = 0;
    private static final int EFFICIENCY_START = 10;
    private static final int EFFICIENCY_END = 16;
    private static final int OFFLINE_START = 19;
    private static final int OFFLINE_END = 25;

    public static List<Double> efficiencyUpgradeEfficiency;
    public static List<Double> efficiencyUpgradeCost;
    public static List<Double> offlineUpgradeEfficiency;
    public static List<Double> offlineUpgradeCost;

    public static void loadValues()
    {
        efficiencyUpgradeEfficiency = AMS.INSTANCE.getConfig().getDoubleList("efficiencyUpgradeEfficiency");
        efficiencyUpgradeCost = AMS.INSTANCE.getConfig().getDoubleList("efficiencyUpgradeCost");
        offlineUpgradeEfficiency = AMS.INSTANCE.getConfig().getDoubleList("offlineUpgradeEfficiency");
        offlineUpgradeCost = AMS.INSTANCE.getConfig().getDoubleList("offlineUpgradeCost");
    }

    public static void openInventory(Player player)
    {
        loadValues();
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
                gui.setItem(i, Utils.getBackHead(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.back"))));
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
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.message.alreadybought")));
            }
            else if(efficiencyLevelBought - level == -1)
            {
                double playerBalance = AMS.getEconomy().getBalance(player);
                if(playerBalance < efficiencyUpgradeCost.get(level - 1))
                {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.message.notenoughmoney")));
                }
                else
                {
                    AMSDatabase.setEfficiencyUpgradeLevel(player.getUniqueId(), level);
                    AMS.getEconomy().withdrawPlayer(player, efficiencyUpgradeCost.get(level - 1));
                    updateInv(player, inventory);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.message.boughtefficiency")
                            .replace("%level%", Utils.getRoman(level))));
                }
            }
            else
            {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.message.notunlocked")));
            }
        }
        else if (slot >= OFFLINE_START && slot <= OFFLINE_END)
        {
            int level = slot - OFFLINE_START + 1;
            if(offlineLevelBought - level >= 0)
            {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.message.alreadybought")));
            }
            else if(offlineLevelBought - level == -1)
            {
                double playerBalance = AMS.getEconomy().getBalance(player);
                if(playerBalance < offlineUpgradeCost.get(level - 1))
                {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.message.notenoughmoney")));
                }
                else
                {
                    AMSDatabase.setOfflineUpgradeLevel(player.getUniqueId(), level);
                    AMS.getEconomy().withdrawPlayer(player, offlineUpgradeCost.get(level - 1));
                    updateInv(player, inventory);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.message.boughtofflinegem")
                            .replace("%level%", Utils.getRoman(level))));
                }
            }
            else
            {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.message.notunlocked")));
            }
        }
    }

    private static ItemStack getEfficiencyUpgradeByID(int id, int boughtLevel)
    {
        String costString;
        if(id + 1 <= boughtLevel)
            costString = ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.bought"));
        else if(id + 1 == boughtLevel + 1)
            costString = "§e" + Utils.doubleToString(efficiencyUpgradeCost.get(id), 0) + "$";
        else
            costString = ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.buybefore").replace("%level%", Utils.getRoman(id)));

        return Utils.createItem(Material.DIAMOND, id + 1, id + 1 <= boughtLevel,
                ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.efficiency.name") + " " + Utils.getRoman(id + 1)),
                ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.efficiency.info")
                        .replace("%amount%", Utils.doubleToString(efficiencyUpgradeEfficiency.get(id) * 100, 0))), "",
                ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.price").replace("%price%", costString)));
    }

    private static ItemStack getOfflineUpgradeByID(int id, int boughtLevel)
    {
        String costString;
        if(id + 1 <= boughtLevel)
            costString = ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.bought"));
        else if(id + 1 == boughtLevel + 1)
            costString = "§e" + Utils.doubleToString(offlineUpgradeCost.get(id), 0) + "$";
        else
            costString = ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.buybefore").replace("%level%", Utils.getRoman(id)));

        return Utils.createItem(Material.EMERALD, id + 1, id + 1 <= boughtLevel,
                ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.offlinegem.name") + " " + Utils.getRoman(id + 1)),
                ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.offlinegem.info")
                .replace("%amount%", Utils.doubleToString(offlineUpgradeEfficiency.get(id) * 100, 0))), "",
                ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.price").replace("%price%", costString)));


    }
}
