/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MoneyDatabase
{
    public static void addNewPlayer(UUID uuid)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO money (uuid, balance) VALUES (?, 100);");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static double getBalance(UUID uuid)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT balance FROM money WHERE uuid = ?");
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

    public static boolean playerExists(UUID uuid)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT uuid FROM money WHERE uuid = ?");
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

    public static void setBalance(UUID uuid, double balance)
    {
        try
        {
            final PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE money SET balance = ? WHERE uuid = ?");
            ps.setDouble(1, balance);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
