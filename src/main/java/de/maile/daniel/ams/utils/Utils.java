/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
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
import java.util.UUID;

public class Utils
{
    public static ItemStack createItem(Material material, int amount, byte subID, boolean enchanted, String displayName, String... loreString)
    {
        List<String> lore = new ArrayList<>();

        ItemStack itemStack = new ItemStack(material, amount, subID);
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
        lore.add("§7Lege Spawner in den AMS, damit");
        lore.add("§7sie Geld für dich produzieren.");

        itemStack = new ItemStack(Material.MOB_SPAWNER, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§a§lSpawner");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack getInventoryPlaceholderItem()
    {
        return createItem(Material.STAINED_GLASS_PANE, 1, (byte) 7, false, " ");
    }

    public static ItemStack getBackHead(String displayName)
    {
        CustomSkull customSkull = new CustomSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==",
                1, "§c<-- Zurück zur AMS");
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
}
