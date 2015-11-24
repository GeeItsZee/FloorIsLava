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

package com.yahoo.tracebachi.FloorIsLava;

import com.yahoo.tracebachi.FloorIsLava.UtilClasses.Point;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class FloorArenaBlocks
{
    private Point upper;
    private Point lower;
    private ArrayList<BlockState> blockStates = new ArrayList<>();
    private Random random = new Random();

    public FloorArenaBlocks(ConfigurationSection alpha, ConfigurationSection beta)
    {
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

    public void saveBlocks(World world)
    {
        for(int i = lower.x(); i <= upper.x(); ++i)
        {
            for(int j = lower.y(); j <= upper.y(); ++j)
            {
                for(int k = lower.z(); k <= upper.z(); ++k)
                {
                    blockStates.add(world.getBlockAt(i, j, k).getState());
                }
            }
        }
    }

    public void restoreBlocks()
    {
        for(BlockState state : blockStates)
        {
            state.update(true);
        }
        blockStates.clear();
    }

    public Location getRandomLocationInside(World world)
    {
        return new Location(world,
            lower.x() + 1 + random.nextInt(upper.x() - lower.x() - 1) + 0.5,
            upper.y() - 1,
            lower.z() + 1 + random.nextInt(upper.z() - lower.z() - 1) + 0.5);
    }

    public boolean isInside(Location loc)
    {
        if(loc.getBlockY() > upper.y()) return false;
        if(loc.getBlockY() < lower.y()) return false;

        if(loc.getBlockX() > upper.x()) return false;
        if(loc.getBlockX() < lower.x()) return false;

        if(loc.getBlockZ() > upper.z()) return false;
        if(loc.getBlockZ() < lower.z()) return false;
        return true;
    }

    public void degradeBlocks(World world, int amount)
    {
        for(int i = lower.x(); i <= upper.x(); ++i)
        {
            for(int j = lower.z(); j <= upper.z(); ++j)
            {
                if(i == (lower.x() + amount) || i == (upper.x() - amount) ||
                    j == (lower.z() + amount) || j == (upper.z() - amount))
                {
                    for(int k = lower.y(); k <= upper.y(); ++k)
                    {
                        world.getBlockAt(i, k, j).setType(Material.AIR);
                    }
                }
            }
        }
    }
}
