/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.ams;

import de.maile.daniel.ams.AMS;
import de.maile.daniel.ams.mysql.AMSDatabase;
import de.maile.daniel.ams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

        ItemStack placeholderItem = Utils.getInventoryPlaceholderItem();
        ItemStack spawnerItem = Utils.createItem(Material.MOB_SPAWNER, 1, (byte) 0, false, "§a§lSpawner verwalten",
                "§7Es befinden sich §a" + spawnerAmount + " §7Spawner in der AMS.", "",
                "§aLinksklick §7>> Alle Spawner aus dem Inventar hinzufügen.",
                "§aRechtsklick §7>> 16 Spieler aus der AMS nehmen.");

        double generatePerSecond = AMSManager.getPerSecondGeneration(player);
        double generatePerHour = generatePerSecond * 60 * 60;

        int offlineLevelBought = AMSDatabase.getOfflineUpgradeLevel(player.getUniqueId());
        String offlineGeneration;
        if(offlineLevelBought == 0)
            offlineGeneration = "§cKeine";
        else
            offlineGeneration = "§a" + Utils.doubleToString(AMSUpgradeInventory.offlineUpgradeEfficiency[offlineLevelBought - 1] * 100, 0) + "%";

        int efficiencyLevelBought = AMSDatabase.getEfficiencyUpgradeLevel(player.getUniqueId());
        String efficiencyGeneration;
        if(efficiencyLevelBought == 0)
            efficiencyGeneration = "§cKeine";
        else
            efficiencyGeneration = "§a+" + Utils.doubleToString(AMSUpgradeInventory.efficiencyUpgradeEfficiency[efficiencyLevelBought - 1] * 100, 0) + "%";

        ItemStack infoItem = Utils.createItem(Material.SIGN, 1, (byte) 0, false, "§6§lInfo",
                "§7Du hast §a" + spawnerAmount + " Spawner §7in der AMS.", "",
                "§7$ pro Sekunde: §a" + Utils.doubleToString(generatePerSecond, 2),
                "§7$ pro Stunde: §a" + Utils.doubleToString(generatePerHour, 2), "",
                "§7Upgrades: " + efficiencyGeneration,
                "§7Offline Generation: " + offlineGeneration, "",
                "§7§oFüge mehr Spawner hinzu, um mehr zu generieren!");

        ItemStack withdrawItem = Utils.createItem(Material.DOUBLE_PLANT, 1, (byte) 0, false, "§a§lGeld abheben",
                "§7Klicke hier um Geld abzuheben.", "", "§7Verfügbarer Betrag: §a" + Utils.doubleToString(balance, 2) + "$");

        ItemStack upgradeItem = Utils.createItem(Material.ENDER_CHEST, 1, (byte) 0, false, "§9§lUpgrades",
                "§7Klicke hier um das Upgrade Menü zu öffnen");

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
                    player.sendMessage("§7Du hast §a" + spawnerAmount + " Spawner §7in den AMS eingezahlt.");
                }
                else
                {
                    player.sendMessage("§7Du hast keine Spawner im Inventar.");
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
                        player.sendMessage("§7Du hast §a16 Spawner §7aus der AMS genommen.");
                    }
                    else
                    {
                        player.sendMessage("§7Dein Inventar ist voll.");
                    }
                }
                else
                {
                    player.sendMessage("§7Du hast nicht genug Spawner in der AMS.");
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
                player.sendMessage("§a" + Utils.doubleToString(amsBalance, 0) + "$ §7wurden zu deinem Konto hinzugefügt.\nNeuer Kontostand: §a"
                        + Utils.doubleToString(AMS.getEconomy().getBalance(player), 0) + "$");
                updateInv(player, inventory);
            }
            else
            {
                player.sendMessage("§7Du musst mindestens 50$ besitzen, um Geld auszuzahlen.");
            }
        }
        else if(slot == UPGRADE_POS)
        {
            AMSUpgradeInventory.openInventory(player);
        }
    }
}
