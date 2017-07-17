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

import com.gmail.tracebachi.FloorIsLava.Leaderboard.FloorLeaderboard;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.gmail.tracebachi.FloorIsLava.Utils.ChatStrings.BAD;
import static com.gmail.tracebachi.FloorIsLava.Utils.ChatStrings.GOOD;

/**
 * Created by Jeremy Lugo on 4/10/2017.
 */
public class FloorHoloCommand implements CommandExecutor
{
    private final FloorLeaderboard floorLeaderboard;

    public FloorHoloCommand(FloorLeaderboard floorLeaderboard)
    {
        this.floorLeaderboard = floorLeaderboard;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if(!sender.hasPermission("FloorIsLava.Staff"))
        {
            sender.sendMessage(BAD + "You do not have access to this command!");
            return true;
        }

        if(!(sender instanceof Player))
        {
            sender.sendMessage(BAD + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if(args.length == 0)
        {
            player.sendMessage(BAD + "/floorholo [place, remove, reset]");
            return true;
        }

        if(args[0].equalsIgnoreCase("place"))
        {
            floorLeaderboard.addNewLeaderboard(player.getLocation());
            player.sendMessage(GOOD + "Leaderboard placed.");
        }
        else if(args[0].equalsIgnoreCase("remove"))
        {
            if(args.length >= 2 && parseInt(args[1]) != null)
            {
                int index = parseInt(args[1]);
                floorLeaderboard.removeLeaderboard(index);
                player.sendMessage(GOOD + "Leaderboard removed.");
            }
            else
            {
                player.sendMessage(BAD + "/floorholo remove <index>");
            }
        }
        else if(args[0].equalsIgnoreCase("reset"))
        {
            floorLeaderboard.resetScores();
            player.sendMessage(GOOD + "Scores reset.");
        }
        else
        {
            player.sendMessage(BAD + "/floorholo [place, remove, reset]");
        }

        return true;
    }

    private Integer parseInt(String src)
    {
        try
        {
            return Integer.parseInt(src);
        }
        catch(NumberFormatException e)
        {
            return null;
        }
    }
}
