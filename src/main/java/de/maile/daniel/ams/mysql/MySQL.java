/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.mysql;

import de.maile.daniel.ams.AMS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySQL
{
    public static Connection con;

    public MySQL()
    {
        connect();
    }

    public static void connect()
    {
        try
        {
            final Properties properties = new Properties();
            properties.setProperty("user", "root");
            properties.setProperty("password", "");
            properties.setProperty("autoReconnect", "true");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/minecraft", properties);
            AMS.INSTANCE.log("Mit MySQL verbunden");
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public static Connection getConnection()
    {
        return con;
    }
}
