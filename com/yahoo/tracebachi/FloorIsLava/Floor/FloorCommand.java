package com.yahoo.tracebachi.FloorIsLava.Floor;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by tracebachi@yahoo.com (BigBossZee) on 12/21/2014.
 */
public class FloorCommand implements CommandExecutor
{
    // Class Members
    private Floor floor;

    // Methods
    public FloorCommand( Floor floorRef ) { floor = floorRef; }

    @Override
    public boolean onCommand( CommandSender commandSender, Command command,
                              String s, String[] strings)
    {
        // Check if command
        if( !command.getLabel().equalsIgnoreCase( "floor" ) ) { return true; }

        // Check if player
        if( !(commandSender instanceof Player) ) { return true; }

        // Check sub-commands
        Player sender = (Player) commandSender;
        if( strings.length == 0 )
        {
            // Inform
            sender.sendMessage( ChatColor.YELLOW + "[FloorIsLava] " +
                    ChatColor.GREEN + "/floor [join/leave/watch/count/wager]" +
                    " [wager amount]" );
        }
        else if( strings[0].equalsIgnoreCase( "join" ) )
        {
            sender.sendMessage( floor.addPlayer(sender) );
        }
        else if( strings[0].equalsIgnoreCase( "leave" ) )
        {
            sender.sendMessage(floor.removePlayer(sender));
        }
        else if( strings[0].equalsIgnoreCase( "count" ) )
        {
            sender.sendMessage( floor.getPlayerCount() );
            sender.sendMessage(floor.getWager());
        }
        else if( strings[0].equalsIgnoreCase("watch") )
        {
            floor.teleportToWatch( sender );
        }
        else if (strings[0].equalsIgnoreCase( "wager" ) && strings.length >= 2 )
        {
            // Add if there is an argument
            sender.sendMessage( floor.addWager( sender.getName(), strings[1] ) );
        }
        else if( commandSender.hasPermission( "FloorIsLava.staff" ) )
        {
            // Check command
            if( strings[0].equalsIgnoreCase( "stop" ) )
            {
                // Force end the current arena
                floor.forceEnd();

                // Inform
                sender.sendMessage( ChatColor.YELLOW + "[FloorIsLava] " +
                        ChatColor.GRAY + "Force ended the arena."  );
            }
            else if( strings[0].equalsIgnoreCase( "start" ) )
            {
                // Force start the current arena
                floor.forceStart();

                // Inform
                sender.sendMessage( ChatColor.YELLOW + "[FloorIsLava] " +
                        ChatColor.GRAY + "Force started the arena."  );

            }
            else if( strings[0].equalsIgnoreCase( "reload" ) )
            {
                // Parse the settings again
                floor.parseSettings();

                // Inform
                sender.sendMessage( ChatColor.YELLOW + "[FloorIsLava] " +
                        ChatColor.GRAY + "Reloaded the arena file."  );
            }
        }
        else
        {
            // Inform
            sender.sendMessage( ChatColor.YELLOW + "[FloorIsLava] " +
                    ChatColor.GREEN + "/floor [join/leave/watch/count/wager]" +
                    " [wager amount]" );
        }

        // Return
        return true;
    }
}
