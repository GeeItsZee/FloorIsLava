package com.yahoo.tracebachi.FloorIsLava.Commands;

import com.yahoo.tracebachi.FloorIsLava.FloorArena;
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
    private static final String GOOD = ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "F.I.L." +
        ChatColor.DARK_GRAY + "] " + ChatColor.GREEN;
    private static final String BAD = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "F.I.L." +
        ChatColor.DARK_GRAY + "] " + ChatColor.RED;

    private final FloorArena arena;

    public FloorCommand(FloorArena arena)
    {
        this.arena = arena;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(BAD + "This command can only be used by players." );
            return true;
        }

        if(args.length == 0)
        {
            sender.sendMessage(BAD + "/floor [join, leave, watch, count, wager] [wager amount]" );
            return true;
        }

        Player player = (Player) sender;
        String lowerArg = args[0].toLowerCase();

        if(lowerArg.startsWith("j"))
        {
            player.sendMessage(arena.add(player));
        }
        else if(lowerArg.startsWith("l"))
        {
            player.sendMessage(arena.remove(player));
        }
        else if(lowerArg.equals("watch"))
        {
            player.teleport(arena.getWatchLocation());
        }
        else if(lowerArg.startsWith("c"))
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
        else if(lowerArg.equals("wager") && args.length >= 2)
        {
            Integer amount = parseInt(args[1]);
            if(amount >= 0)
            {
                player.sendMessage(arena.addWager(amount, player.getName()));
            }
            else
            {
                player.sendMessage(BAD + "That is not a valid amount to wager.");
            }
        }
        else
        {
            player.sendMessage(BAD + "/floor [join, leave, watch, count, wager] [wager amount]" );
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
