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
package com.yahoo.tracebachi.FloorIsLava.Commands;

import com.yahoo.tracebachi.FloorIsLava.FloorArena;
import com.yahoo.tracebachi.FloorIsLava.FloorIsLavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class ManageFloorCommand implements CommandExecutor
{
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
            sender.sendMessage(FloorArena.BAD + "You do not have access to this command!");
            return true;
        }

        if(args.length == 0)
        {
            sender.sendMessage(FloorArena.BAD + "/mfloor [start, stop, reload, enable, disable]");
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
            sender.sendMessage(FloorArena.GOOD + "Configuration reloaded.");
        }
        else if(args[0].equalsIgnoreCase("enable"))
        {
            arena.enableArena();
            sender.sendMessage(FloorArena.GOOD + "FloorIsLava enabled.");
        }
        else if(args[0].equalsIgnoreCase("disable"))
        {
            arena.disableArena();
            sender.sendMessage(FloorArena.GOOD + "FloorIsLava disabled. " +
                "Players will not be able to join until renabled.");
        }
        else
        {
            sender.sendMessage(FloorArena.BAD + "/mfloor [start, stop, reload, enable, disable]");
        }
        return true;
    }
}
