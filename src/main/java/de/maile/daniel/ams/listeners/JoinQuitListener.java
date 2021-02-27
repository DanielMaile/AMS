/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.listeners;

import de.maile.daniel.ams.ams.AMSManager;
import de.maile.daniel.ams.mysql.MoneyDatabase;
import de.maile.daniel.ams.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinQuitListener implements Listener
{
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        //Later be changed to hasNeverPlayedBefore
        if(!MoneyDatabase.playerExists(player.getUniqueId()))
        {
            MoneyDatabase.addNewPlayer(player.getUniqueId());
        }

        //AMS
        double generated = AMSManager.getAndUpdateOfflineBalance(player);
        if(generated > 0)
            player.sendMessage("§7Während du Offline warst hat deine AMS §a" + Utils.doubleToString(generated, 2) + "$ §7generiert");
    }
}
