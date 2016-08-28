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
import com.gmail.tracebachi.FloorIsLava.Booster.Booster;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.gmail.tracebachi.FloorIsLava.Utils.ChatStrings.BAD;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class FloorBoosterCommand implements CommandExecutor
{
    private final Arena arena;

    public FloorBoosterCommand(Arena arena)
    {
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
            sender.sendMessage(BAD + "/floorbooster [start, stop]");
            return true;
        }

        Booster booster = arena.getBooster();

        if(args[0].equalsIgnoreCase("stop"))
        {
            if(!booster.isActive())
            {
                sender.sendMessage(BAD + "A Booster is not active.");
                return true;
            }

            booster.stop();
        }
        else if(args[0].equalsIgnoreCase("start"))
        {
            if(booster.isActive())
            {
                sender.sendMessage(BAD + "Booster is already active. "
                            + "To start another one, first type: /floorbooster stop");
                return true;
            }

            String owner = "Console";

            if(sender instanceof Player)
            {
                owner = sender.getName();
            }

            Booster.BoosterType type = Booster.BoosterType.PERMANENT;

            if(args.length >= 2)
            {
                String requestedType = args[1];
                Booster.BoosterType newType = Booster.BoosterType.match(requestedType);

                if(newType == null)
                {
                    sender.sendMessage(BAD + "/floorbooster start [1h, 2h, 4h]");
                    return true;
                }

                type = newType;
            }

            booster.start(owner, type);
        }
        else
        {
            sender.sendMessage(BAD + "/floorbooster [start, stop]");
        }

        return true;
    }
}
