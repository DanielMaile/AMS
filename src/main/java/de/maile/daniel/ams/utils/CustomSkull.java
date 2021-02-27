/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomSkull
{

    ItemStack head;
    SkullMeta skullMeta;
    String base64;

    public CustomSkull(String base64, int amount, String displayName, String... loreString)
    {
        this.base64 = base64;
        head = new ItemStack(Material.PLAYER_HEAD, amount);
        head.getItemMeta().setDisplayName(displayName);
        createSkull(head, displayName, base64, loreString);
    }

    public ItemStack toItemStack()
    {
        return head;
    }

    public ItemStack createSkull(ItemStack item, String displayName, String base64, String... loreString)
    {
        skullMeta = (SkullMeta) item.getItemMeta();
        editMeta(skullMeta, base64);
        skullMeta.setDisplayName(displayName);
        List<String> lore = new ArrayList<>();
        for (String s : loreString)
        {
            lore.add(s);
        }
        skullMeta.setLore(lore);
        item.setItemMeta(skullMeta);
        return item;
    }

    private void editMeta(SkullMeta skullMeta, String b64)
    {
        Field metaProfileField = null;
        try
        {
            if (metaProfileField == null)
            {
                metaProfileField = skullMeta.getClass().getDeclaredField("profile");
                metaProfileField.setAccessible(true);
            }
            metaProfileField.set(skullMeta, getProfile(b64));
        }
        catch (NoSuchFieldException | IllegalAccessException ex2)
        {
            ex2.printStackTrace();
        }
    }

    private GameProfile getProfile(String b64)
    {
        UUID uuid = new UUID(b64.substring(b64.length() - 20).hashCode(), b64.substring(b64.length() - 10).hashCode());
        GameProfile profile = new GameProfile(uuid, "");
        profile.getProperties().put("textures", new Property("textures", b64));
        return profile;
    }
}
