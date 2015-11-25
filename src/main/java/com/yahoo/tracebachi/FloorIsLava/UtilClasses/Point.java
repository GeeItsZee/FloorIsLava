/* FloorIsLava Minigame for Multiplayer Minecraft
 * Copyright (C) 2015 Trace Bachi (tracebachi@yahoo.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
