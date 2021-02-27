/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams;

import de.maile.daniel.ams.ams.AMSManager;
import de.maile.daniel.ams.ams.AMSCommand;
import de.maile.daniel.ams.commands.MoneyCommand;
import de.maile.daniel.ams.commands.SpawnerCommand;
import de.maile.daniel.ams.listeners.InventoryListener;
import de.maile.daniel.ams.listeners.JoinQuitListener;
import de.maile.daniel.ams.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.SQLException;

public final class AMS extends JavaPlugin
{
    public static String PREFIX = "§b[AMS] §7";
    public static AMS INSTANCE;

    @Override
    public void onEnable()
    {
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
    }

    public void log(String text)
    {
        Bukkit.getConsoleSender().sendMessage(PREFIX + text);
    }

    private void register()
    {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new JoinQuitListener(), this);

        Bukkit.getPluginCommand("ams").setExecutor(new AMSCommand());
        Bukkit.getPluginCommand("money").setExecutor(new MoneyCommand());
        Bukkit.getPluginCommand("spawner").setExecutor(new SpawnerCommand());
    }

    private void createDatabase()
    {
        try
        {
            MySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS money (id int NOT NULL AUTO_INCREMENT," +
                    "uuid VARCHAR(64), balance DOUBLE PRECISION, PRIMARY KEY (id));").executeUpdate();
            MySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS ams (id int NOT NULL AUTO_INCREMENT," +
                    "uuid VARCHAR(64), spawners BIGINT, balance DOUBLE PRECISION," +
                    "online_time BIGINT, offline_time BIGINT, efficiency_upgrade INT, offline_upgrade INT, PRIMARY KEY (id));").executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
