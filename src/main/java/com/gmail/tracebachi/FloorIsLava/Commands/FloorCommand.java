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
import com.gmail.tracebachi.FloorIsLava.Gui.FloorGuiMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.gmail.tracebachi.FloorIsLava.Utils.ChatStrings.BAD;
import static com.gmail.tracebachi.FloorIsLava.Utils.ChatStrings.GOOD;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class FloorCommand implements CommandExecutor
{
    private final Arena arena;

    public FloorCommand(Arena arena)
    {
        this.arena = arena;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(BAD + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if(args.length >= 2 && args[0].startsWith("w")) // W = wager
        {
            Integer amount = parseInt(args[1]);

            if(amount == null || amount <= 0)
            {
                player.sendMessage(BAD + args[0] + " is an invalid amount.");
                return true;
            }

            arena.wager(amount, player);
        }
        else if(args.length >= 1 && args[0].startsWith("c")) // C = count
        {
            String status = arena.hasStarted() ? "started." : "waiting.";

            player.sendMessage(GOOD + "There are " +
                arena.getPlayingSize() + " players " + status);
            player.sendMessage(GOOD + "Wager: $" +
                arena.getWager() + "");
        }
        else if(args.length >= 1 && args[0].startsWith("l")) // L = leave
        {
            arena.leave(player);
        }
        else
        {
            FloorGuiMenu menu = new FloorGuiMenu(arena);
            menu.showTo(player);
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
