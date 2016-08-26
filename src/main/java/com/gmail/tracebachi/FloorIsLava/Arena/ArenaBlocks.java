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
package com.gmail.tracebachi.FloorIsLava.Arena;

import com.gmail.tracebachi.FloorIsLava.Utils.CuboidArea;
import com.gmail.tracebachi.FloorIsLava.Utils.Point;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;

import java.util.ArrayList;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class ArenaBlocks
{
    private CuboidArea cuboidArea;
    private ArrayList<BlockState> blockStates = new ArrayList<>();

    public ArenaBlocks(CuboidArea cuboidArea)
    {
        this.cuboidArea = cuboidArea;
    }

    public CuboidArea getCuboidArea()
    {
        return cuboidArea;
    }

    public void save(World world)
    {
        Point lower = cuboidArea.getLower();
        Point upper = cuboidArea.getUpper();

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

    public void restore()
    {
        for(BlockState state : blockStates)
        {
            state.update(true);
        }

        blockStates.clear();
    }

    public void degradeBlocks(World world, int amount)
    {
        Point lower = cuboidArea.getLower();
        Point upper = cuboidArea.getUpper();

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
