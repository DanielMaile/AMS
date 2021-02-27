/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.commands;

import de.maile.daniel.ams.AMS;
import de.maile.daniel.ams.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnerCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(AMS.INSTANCE.getConfig().getString("error.onlyPlayers"));
            return true;
        }

        Player player = (Player) sender;
        player.getInventory().addItem(Utils.createSpawners(64));
        return true;
    }
}
