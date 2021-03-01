/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams;

import de.maile.daniel.ams.ams.AMSManager;
import de.maile.daniel.ams.ams.AMSCommand;
import de.maile.daniel.ams.ams.AMSUpgradeInventory;
import de.maile.daniel.ams.commands.SpawnerCommand;
import de.maile.daniel.ams.listeners.BlockListener;
import de.maile.daniel.ams.listeners.EntityDeathListener;
import de.maile.daniel.ams.listeners.InventoryListener;
import de.maile.daniel.ams.listeners.JoinQuitListener;
import de.maile.daniel.ams.mysql.MySQL;
import de.maile.daniel.ams.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public final class AMS extends JavaPlugin
{
    public static String PREFIX = "[AMS] ";
    public static AMS INSTANCE;

    private static Economy econ = null;

    private  YamlConfiguration yamlConfiguration;

    @Override
    public void onEnable()
    {
        createFiles();
        INSTANCE = this;

        if (!setupEconomy())
        {
            log(ChatColor.translateAlternateColorCodes('&', yamlConfiguration.getString("error.noEcomony")));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        log(ChatColor.translateAlternateColorCodes('&', yamlConfiguration.getString("info.pluginEnabled")));

        register();
        MySQL.connect();
        createDatabase();
        AMSUpgradeInventory.loadValues();
        AMSManager.startUpdating();
        addSpawnerRecipe();
    }

    private void addSpawnerRecipe()
    {
        if(getConfig().getBoolean("enable_spawner_crafting"))
        {
            ItemStack itemStack = Utils.createSpawners(1);
            NamespacedKey key = new NamespacedKey(this, "spawner");
            ShapedRecipe recipe = new ShapedRecipe(key, itemStack);
            recipe.shape("III", "IGI", "III");
            recipe.setIngredient('I', Material.IRON_INGOT);
            recipe.setIngredient('G', Material.GOLD_BLOCK);
            Bukkit.addRecipe(recipe);
        }
    }

    private void createFiles()
    {
        File folder = new File("plugins/AMS");
        if(!folder.exists())
            folder.mkdirs();

        File file = new File("plugins/AMS/config.yml");
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if(!file.exists())
        {
            createDefaultConfig();
            try
            {
                yamlConfiguration.save(file);
                yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public YamlConfiguration getConfig()
    {
        return yamlConfiguration;
    }

    private void createDefaultConfig()
    {
        yamlConfiguration.set("drops.player.enabled", true);
        yamlConfiguration.set("drops.player.chance", 0.01d);
        yamlConfiguration.set("drops.entity.enabled", true);
        yamlConfiguration.set("drops.entity.chance", 0.01d);
        yamlConfiguration.set("enable_spawner_crafting", true);
        yamlConfiguration.set("allow_spawner_break", true);
        yamlConfiguration.set("efficiencyUpgradeEfficiency", new double[] {0.05d, 0.15d, 0.25d, 0.5d, 0.75d, 1d, 2d});
        yamlConfiguration.set("efficiencyUpgradeCost", new double[] {100d, 500d, 1000d, 5000d, 10000d, 25000d, 100000d});
        yamlConfiguration.set("offlineUpgradeEfficiency", new double[] {0.02d, 0.05d, 0.10d, 0.20d, 0.30d, 0.40d, 0.50d});
        yamlConfiguration.set("offlineUpgradeCost",new double[] {100d, 500d, 1000d, 5000d, 10000d, 25000d, 100000d});
        yamlConfiguration.set("generation_multiplier", 0.1d);
        yamlConfiguration.set("error.noEcomony", "No ecomony plugin was found.");
        yamlConfiguration.set("error.onlyPlayers", "This command can only be executed by players.");
        yamlConfiguration.set("info.pluginEnabled", "Plugin enabled.");
        yamlConfiguration.set("info.pluginDisabled", "Plugin disabled.");
        yamlConfiguration.set("info.connectedToDatabase", "Connected to Database.");
        yamlConfiguration.set("info.offlineGeneration", "&7While you were offline your AMS generated &a%amount%$");
        yamlConfiguration.set("spawner.name", "&a&lSpawner");
        yamlConfiguration.set("spawner.info.line1", "&7Add spawners to your AMS so");
        yamlConfiguration.set("spawner.info.line2", "&7they can generate money for you.");
        yamlConfiguration.set("amsmenu.spawner.name", "&a&lManage spawners");
        yamlConfiguration.set("amsmenu.spawner.leftclick", "&aLeftclick &7>> Add all spawners from your inventory.");
        yamlConfiguration.set("amsmenu.spawner.rightclick", "&aRightclick &7>> Withdraw 16 spawners from your AMS.");
        yamlConfiguration.set("amsmenu.spawner.message.nospawner", "&7There are no spawners in your inventory.");
        yamlConfiguration.set("amsmenu.spawner.message.added", "&7You have added &a%amount% spawners &7to your AMS.");
        yamlConfiguration.set("amsmenu.spawner.message.withdraw", "&7You withdrew &a16 spawners &7from your AMS.");
        yamlConfiguration.set("amsmenu.spawner.message.fullinv", "&7Your inventory is full.");
        yamlConfiguration.set("amsmenu.spawner.message.notenough", "&7There are not enough spawners in your ams.");
        yamlConfiguration.set("amsmenu.info.name", "&6&lInfo");
        yamlConfiguration.set("amsmenu.info.none", "&cNone");
        yamlConfiguration.set("amsmenu.info.amount", "&7There are &a%amount% &7spawner in your AMS.");
        yamlConfiguration.set("amsmenu.info.perSecond", "&7$ per second: &a%amount%");
        yamlConfiguration.set("amsmenu.info.perHour", "&7$ per hour: &a%amount%");
        yamlConfiguration.set("amsmenu.info.upgrades", "&7Upgrades: %amount%");
        yamlConfiguration.set("amsmenu.info.offlineGeneration", "&7Offline generation: %amount%");
        yamlConfiguration.set("amsmenu.info.info", "&7&oAdd more spawners or buy upgrades to generate more!");
        yamlConfiguration.set("amsmenu.withdraw.name", "&a&lWithdraw money");
        yamlConfiguration.set("amsmenu.withdraw.click", "&7Click to withdraw your money.");
        yamlConfiguration.set("amsmenu.withdraw.amount", "&7Balance: &a%amount%$");
        yamlConfiguration.set("amsmenu.withdraw.message.notenough", "&7You need at least 50$ to withdraw money.");
        yamlConfiguration.set("amsmenu.withdraw.message.added", "&7You withdrew &a%amount%$&7.");
        yamlConfiguration.set("amsmenu.upgrades.name", "&9&lUpgrades");
        yamlConfiguration.set("amsmenu.upgrades.click", "&7Click to open the upgrade menu");
        yamlConfiguration.set("upgrademenu.back", "&c<-- Back to AMS");
        yamlConfiguration.set("upgrademenu.offlinegem.name", "&aEfficiency");
        yamlConfiguration.set("upgrademenu.offlinegem.info", "&7Offline generation: &a%amount%% &7from your Online Generation.");
        yamlConfiguration.set("upgrademenu.efficiency.name", "&aOffline Gem");
        yamlConfiguration.set("upgrademenu.efficiency.info", "&7Generation: &a+%amount%%");
        yamlConfiguration.set("upgrademenu.bought", "&aBought");
        yamlConfiguration.set("upgrademenu.buybefore", "&cBuy Level %level% before");
        yamlConfiguration.set("upgrademenu.price", "&7Price: %price%");
        yamlConfiguration.set("upgrademenu.message.alreadybought", "&cYou have already bought this upgrade.");
        yamlConfiguration.set("upgrademenu.message.notenoughmoney", "&cNot enough money to buy this upgrade.");
        yamlConfiguration.set("upgrademenu.message.notunlocked", "&cYou have to buy the level before first.");
        yamlConfiguration.set("upgrademenu.message.boughtofflinegem", "&7You have bought &aOffline Gem %level%");
        yamlConfiguration.set("upgrademenu.message.boughtefficiency", "&7You have bought &aEfficiency %level%");
    }

    @Override
    public void onDisable()
    {
        log(ChatColor.translateAlternateColorCodes('&', yamlConfiguration.getString("info.pluginDisabled")));
        MySQL.disconnect();
    }

    public void log(String text)
    {
        getLogger().info(text);
    }

    private void register()
    {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new JoinQuitListener(), this);
        pluginManager.registerEvents(new BlockListener(), this);
        pluginManager.registerEvents(new EntityDeathListener(), this);

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
