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
package com.gmail.tracebachi.FloorIsLava.Utils;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Random;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 8/26/16.
 */
public class CuboidArea
{
    private static final Random RANDOM = new Random();

    private Point upper;
    private Point lower;

    public CuboidArea(ConfigurationSection alpha, ConfigurationSection beta)
    {
        Preconditions.checkNotNull(alpha, "Point was null.");
        Preconditions.checkNotNull(beta, "Point was null.");

        int alphaX = alpha.getInt("x");
        int alphaY = alpha.getInt("y");
        int alphaZ = alpha.getInt("z");
        int betaX = beta.getInt("x");
        int betaY = beta.getInt("y");
        int betaZ = beta.getInt("z");

        upper = new Point(Math.max(alphaX, betaX),
            Math.max(alphaY, betaY), Math.max(alphaZ, betaZ));
        lower = new Point(Math.min(alphaX, betaX),
            Math.min(alphaY, betaY), Math.min(alphaZ, betaZ));
    }

    public Point getUpper()
    {
        return upper;
    }

    public Point getLower()
    {
        return lower;
    }

    public boolean isInside(Location loc)
    {
        return isInside(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public boolean isInside(int locX, int locY, int locZ)
    {
        if(locY > upper.y()) return false;
        if(locY < lower.y()) return false;

        if(locX > upper.x()) return false;
        if(locX < lower.x()) return false;

        if(locZ > upper.z()) return false;
        if(locZ < lower.z()) return false;
        return true;
    }

    public Location getRandomLocationInside(World world)
    {
        return new Location(world,
            lower.x() + 1 + RANDOM.nextInt(upper.x() - lower.x() - 1) + 0.5,
            upper.y() - 1,
            lower.z() + 1 + RANDOM.nextInt(upper.z() - lower.z() - 1) + 0.5);
    }
}
