/*
 * This file is part of FloorIsLava.
 *
 * FloorIsLava is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FloorIsLava is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FloorIsLava.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    private final double yaw;
    private final double pitch;

    public Point(int x, int y, int z)
    {
        this(x, y, z, 0.0, 0.0);
    }

    public Point(int x, int y, int z, double yaw, double pitch)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Point(ConfigurationSection section)
    {
        this.x = section.getInt("x");
        this.y = section.getInt("y");
        this.z = section.getInt("z");
        this.yaw = section.getDouble("yaw", 0.0);
        this.pitch = section.getDouble("pitch", 0.0);
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

    public double yaw()
    {
        return yaw;
    }

    public double pitch()
    {
        return pitch;
    }

    @Override
    public String toString()
    {
        return x + ", " + y + ", " + z + ", " + yaw + ", " + pitch;
    }
}
