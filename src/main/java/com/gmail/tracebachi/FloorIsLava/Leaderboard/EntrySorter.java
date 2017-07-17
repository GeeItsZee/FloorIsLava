package com.gmail.tracebachi.FloorIsLava.Leaderboard;

import com.gmail.tracebachi.FloorIsLava.FloorIsLavaPlugin;
import org.bukkit.Bukkit;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Jeremy Lugo on 7/16/2017.
 */
public class EntrySorter
{
    private static final FloorIsLavaPlugin PLUGIN = FloorIsLavaPlugin.getInstance();

    public interface Callback
    {
        void sorted();
    }

    public static void sortList(List<LeaderboardEntry> list, Comparator comparator, Callback callback)
    {
        executeAsync(() ->
        {
            list.sort(comparator);
            executeSync(callback::sorted);
        });
    }

    private static void executeAsync(Runnable runnable)
    {
        Bukkit.getScheduler().runTaskAsynchronously(PLUGIN, runnable);
    }

    private static void executeSync(Runnable runnable)
    {
        Bukkit.getScheduler().runTask(PLUGIN, runnable);
    }
}
