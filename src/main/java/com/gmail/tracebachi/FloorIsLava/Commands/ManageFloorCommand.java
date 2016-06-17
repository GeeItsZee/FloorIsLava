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
package com.gmail.tracebachi.FloorIsLava.Commands;

import com.gmail.tracebachi.FloorIsLava.Arena.Arena;
import com.gmail.tracebachi.FloorIsLava.FloorIsLavaPlugin;
import com.gmail.tracebachi.FloorIsLava.Booster.Booster;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class ManageFloorCommand implements CommandExecutor
{
    private final FloorIsLavaPlugin plugin;
    private final Arena arena;

    public ManageFloorCommand(FloorIsLavaPlugin plugin, Arena arena)
    {
        this.plugin = plugin;
        this.arena = arena;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if(!sender.hasPermission("FloorIsLava.Staff"))
        {
            sender.sendMessage(Arena.BAD + "You do not have access to this command!");
            return true;
        }

        if(args.length == 0)
        {
            sender.sendMessage(Arena.BAD + "/mfloor [start, stop, Booster, reload, enable, disable]");
            return true;
        }

        if(args[0].equalsIgnoreCase("start"))
        {
            arena.forceStart(sender);
        }
        else if(args[0].equalsIgnoreCase("stop"))
        {
            arena.forceStop(sender);
        }
        else if(args[0].equalsIgnoreCase("reload"))
        {
            arena.forceStop(sender);
            plugin.reloadConfig();
            arena.loadConfig(plugin.getConfig());
            sender.sendMessage(Arena.GOOD + "Configuration reloaded.");
        }
        else if(args[0].equalsIgnoreCase("enable"))
        {
            arena.enableArena(sender);
        }
        else if(args[0].equalsIgnoreCase("disable"))
        {
            arena.disableArena(sender);
        }
        else if(args[0].equalsIgnoreCase("Booster"))
        {
            if(args.length < 2)
            {
                sender.sendMessage(Arena.BAD + "/mfloor Booster [start, stop]");
                return true;
            }

            if(args[1].equalsIgnoreCase("stop"))
            {
                if(!arena.getBooster().isActive())
                {
                    sender.sendMessage(Arena.BAD + "A Booster is not active.");
                    return true;
                }
                arena.getBooster().stop();
            }
            else if(args[1].equalsIgnoreCase("start"))
            {
                if(arena.getBooster().isActive())
                {
                    sender.sendMessage(Arena.BAD + "Booster is already active. "
                                + "To start another one, first type: /mfloor Booster stop");
                    return true;
                }
                String owner = "Console";
                if(sender instanceof Player)
                    owner = ((Player) sender).getName();
                Booster.BoosterType type = Booster.BoosterType.PERMANENT;
                if(args.length >= 3)
                {
                    String requestedType = args[2];
                    Booster.BoosterType newType = Booster.BoosterType.match(requestedType);
                    if(newType == null)
                    {
                        sender.sendMessage(arena.BAD + "/mfloor Booster start [1h, 2h, 4h]");
                        return true;
                    }
                    type = newType;
                }
                arena.getBooster().start(owner, type);
            }
            else
            {
                sender.sendMessage(Arena.BAD + "/mfloor Booster [start, stop]");
            }
        }
        else
        {
            sender.sendMessage(Arena.BAD + "/mfloor [start, stop, Booster, reload, enable, disable]");
        }
        return true;
    }
}
