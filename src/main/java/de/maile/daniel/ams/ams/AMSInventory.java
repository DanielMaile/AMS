/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.ams;

import de.maile.daniel.ams.AMS;
import de.maile.daniel.ams.mysql.AMSDatabase;
import de.maile.daniel.ams.utils.Utils;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
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
                if (!player.getOpenInventory().getTitle().equals(AMS_INVENTORY_NAME))
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
        ItemStack spawnerItem = Utils.createItem(Material.SPAWNER, 1, false, ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.spawner.name")),
                ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.info.amount").replace("%amount%", Long.toString(spawnerAmount))), "",
                ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.spawner.leftclick")),
                ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.spawner.rightclick")));

        double generatePerSecond = AMSManager.getPerSecondGeneration(player);
        double generatePerHour = generatePerSecond * 60 * 60;

        int offlineLevelBought = AMSDatabase.getOfflineUpgradeLevel(player.getUniqueId());
        String offlineGeneration;
        if (offlineLevelBought == 0)
            offlineGeneration = ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.info.none"));
        else
            offlineGeneration = "§a" + Utils.doubleToString(AMSUpgradeInventory.offlineUpgradeEfficiency.get(offlineLevelBought - 1) * 100, 0) + "%";

        int efficiencyLevelBought = AMSDatabase.getEfficiencyUpgradeLevel(player.getUniqueId());
        String efficiencyGeneration;
        if (efficiencyLevelBought == 0)
            efficiencyGeneration = ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.info.none"));
        else
            efficiencyGeneration = "§a+" + Utils.doubleToString(AMSUpgradeInventory.efficiencyUpgradeEfficiency.get(efficiencyLevelBought - 1) * 100, 0) + "%";

        ItemStack infoItem = Utils.createItem(Material.OAK_SIGN, 1, false, ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.info.name"))),
                ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.info.amount").replace("%amount%", Long.toString(spawnerAmount))), "",
                ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.info.perSecond").replace("%amount%", Utils.doubleToString(generatePerSecond, 2))),
                ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.info.perHour").replace("%amount%", Utils.doubleToString(generatePerHour, 2))), "",
                ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.info.upgrades").replace("%amount%", efficiencyGeneration)),
                ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.info.offlineGeneration").replace("%amount%", offlineGeneration)), "",
                ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.info.info")));

        ItemStack withdrawItem = Utils.createItem(Material.SUNFLOWER, 1, false, ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.withdraw.name")),
                ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.withdraw.click")), "",
                ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.withdraw.amount").replace("%amount%", Utils.doubleToString(balance, 2))));

        ItemStack upgradeItem = Utils.createItem(Material.ENDER_CHEST, 1, false, ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.upgrades.name")),
                ChatColor.translateAlternateColorCodes('&', config.getString("amsmenu.upgrades.click")));

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

                    if (items.getType() == Material.SPAWNER)
                    {
                        net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(items);
                        NBTTagCompound nbtTagCompound = nmsStack.getTag();

                        if (nbtTagCompound != null)
                        {
                            if (nbtTagCompound.hasKey("ams"))
                            {
                                String value = nbtTagCompound.getString("ams");

                                if(value != null)
                                {
                                    if (value.equals("spawner"))
                                    {
                                        spawnerAmount += items.getAmount();
                                        player.getInventory().removeItem(items);
                                    }
                                }
                            }
                        }
                    }
                }

                if (spawnerAmount > 0)
                {
                    AMSDatabase.setSpawners(player.getUniqueId(), AMSDatabase.getSpawners(player.getUniqueId()) + spawnerAmount);
                    updateInv(player, inventory);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("amsmenu.spawner.message.added").replace("%amount%", Long.toString(spawnerAmount))));
                }
                else
                {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("amsmenu.spawner.message.nospawner")));
                }
            }
            else if (clickType == ClickType.RIGHT)
            {
                if (AMSDatabase.getSpawners(player.getUniqueId()) >= 16)
                {

                    boolean added = false;
                    ItemStack[] items = player.getInventory().getContents();

                    for (int i = 0; i < 36; i++)
                    {
                        if (items[i] == null)
                        {
                            player.getInventory().addItem(Utils.createSpawners(16));
                            added = true;
                            break;
                        }
                        else
                        {
                            if (items[i].getType() == Material.SPAWNER)
                            {
                                if (items[i].getAmount() + 16 <= 64)
                                {
                                    items[i].setAmount(items[i].getAmount() + 16);
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
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("amsmenu.spawner.message.withdraw")));
                    }
                    else
                    {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("error.fullinv")));
                    }
                }
                else
                {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("amsmenu.spawner.message.notenough")));
                }
            }
        }
        else if (slot == WITHDRAW_POS)
        {
            double amsBalance = AMSDatabase.getBalance(player.getUniqueId());
            if (amsBalance >= 50d)
            {
                AMS.getEconomy().depositPlayer(player, amsBalance);
                AMSDatabase.setBalance(player.getUniqueId(), 0);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("amsmenu.withdraw.message.added").replace("%amount%", Utils.doubleToString(amsBalance, 0))));
                updateInv(player, inventory);
            }
            else
            {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("amsmenu.withdraw.message.notenough")));
            }
        }
        else if (slot == UPGRADE_POS)
        {
            AMSUpgradeInventory.openInventory(player);
        }
    }
}
