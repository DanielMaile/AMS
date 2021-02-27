/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.ams;

import de.maile.daniel.ams.AMS;
import de.maile.daniel.ams.mysql.AMSDatabase;
import de.maile.daniel.ams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AMSInventory
{
    public static final String AMS_INVENTORY_NAME = "§8AMS";

    public static final int SPAWNER_POS = 11;
    public static final int INFO_POS = 13;
    public static final int WITHDRAW_POS = 15;
    public static final int UPGRADE_POS = 22;

    public static int TASK_ID;

    public static void openInventory(Player player)
    {
        Inventory gui = Bukkit.createInventory(player, 36, AMS_INVENTORY_NAME);
        updateInv(player, gui);
        player.openInventory(gui);
        autoUpdate(player, gui);
    }

    public static void autoUpdate(Player player, Inventory gui)
    {
        TASK_ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(AMS.INSTANCE, new Runnable()
        {
            @Override
            public void run()
            {
                if(!player.getOpenInventory().getTopInventory().getTitle().equals(AMS_INVENTORY_NAME))
                {
                    Bukkit.getScheduler().cancelTask(TASK_ID);
                }
                else
                {
                    updateInv(player, gui);
                }
            }
        }, 20L, 20L);
    }

    private static void updateInv(Player player, Inventory gui)
    {
        AMSManager.updateOnlineBalance(player);
        long spawnerAmount = AMSDatabase.getSpawners(player.getUniqueId());

        double balance = AMSDatabase.getBalance(player.getUniqueId());

        YamlConfiguration config = AMS.INSTANCE.getConfig();

        ItemStack placeholderItem = Utils.getInventoryPlaceholderItem();
        ItemStack spawnerItem = Utils.createItem(Material.MOB_SPAWNER, 1, (byte) 0, false, config.getString("amsmenu.spawner.name"),
                config.getString("amsmenu.info.amount").replace("%amount%", Long.toString(spawnerAmount)), "",
                config.getString("amsmenu.spawner.leftclick"),
                config.getString("amsmenu.spawner.rightclick"));

        double generatePerSecond = AMSManager.getPerSecondGeneration(player);
        double generatePerHour = generatePerSecond * 60 * 60;

        int offlineLevelBought = AMSDatabase.getOfflineUpgradeLevel(player.getUniqueId());
        String offlineGeneration;
        if(offlineLevelBought == 0)
            offlineGeneration = config.getString("amsmenu.info.none");
        else
            offlineGeneration = "§a" + Utils.doubleToString(AMSUpgradeInventory.offlineUpgradeEfficiency[offlineLevelBought - 1] * 100, 0) + "%";

        int efficiencyLevelBought = AMSDatabase.getEfficiencyUpgradeLevel(player.getUniqueId());
        String efficiencyGeneration;
        if(efficiencyLevelBought == 0)
            efficiencyGeneration = config.getString("amsmenu.info.none");
        else
            efficiencyGeneration = "§a+" + Utils.doubleToString(AMSUpgradeInventory.efficiencyUpgradeEfficiency[efficiencyLevelBought - 1] * 100, 0) + "%";

        ItemStack infoItem = Utils.createItem(Material.SIGN, 1, (byte) 0, false, config.getString("amsmenu.info.name"),
                config.getString("amsmenu.info.amount").replace("%amount%", Long.toString(spawnerAmount)), "",
                config.getString("amsmenu.info.perSecond").replace("%amount%", Utils.doubleToString(generatePerSecond, 2)),
                config.getString("amsmenu.info.perHour").replace("%amount%", Utils.doubleToString(generatePerHour, 2)), "",
                config.getString("amsmenu.info.upgrades").replace("%amount%", efficiencyGeneration),
                config.getString("amsmenu.info.offlineGeneration").replace("%amount%", offlineGeneration), "",
                config.getString("amsmenu.info.info"));

        ItemStack withdrawItem = Utils.createItem(Material.DOUBLE_PLANT, 1, (byte) 0, false, config.getString("amsmenu.withdraw.name"),
                config.getString("amsmenu.withdraw.click"), "",
                config.getString("amsmenu.withdraw.amount").replace("%amount%", Utils.doubleToString(balance, 2)));

        ItemStack upgradeItem = Utils.createItem(Material.ENDER_CHEST, 1, (byte) 0, false, config.getString("amsmenu.upgrades.name"),
                config.getString("amsmenu.upgrades.click"));

        for (int i = 0; i < 36; i++)
        {
            if (i == SPAWNER_POS)
                gui.setItem(i, spawnerItem);
            else if (i == INFO_POS)
                gui.setItem(i, infoItem);
            else if (i == WITHDRAW_POS)
                gui.setItem(i, withdrawItem);
            else if (i == UPGRADE_POS)
                gui.setItem(i, upgradeItem);
            else
                gui.setItem(i, placeholderItem);
        }
    }

    public static void clicked(Player player, int slot, ItemStack clickedItem, Inventory inventory, ClickType clickType)
    {
        if (slot == SPAWNER_POS)
        {
            if (clickType == ClickType.LEFT)
            {
                int spawnerAmount = 0;
                for (ItemStack items : player.getInventory().getContents())
                {
                    if (items == null)
                        continue;

                    if (items.getType() == Material.MOB_SPAWNER)
                    {
                        spawnerAmount += items.getAmount();
                        player.getInventory().removeItem(items);
                    }
                }

                if (spawnerAmount > 0)
                {
                    AMSDatabase.setSpawners(player.getUniqueId(), AMSDatabase.getSpawners(player.getUniqueId()) + spawnerAmount);
                    updateInv(player, inventory);
                    player.sendMessage(AMS.INSTANCE.getConfig().getString("amsmenu.spawner.message.added").replace("%amount%", Long.toString(spawnerAmount)));
                }
                else
                {
                    player.sendMessage(AMS.INSTANCE.getConfig().getString("amsmenu.spawner.message.nospawner"));
                }
            }
            else if (clickType == ClickType.RIGHT)
            {
                if(AMSDatabase.getSpawners(player.getUniqueId()) >= 16)
                {
                    boolean added = false;
                    for (ItemStack items : player.getInventory().getContents())
                    {
                        if (items == null)
                        {
                            player.getInventory().addItem(Utils.createSpawners(16));
                            added = true;
                            break;
                        }
                        else
                        {
                            if (items.getType() == Material.MOB_SPAWNER)
                            {
                                if (items.getAmount() + 16 <= 64)
                                {
                                    items.setAmount(items.getAmount() + 16);
                                    added = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (added)
                    {
                        AMSDatabase.setSpawners(player.getUniqueId(), AMSDatabase.getSpawners(player.getUniqueId()) - 16);
                        updateInv(player, inventory);
                        player.sendMessage(AMS.INSTANCE.getConfig().getString("amsmenu.spawner.message.withdraw"));
                    }
                    else
                    {
                        player.sendMessage(AMS.INSTANCE.getConfig().getString("amsmenu.spawner.message.fullinv"));
                    }
                }
                else
                {
                    player.sendMessage(AMS.INSTANCE.getConfig().getString("amsmenu.spawner.message.notenough"));
                }
            }
        }
        else if (slot == WITHDRAW_POS)
        {
            double amsBalance = AMSDatabase.getBalance(player.getUniqueId());
            if(amsBalance >= 50d)
            {
                AMS.getEconomy().depositPlayer(player, amsBalance);
                AMSDatabase.setBalance(player.getUniqueId(), 0);
                player.sendMessage(AMS.INSTANCE.getConfig().getString("amsmenu.withdraw.message.added").replace("%amount%", Utils.doubleToString(amsBalance, 0)));
                updateInv(player, inventory);
            }
            else
            {
                player.sendMessage(AMS.INSTANCE.getConfig().getString("amsmenu.withdraw.message.notenough"));
            }
        }
        else if(slot == UPGRADE_POS)
        {
            AMSUpgradeInventory.openInventory(player);
        }
    }
}
