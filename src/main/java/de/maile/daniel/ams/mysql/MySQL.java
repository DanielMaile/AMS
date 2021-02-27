/*
 * Copyright (c) 2021 Daniel Maile, Alle Rechte vorbehalten.
 */

package de.maile.daniel.ams.mysql;

import de.maile.daniel.ams.AMS;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL
{
    public static Connection con;

    public static void connect()
    {

        try
        {
            File file = new File("plugins/AMS");
            if(!file.exists())
                file.mkdirs();

            DriverManager.registerDriver(new org.sqlite.JDBC());
            con = DriverManager.getConnection("jdbc:sqlite:plugins/AMS/playerdata.db");
            AMS.INSTANCE.log("Mit Datenbank verbunden");
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public static void disconnect()
    {
        try
        {
            con.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static Connection getConnection()
    {
        return con;
    }
}
