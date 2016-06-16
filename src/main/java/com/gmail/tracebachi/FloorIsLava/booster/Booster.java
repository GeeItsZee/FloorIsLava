package com.gmail.tracebachi.FloorIsLava.booster;

import com.gmail.tracebachi.FloorIsLava.FloorIsLavaPlugin;
import com.gmail.tracebachi.FloorIsLava.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Jeremy Lugo(Refrigerbater) on 6/12/2016.
 */
public class Booster
{
    private FloorIsLavaPlugin plugin;
    private Arena arena;

    private String owner = "Potato";
    private boolean active = false;
    private long timeActivated;
    private BoosterType boosterType = BoosterType.NONE;
    private int taskId;

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

        this.taskId = run();
        String message = ChatColor.translateAlternateColorCodes('&',
                    arena.GOOD + owner + " &7activated a &f" +
                    boosterType.getName() + " &7booster." +
                    " Loadout points are increased to 10, money and wintatoes" +
                    " earned are doubled.");
        broadcast(message, true);
}

    public void stop()
    {
        String message = ChatColor.translateAlternateColorCodes('&',
                    arena.GOOD + owner + "&7's &f" + boosterType.getName() +
                                " &7booster is now over!");
        broadcast(message, true);

        arena.getLoadoutMap().clear();

        active = false;
        owner = null;
        timeActivated = -1;
        boosterType = BoosterType.NONE;

        Bukkit.getScheduler().cancelTask(taskId);
        taskId = 0;
    }

    public int run()
    {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, ()-> {
            if(boosterType.getTime() > 0
                        && boosterType.getTime() <= System.currentTimeMillis() - timeActivated)
            {
                stop();
                return;
            }
            String message = ChatColor.translateAlternateColorCodes('&',
                        arena.GOOD + owner + "&7's &f" + boosterType.getName() +
                        " &7booster is active." + " Loadout points are increased to 10" +
                        ", money and wintatoes earned are doubled.");
            broadcast(message, false);
            String formattedTimeLeft = getFormattedTimeLeft();
            int hoursLeft = Integer.parseInt(formattedTimeLeft.split(":")[0]);
            int minutesLeft = Integer.parseInt(formattedTimeLeft.split(":")[1]);
            int secondsLeft = Integer.parseInt(formattedTimeLeft.split(":")[2]);
            String timeLeftMessage = ChatColor.translateAlternateColorCodes('&',
                        Arena.GOOD + "&7Time remaining for &a" + owner + "&7's booster: "
                        + (boosterType.equals(BoosterType.PERMANENT) ? "Unlimited" : "Hours: "
                        + hoursLeft + ", Minutes: " + minutesLeft + ", Seconds: " + secondsLeft));
            broadcast(timeLeftMessage, false);
        }, 6000L, 6000L);
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
        return hours + ":" + minutes + ":" + seconds;
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
                case "1h": return BoosterType.ONE_HOUR;
                case "2h": return BoosterType.TWO_HOUR;
                case "4h": return BoosterType.FOUR_HOUR;
                default: return null;
            }
        }
    }

    private void broadcast(String message, boolean global)
    {
        for(Player target : Bukkit.getOnlinePlayers())
        {
            if(!global && target.getLocation().distance(arena.getWatchLocation())
                        > arena.getBoosterBroadcastRange()) continue;

            if(target != null)
            {
                target.sendMessage(message);
            }
        }
    }
}
