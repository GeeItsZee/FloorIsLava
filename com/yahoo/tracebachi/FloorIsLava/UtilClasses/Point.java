package com.yahoo.tracebachi.FloorIsLava.UtilClasses;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class Point
{
    private final int x;
    private final int y;
    private final int z;

    public Point(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(ConfigurationSection section)
    {
        this.x = section.getInt("x");
        this.y = section.getInt("y");
        this.z = section.getInt("z");
    }

    public int x()
    {
        return x;
    }

    public int y()
    {
        return y;
    }

    public int z()
    {
        return z;
    }

    @Override
    public String toString()
    {
        return x + ", " + y + ", " + z;
    }
}
