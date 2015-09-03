package com.yahoo.tracebachi.FloorIsLava.Commands;

import com.yahoo.tracebachi.FloorIsLava.FloorArena;
import com.yahoo.tracebachi.FloorIsLava.FloorIsLavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class ManageFloorCommand implements CommandExecutor
{
    private static final String GOOD = ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "F.I.L." +
        ChatColor.DARK_GRAY + "] " + ChatColor.GREEN;
    private static final String BAD = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "F.I.L." +
        ChatColor.DARK_GRAY + "] " + ChatColor.RED;

    private final FloorIsLavaPlugin plugin;
    private final FloorArena arena;

    public ManageFloorCommand(FloorIsLavaPlugin plugin, FloorArena arena)
    {
        this.plugin = plugin;
        this.arena = arena;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if(!sender.hasPermission("FloorIsLava.Staff"))
        {
            sender.sendMessage(BAD + "You do not have access to this command!");
            return true;
        }

        if(args.length == 0)
        {
            sender.sendMessage(BAD + "/mfloor [start, stop, reload, enable, disable]");
            return true;
        }

        if(args[0].equalsIgnoreCase("start"))
        {
            sender.sendMessage(arena.forceStart());
        }
        else if(args[0].equalsIgnoreCase("stop"))
        {
            sender.sendMessage(arena.forceStop());
        }
        else if(args[0].equalsIgnoreCase("reload"))
        {
            arena.forceStop();
            plugin.reloadConfig();
            arena.loadConfig(plugin.getConfig());
            sender.sendMessage(GOOD + "Configuration reloaded.");
        }
        else if(args[0].equalsIgnoreCase("enable"))
        {
            arena.enableArena();
            sender.sendMessage(GOOD + "FloorIsLava enabled.");
        }
        else if(args[0].equalsIgnoreCase("disable"))
        {
            arena.disableArena();
            sender.sendMessage(GOOD + "FloorIsLava disabled. " +
                "Players will not be able to join until renabled.");
        }
        else
        {
            sender.sendMessage(BAD + "/mfloor [start, stop, reload, enable, disable]");
        }
        return true;
    }
}
