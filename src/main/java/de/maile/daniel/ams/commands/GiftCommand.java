package de.maile.daniel.ams.commands;

import de.maile.daniel.ams.AMS;
import de.maile.daniel.ams.mysql.AMSDatabase;
import de.maile.daniel.ams.utils.Utils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiftCommand implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
            return true;

        if (args.length != 1)
        {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("gift.message.usage")));
            return true;
        }

        if (!NumberUtils.isNumber(args[0]))
        {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("gift.message.novalue"))
                    .replace("%value%", args[0]));
            return true;
        }

        Player player = (Player) sender;
        long giftAmount = NumberUtils.toLong(args[0]);
        long spawnerBalance = AMSDatabase.getSpawners(player.getUniqueId());

        if (giftAmount < 1)
        {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("gift.message.atleast")));
            return true;
        }

        if (spawnerBalance < giftAmount)
        {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("gift.message.notenough")));
            return true;
        }

        ItemStack gift = Utils.createGift(giftAmount, player.getDisplayName());
        boolean added = false;
        ItemStack[] items = player.getInventory().getContents();

        for (int i = 0; i < 36; i++)
        {
            if (items[i] == null)
            {
                player.getInventory().addItem(gift);
                added = true;
                break;
            }
        }

        if (added)
        {
            AMSDatabase.setSpawners(player.getUniqueId(), spawnerBalance - giftAmount);
        }
        else
        {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', AMS.INSTANCE.getConfig().getString("error.fullinv")));
        }

        return true;
    }
}
