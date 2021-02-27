/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.listeners;

import de.maile.daniel.ams.AMS;
import de.maile.daniel.ams.ams.AMSManager;
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
        double generated = AMSManager.getAndUpdateOfflineBalance(player);
        if(generated > 0)
            player.sendMessage(AMS.INSTANCE.getConfig().getString("info.offlineGeneration").replace("%amount%", Utils.doubleToString(generated, 2)));
    }
}
