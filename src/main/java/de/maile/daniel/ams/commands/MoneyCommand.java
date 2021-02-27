/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.commands;

import de.maile.daniel.ams.mysql.MoneyDatabase;
import de.maile.daniel.ams.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(args.length == 0 && sender instanceof Player)
        {
            Player player = (Player) sender;
            if(MoneyDatabase.playerExists(player.getUniqueId()))
            {
                sender.sendMessage("§aDein Kontostand: " + Utils.doubleToString(MoneyDatabase.getBalance(player.getUniqueId()), 0) + "$");
            }
            else
            {
                sender.sendMessage("§cVon diesem Spieler sind keine Daten vorhanden");
            }
        }
        else if(args.length == 1)
        {
            Player player = Bukkit.getPlayer(args[0]);
            if(player == null)
            {
                //Player Offline
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                if(offlinePlayer == null)
                {
                    //No valid Playername
                    sender.sendMessage("§cVon diesem Spieler sind keine Daten vorhanden");
                }
                else
                {
                    if(MoneyDatabase.playerExists(offlinePlayer.getUniqueId()))
                    {
                        sender.sendMessage("§aKontostand von " + args[0] + ": "
                                + Utils.doubleToString(MoneyDatabase.getBalance(offlinePlayer.getUniqueId()),0) + "$");
                    }
                    else
                    {
                        sender.sendMessage("§cVon diesem Spieler sind keine Daten vorhanden");
                    }
                }
            }
            else
            {
                if(MoneyDatabase.playerExists(player.getUniqueId()))
                {
                    sender.sendMessage("§aKontostand von " + args[0] + ": "
                            + Utils.doubleToString(MoneyDatabase.getBalance(player.getUniqueId()),2)+ "$");
                }
                else
                {
                    sender.sendMessage("§cVon diesem Spieler sind keine Daten vorhanden");
                }
            }
        }
        else
        {
            sender.sendMessage("§cVerwende /money (spielername)");
        }
        return false;
    }
}
