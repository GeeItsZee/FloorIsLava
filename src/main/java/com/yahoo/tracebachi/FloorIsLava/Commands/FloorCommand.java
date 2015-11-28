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
import com.yahoo.tracebachi.FloorIsLava.FloorGuiMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class FloorCommand implements CommandExecutor
{
    private static final String GOOD = ChatColor.translateAlternateColorCodes('&', "&8[&aFIL&8]&a ");
    private static final String BAD = ChatColor.translateAlternateColorCodes('&', "&8[&cFIL&8]&c ");

    private final FloorArena arena;
    private final FloorGuiMenu menu;

    public FloorCommand(FloorArena arena, FloorGuiMenu menu)
    {
        this.arena = arena;
        this.menu = menu;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(BAD + "This command can only be used by players." );
            return true;
        }

        Player player = (Player) sender;

        if(args.length >= 2 && args[0].startsWith("w"))
        {
            Integer amount = parseInt(args[1]);
            if(amount == null || amount <= 0)
            {
                player.sendMessage(BAD + "That is not a valid amount to wager.");
            }
            else
            {
                player.sendMessage(arena.addWager(amount, player.getName()));
            }
        }
        else if(args.length >= 1 && args[0].startsWith("c"))
        {
            int playerCount = arena.getWatchingSize();
            int wager = arena.getWager();

            if(arena.hasStarted())
            {
                player.sendMessage(GOOD + "There are " + playerCount +
                    " players playing for $" + wager + ".");
            }
            else
            {
                player.sendMessage(GOOD + "There are " + playerCount +
                    " players waiting to play for $" + wager + ".");
            }
        }
        else
        {
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
