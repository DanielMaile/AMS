/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.ams;

import de.maile.daniel.ams.mysql.AMSDatabase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AMSCommand implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage("Dieser Befehl ist nur von Spielern verwendbar");
            return true;
        }

        Player player = (Player) sender;
        if(!AMSDatabase.playerExists(player.getUniqueId()))
            AMSDatabase.addNewPlayer(player.getUniqueId());
        AMSInventory.openInventory(player);
        return true;
    }
}
