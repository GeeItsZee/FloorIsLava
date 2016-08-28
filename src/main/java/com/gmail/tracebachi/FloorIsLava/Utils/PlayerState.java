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

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class PlayerState
{
    private boolean flyAllowed;
    private Location location;
    private ItemStack[] armor;
    private ItemStack[] content;
    private GameMode gameMode;

    public void save(Player target)
    {
        gameMode = target.getGameMode();
        flyAllowed = target.getAllowFlight();

        target.setGameMode(GameMode.SURVIVAL);

        location = target.getLocation();
        armor = target.getInventory().getArmorContents();
        content = target.getInventory().getContents();
    }

    public void restoreInventory(Player target)
    {
        target.setHealth(20.0);
        target.getInventory().setArmorContents(armor);
        target.getInventory().setContents(content);
    }

    public void restoreLocation(Player target)
    {
        target.teleport(location);
    }

    public void restoreGameMode(Player target)
    {
        target.setGameMode(gameMode);

        if(flyAllowed)
        {
            target.setAllowFlight( true );
            target.setFlying( true );
        }
    }
}
