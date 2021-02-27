/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AMSDatabase
{
    public static void addNewPlayer(UUID uuid)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO ams (uuid, spawners," +
                    "balance, online_time, offline_time, efficiency_upgrade, offline_upgrade) VALUES (?, 0, 0, 0, 0, 0, 0);");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean playerExists(UUID uuid)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT uuid FROM ams WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            final ResultSet rs = ps.executeQuery();
            return rs.next();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static double getBalance(UUID uuid)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT balance FROM ams WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            final ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                return rs.getDouble("balance");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getSpawners(UUID uuid)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT spawners FROM ams WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            final ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                return rs.getLong("spawners");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setBalance(UUID uuid, double balance)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE ams SET balance = ? WHERE uuid = ?");
            ps.setDouble(1, balance);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void setSpawners(UUID uuid, long spawners)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE ams SET spawners = ? WHERE uuid = ?");
            ps.setLong(1, spawners);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void incOnlineTime(UUID uuid)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE ams SET online_time = online_time + 1 WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void incOfflineTime(UUID uuid)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE ams SET offline_time = offline_time + 1 WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static int getOfflineUpgradeLevel(UUID uuid)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT offline_upgrade FROM ams WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            final ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                return rs.getInt("offline_upgrade");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setOfflineUpgradeLevel(UUID uuid, int offlineUpgradeLevel)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE ams SET offline_upgrade = ? WHERE uuid = ?");
            ps.setInt(1, offlineUpgradeLevel);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void setEfficiencyUpgradeLevel(UUID uuid, int efficiencyUpgradeLevel)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE ams SET efficiency_upgrade = ? WHERE uuid = ?");
            ps.setInt(1, efficiencyUpgradeLevel);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static int getEfficiencyUpgradeLevel(UUID uuid)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT efficiency_upgrade FROM ams WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            final ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                return rs.getInt("efficiency_upgrade");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getOnlineTime(UUID uuid)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT online_time FROM ams WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            final ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                return rs.getLong("online_time");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }


    public static long getOfflineTime(UUID uuid)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT offline_time FROM ams WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            final ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                return rs.getLong("offline_time");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setOnlineTime(UUID uuid, long onlineTime)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE ams SET online_time = ? WHERE uuid = ?");
            ps.setLong(1, onlineTime);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void setOfflineTime(UUID uuid, long offlineTime)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE ams SET offline_time = ? WHERE uuid = ?");
            ps.setLong(1, offlineTime);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static List<UUID> getAllUUIDs()
    {
        List<UUID> uuids = new ArrayList<UUID>();
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT uuid FROM ams");
            final ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                uuids.add(UUID.fromString(rs.getString(("uuid"))));
            }
            return uuids;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
