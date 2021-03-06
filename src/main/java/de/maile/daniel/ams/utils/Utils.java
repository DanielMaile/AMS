/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.utils;

import de.maile.daniel.ams.AMS;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagList;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Utils
{
    public static ItemStack createItem(Material material, int amount, boolean enchanted, String displayName, String... loreString)
    {
        List<String> lore = new ArrayList<>();

        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);

        for (String s : loreString)
        {
            lore.add(s);
        }

        itemMeta.setLore(lore);
        if(enchanted)
        {
            itemMeta.addEnchant(Enchantment.OXYGEN, 1, false);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack createSpawners(int amount)
    {
        ItemStack itemStack;

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("spawner.info.line1")));
        lore.add(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("spawner.info.line2")));

        itemStack = new ItemStack(Material.SPAWNER, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("spawner.name")));
        itemMeta.setLore(lore);
        itemMeta.addEnchant(Enchantment.OXYGEN, 1, false);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);

        net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbtTagCompound = nmsStack.getOrCreateTag();
        nbtTagCompound.setString("ams", "spawner");
        nmsStack.setTag(nbtTagCompound);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public static ItemStack createGift(long amount, String playername)
    {
        ItemStack itemStack;

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("gift.item.amount")).replace("%amount%", Long.toString(amount)));
        lore.add(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("gift.item.from")).replace("%playername%", playername));
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("gift.item.rightclick")));

        itemStack = new ItemStack(Material.PAPER, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("gift.item.name")));
        itemMeta.setLore(lore);
        itemMeta.addEnchant(Enchantment.OXYGEN, 1, false);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);

        net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbtTagCompound = nmsStack.getOrCreateTag();
        nbtTagCompound.setString("ams", "gift");
        nbtTagCompound.setLong("amount", amount);
        nmsStack.setTag(nbtTagCompound);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public static ItemStack getInventoryPlaceholderItem()
    {
        return createItem(Material.GRAY_STAINED_GLASS_PANE, 1, false, " ");
    }

    public static ItemStack getBackHead(String displayName)
    {
        CustomSkull customSkull = new CustomSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==",
                1, ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("upgrademenu.back")));
        return customSkull.toItemStack();
    }

    public static long getPlayTime(UUID uuid)
    {
        File worldFolder = new File(((World) Bukkit.getServer().getWorlds().get(0)).getWorldFolder(), "stats");
        File playerStatistics = new File(worldFolder, uuid + ".json");
        if (playerStatistics.exists())
        {
            try
            {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(playerStatistics));
                return (Long) jsonObject.get("stat.playOneMinute");
            }
            catch (IOException | ParseException e)
            {
            }
        }

        return 0L;
    }

    public static String getRoman(int number)
    {
        if(number < 1 || number > 10)
        {
            return "";
        }
        else
        {
            String[] romanNames = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
            return romanNames[number - 1];
        }
    }

    public static String doubleToString(double d, int decimals)
    {
        String pattern;
        if(decimals > 0)
        {
            pattern = "#,##0.";
            for (int i = 0; i < decimals; i++)
            {
                pattern += "0";
            }
        }
        else
        {
            pattern = "#,##0";
        }
        DecimalFormat formatter = new DecimalFormat(pattern);
        return formatter.format(d);
    }

    public static void dropSpawner(Location location, double chance)
    {
        Random random = new Random();
        if(random.nextDouble() <= chance)
        {
            ItemStack itemStack = Utils.createSpawners(1);
            location.getWorld().dropItemNaturally(location, itemStack);
        }
    }
}
