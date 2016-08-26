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
package com.gmail.tracebachi.FloorIsLava.Booster;

import com.gmail.tracebachi.FloorIsLava.Arena.Arena;
import com.gmail.tracebachi.FloorIsLava.FloorIsLavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import static com.gmail.tracebachi.FloorIsLava.Utils.ChatStrings.GOOD;

/**
 * Created by Jeremy Lugo (Refrigerbater) on 6/12/2016.
 */
public class Booster
{
    private FloorIsLavaPlugin plugin;
    private Arena arena;

    private String owner = "Potato";
    private boolean active = false;
    private long timeActivated;
    private BoosterType boosterType = BoosterType.NONE;
    private BukkitTask task;

    public Booster(FloorIsLavaPlugin plugin, Arena arena)
    {
        this.plugin = plugin;
        this.arena = arena;
    }

    public void start(String owner, BoosterType boosterType)
    {
        this.owner = owner;
        this.boosterType = boosterType;
        this.timeActivated = System.currentTimeMillis();
        this.active = true;
        this.task = Bukkit.getScheduler().runTaskTimer(plugin, () ->
        {
            long boosterTypeTime = boosterType.getTime();

            if(boosterTypeTime > 0 && boosterTypeTime < System.currentTimeMillis() - timeActivated)
            {
                stop();
                return;
            }

            String message = ChatColor.translateAlternateColorCodes('&',
                GOOD + owner + "&7's &f" + boosterType.getName() +
                " &7booster is active." + " Loadout points are increased to 10" +
                ", money, and wintatoes earned are doubled.");
            broadcast(message, false);

            String formattedTimeLeft = getFormattedTimeLeft();
            String timeLeftMessage = ChatColor.translateAlternateColorCodes('&',
                GOOD + "&7Time remaining for &a" + owner + "&7's Booster: " + formattedTimeLeft);
            broadcast(timeLeftMessage, false);
        }, 6000L, 6000L);

        String message = ChatColor.translateAlternateColorCodes('&',
            GOOD + owner + "" +
            " &7activated a &f" +
            boosterType.getName() + " &7booster." +
            " Loadout points are increased to 10, money and wintatoes" +
            " earned are doubled.");
        broadcast(message, true);
    }

    public void stop()
    {
        String message = ChatColor.translateAlternateColorCodes('&',
            GOOD + owner + "&7's &f" + boosterType.getName() +" &7booster is now over!");
        broadcast(message, true);

        arena.getLoadoutMap().clear();

        active = false;
        owner = "Potato";
        timeActivated = 0;
        boosterType = BoosterType.NONE;

        task.cancel();
        task = null;
    }

    public String getFormattedTimeLeft()
    {
        long timeLeft = timeActivated + boosterType.getTime() - System.currentTimeMillis();
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        while(timeLeft >= 3600000)
        {
            hours++;
            timeLeft -= 3600000;
        }

        while(timeLeft >= 60000)
        {
            minutes++;
            timeLeft -= 60000;
        }

        while(timeLeft >= 1000)
        {
            seconds++;
            timeLeft -= 1000;
        }

        return hours + " hours, " + minutes + " minutes, " + seconds + " seconds";
    }

    public String getOwner()
    {
        return owner;
    }

    public boolean isActive()
    {
        return active;
    }

    public long getTimeActivated()
    {
        return timeActivated;
    }

    public BoosterType getBoosterType()
    {
        return boosterType;
    }

    public enum BoosterType
    {
        NONE("None", 0),
        ONE_HOUR("One Hour", 3600000),
        TWO_HOUR("Two Hour", 7200000),
        FOUR_HOUR("Four Hour", 14400000),
        PERMANENT("Permanent", -1);

        private String name;
        private long time;

        BoosterType(String name, long time)
        {
            this.name = name;
            this.time = time;
        }

        public String getName()
        {
            return name;
        }

        public long getTime()
        {
            return time;
        }

        public static BoosterType match(String input)
        {
            switch(input.toLowerCase())
            {
                case "1":
                case "1h":
                    return BoosterType.ONE_HOUR;
                case "2":
                case "2h":
                    return BoosterType.TWO_HOUR;
                case "4":
                case "4h":
                    return BoosterType.FOUR_HOUR;
                default:
                    return null;
            }
        }
    }

    private void broadcast(String message, boolean global)
    {
        World world = Bukkit.getWorld(arena.getWorldName());
        Location locationInside = arena.getWatchCuboidArea().getRandomLocationInside(world);
        int boosterBroadcastRange = arena.getBoosterBroadcastRange();

        for(Player target : Bukkit.getOnlinePlayers())
        {
            if(target != null)
            {
                Location location = target.getLocation();

                if(!global && location.distance(locationInside) > boosterBroadcastRange)
                {
                    continue;
                }

                target.sendMessage(message);
            }
        }
    }
}
