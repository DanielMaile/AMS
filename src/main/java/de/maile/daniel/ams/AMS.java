/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams;

import de.maile.daniel.ams.ams.AMSManager;
import de.maile.daniel.ams.ams.AMSCommand;
import de.maile.daniel.ams.commands.SpawnerCommand;
import de.maile.daniel.ams.listeners.InventoryListener;
import de.maile.daniel.ams.listeners.JoinQuitListener;
import de.maile.daniel.ams.mysql.MySQL;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class AMS extends JavaPlugin
{
    public static String PREFIX = "[AMS] ";
    public static AMS INSTANCE;

    private static Economy econ = null;

    @Override
    public void onEnable()
    {
        if (!setupEconomy())
        {
            log("Es wurde kein Economy Plugin gefunden");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        log("Plugin aktiviert");
        INSTANCE = this;
        register();
        MySQL.connect();
        createDatabase();
        AMSManager.startUpdating();
    }

    @Override
    public void onDisable()
    {
        log("Plugin deaktiviert");
        MySQL.disconnect();
    }

    public void log(String text)
    {
        getLogger().info(PREFIX + text);
    }

    private void register()
    {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new JoinQuitListener(), this);

        Bukkit.getPluginCommand("ams").setExecutor(new AMSCommand());
        Bukkit.getPluginCommand("spawner").setExecutor(new SpawnerCommand());
    }

    private void createDatabase()
    {
        try
        {
            MySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS ams (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "uuid VARCHAR(64), spawners BIGINT, balance DOUBLE PRECISION, " +
                    "online_time BIGINT, offline_time BIGINT, efficiency_upgrade INT, offline_upgrade INT);").executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static Economy getEconomy()
    {
        return econ;
    }

    private boolean setupEconomy()
    {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
        {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
        {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
