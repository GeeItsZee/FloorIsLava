package com.yahoo.tracebachi.FloorIsLava.Floor;

import com.yahoo.tracebachi.FloorIsLava.FloorIsLava;
import com.yahoo.tracebachi.FloorIsLava.PlayerState;
import com.yahoo.tracebachi.FloorIsLava.Tasks.CountdownTask;
import com.yahoo.tracebachi.FloorIsLava.Tasks.PlayTickTask;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;

/**
 * Created by tracebachi@yahoo.com (BigBossZee) on 12/19/2014.
 */
public class Floor implements Listener
{
    // Class Members
    private static final String tagPos = ChatColor.YELLOW + "[FloorIsLava] " +
            ChatColor.GREEN;
    private static final String tagNeg = ChatColor.YELLOW + "[FloorIsLava] " +
            ChatColor.RED;

    private boolean started = false;
    private BukkitTask countdownTask;
    private BukkitTask playTicker;
    private FloorSetting setting;
    private ItemStack winTato;

    private List<String> waiting = new LinkedList<String>();
    private Map<String, PlayerState> playing = new HashMap<String, PlayerState>();

    private int countdown = 0;
    private int elapsedTicks = 0;
    private int degradeLevel = 0;

    // Methods
    public Floor()
    {
        winTato = new ItemStack( Material.POTATO_ITEM );
        ItemMeta winTatoMeta = winTato.getItemMeta();
        winTatoMeta.setDisplayName( ChatColor.GOLD + "WinTato" );
        List<String> lore = new LinkedList<String>();
        lore.add( "You won a round of FloorIsLava!" );
        lore.add( "--" );
        lore.add( "May the WinTato be with you - Zee" );
        winTatoMeta.setLore(lore);
        winTato.setItemMeta( winTatoMeta );
    }

    public void parseSettings()
    {
        // Log the method call
        log( "parseSettings()" );

        // Parse the settings
        forceEnd();
        setting = new FloorSetting( FloorIsLava.core.getDataFolder()
                + File.separator + "arena.json" );
    }

    public String getWager()
    {
        return tagPos + "Current wager total is $" + setting.wager;
    }

    public String getPlayerCount()
    {
        return tagPos + "Current players: " +
                ((started) ?
                        ( "Playing = " + playing.size()) :
                        ( "Waiting = " + waiting.size()));
    }

    public void teleportToWatch( Player target )
    {
        target.teleport( setting.getWatchLocation() );
    }

    public String addWager( String name, String input )
    {
        try
        {
            // Parse the number
            int amount = Integer.parseInt( input );

            // Add to the wager
            if( amount > 0 )
            {
                // Withdraw
                EconomyResponse response = FloorIsLava.economy.bankWithdraw(
                        name, amount );

                // Check
                if( response.transactionSuccess() )
                {
                    // Add and broadcast
                    setting.wager += amount;
                    broadcastToFloor( tagPos + "+$" + amount + " by " +
                            name + " ( = $" + setting.wager + " )" );

                    // Inform
                    return tagPos + "You added $" + amount +
                            " to FloorIsLava ( = $" + setting.wager + " )";
                }
            }

            // Else
            return tagNeg + "Nice try. That won't work.";
        }
        catch( NumberFormatException ex )
        {
            return tagNeg + input + " is not a valid number.";
        }
    }

    public String addPlayer( Player target )
    {
        // Check if started
        if( started )
        {
            // Log the method call
            log( "addPlayer() not added because started is true" );

            // Inform
            return tagNeg + "FloorIsLava has already begun.";
        }
        else if( waiting.contains( target.getName() ) )
        {
            // Log the method call
            log( "addPlayer() not added because already in waiting" );

            // Inform
            return tagNeg + "You are already part of FloorIsLava.";
        }
        else
        {
            // Log the method call
            log( "addPlayer() =" + waiting.size() + 1 );

            // Broadcast, add, and update
            broadcastToFloor( tagPos + target.getName() + " has joined." );
            waiting.add(target.getName());
            updateCountdownTask();
            return tagPos + "You have joined.";
        }
    }

    public String removePlayer( Player target )
    {
        // Check if part of waiting
        String name = target.getName();
        PlayerState state = playing.remove( name );
        if( started && state != null )
        {
            // Log the method call
            log( "removePlayer() because started and state found" );

            // Restore
            state.restoreLocationAndInventory(target);
            state.restoreGameAndFlyMode(target);
            return tagPos + "You have left.";
        }
        else if( !started && waiting.remove( name ) )
        {
            // Log the method call
            log( "removePlayer() because not start and in waiting" );

            // Broadcast and update
            broadcastToFloor( tagNeg + name + " has left." );
            updateCountdownTask();
            Bukkit.getLogger().info( "FloorIsLava -player ->" + waiting.size() );
            return tagPos + "You have left.";
        }
        else
        {
            // Log the method call
            log( "removePlayer() not removed" );

            // Return
            return tagNeg + "You are not part of FloorIsLava.";
        }
    }

    public void countdownTick()
    {
        // Log the method call
        log( "countdownTick() = " + countdown );

        // Check the count
        if( countdown <= 0 )
        {
            // Cancel the task and start
            countdownTask.cancel();
            countdownTask = null;
            normalStart();
        }
        else
        {
            // Broadcast
            broadcastToFloor( tagPos + "Starting in " + countdown );
            countdown--;
        }
    }

    public void forceStart()
    {
        // Log the method call
        log( "forceStart()" );

        // If current game not in progress
        if( !started )
        {
            // Cancel the countdown task
            if( countdownTask != null )
            {
                countdownTask.cancel();
                countdownTask = null;
            }

            // Broadcast and start
            broadcastToFloor(tagPos + "Force started.");
            normalStart();
        }
    }

    public void normalStart()
    {
        // Log the method call
        log( "normalStart()" );

        // Save the floor
        setting.saveFloor();

        // Iterate through everyone
        for( String name : waiting )
        {
            // Initialize variables
            Player target = Bukkit.getPlayer( name );

            // Check if they are online, if not then ignore
            if( target.isOnline() )
            {
                // Initialize variables
                PlayerState tempState = new PlayerState();

                // Save game and fly mode, change to survival, save location and inventory
                tempState.saveGameAndFlyMode(target);
                target.setGameMode( GameMode.SURVIVAL );
                tempState.saveLocationAndInventory( target );

                // Add state to map
                playing.put(name, tempState);

                // Clear all effects from the player
                for( PotionEffect e : target.getActivePotionEffects() )
                {
                    target.removePotionEffect( e.getType() );
                }

                // Run through the prestart commands
                for( String command : setting.getPrestartCommands() )
                {
                    Bukkit.getServer().dispatchCommand( target, command );
                }

                // Move them onto the floor
                target.teleport( setting.getRandomStartLocation() );
            }
        }

        // Start the timer task
        playTicker = Bukkit.getScheduler().runTaskTimer(
                FloorIsLava.core,
                new PlayTickTask( this ),
                20,
                5 );

        // Set started
        started = true;
    }

    public void playTick()
    {
        // Log the method call
        log( "playTick()" );

        // Iterate through everyone
        Iterator<Map.Entry<String, PlayerState>> iter = playing.entrySet().iterator();
        while( iter.hasNext() )
        {
            // Initialize variables
            Map.Entry<String, PlayerState> entry = iter.next();
            Player target = Bukkit.getPlayer( entry.getKey() );
            PlayerState state = entry.getValue();
            Location location = target.getLocation();

            // Check if below the lower point or above the high point
            if( setting.isAboveOrBelow( location ) )
            {
                // Remove from the floor
                iter.remove();
                state.restoreLocationAndInventory( target );
                state.restoreGameAndFlyMode(target);

                // Reward with base
                FloorIsLava.economy.bankDeposit( entry.getKey(),
                        setting.getBaseReward());
                target.sendMessage(tagPos + "Thanks for playing! Here's $" +
                        setting.getBaseReward() + '.');

                // Broadcast
                broadcastToFloor(tagNeg + entry.getKey() + " fell! " +
                        playing.size() + '/' + waiting.size() + " left!");

                // Log remaining
                log( "^ Remaining: " + playing.size() );
            }
        }

        // If more than one remaining
        if( playing.size() > 1 )
        {
            // Degrade floor
            if( elapsedTicks > 24 && (elapsedTicks % 15) == 0 )
            {
                // Increment the degrade level and degrade
                setting.degradeFloor( degradeLevel );
                degradeLevel++;
            }

            // Increment elapsed
            elapsedTicks++;
        }
        else { normalEnd(); }
    }

    public void forceEnd()
    {
        // Log the method call
        log( "forceEnd()" );

        // Remove everyone
        for( Map.Entry<String, PlayerState> entry : playing.entrySet() )
        {
            // Initialize variables
            Player target = Bukkit.getPlayer( entry.getKey() );
            PlayerState state = entry.getValue();

            // Restore
            state.restoreLocationAndInventory(target);
            state.restoreGameAndFlyMode(target);
        }

        // End
        end();
    }

    public void normalEnd()
    {
        // Log the method call
        log( "normalEnd()" );

        // Loop through remaining players
        for( Map.Entry<String, PlayerState> entry : playing.entrySet() )
        {
            // Initialize variables
            Player target = Bukkit.getPlayer( entry.getKey() );
            PlayerState state = entry.getValue();

            // Restore
            state.restoreLocationAndInventory(target);
            target.getInventory().addItem(winTato);
            state.restoreGameAndFlyMode( target );

            // Reward with wager
            FloorIsLava.economy.bankDeposit( entry.getKey(),
                    setting.getWinnerReward() );
            target.sendMessage(tagPos + "You won! Here's a WinTato and $" +
                    setting.getWinnerReward());

            // Log the win
            Bukkit.getLogger().info( entry.getKey() + " won a round. Award = " +
                    setting.getWinnerReward() );
            setting.wager = 0;

            // Broadcast the win
            broadcastToFloor( tagPos + entry.getKey() + " won that round!" );
        }

        // End
        end();
    }

    private void updateCountdownTask()
    {
        // Log the method call
        log( "updateCountdownTask() " + waiting.size() );

        // Stop the start task
        if( countdownTask != null )
        {
            countdownTask.cancel();
            countdownTask = null;
        }

        // Check the waiting size
        if( waiting.size() >= setting.getMinPlayers() )
        {
            // Start the countdown task in 100 ticks
            countdown = setting.getCountdownMax();
            countdownTask = Bukkit.getScheduler().runTaskTimer( FloorIsLava.core,
                    new CountdownTask(this), 100, 10 );
        }
        else
        {
            // Broadcast the stop
            broadcastToFloor( tagNeg + "Too few players to start." );
        }
    }

    private void end()
    {
        // Log the method call
        log( "---------- end()" );

        // Reset tick specific values
        degradeLevel = 0;
        elapsedTicks = 0;

        // Clear the map and waiting
        playing.clear();
        waiting.clear();

        // Restore the floor
        if( setting != null ) { setting.restoreFloor(); }

        // Stop the ticker tasks
        if( playTicker != null )
        {
            playTicker.cancel();
            playTicker = null;
        }
        if( countdownTask != null )
        {
            countdownTask.cancel();
            countdownTask = null;
        }
        started = false;
    }

    @EventHandler
    public void onPlayerBlockClick( PlayerInteractEvent event )
    {
        // Check if the player is in the arena
        Player target = event.getPlayer();
        if( playing.containsKey( target.getName() ) )
        {
            // Check if right click
            if( event.getAction() == Action.LEFT_CLICK_BLOCK )
            {
                // Check if the block is part of the arena
                if( setting.isInside( event.getClickedBlock().getLocation() ) )
                {
                    // Convert it to air
                    event.getClickedBlock().setType( Material.AIR );
                }
            }

            // Cancel the event
            event.setCancelled( true );
        }
    }

    @EventHandler
    public void onPlayerTeleport( PlayerTeleportEvent event )
    {
        // Check if the destination is in the arena
        if( setting.isInside( event.getTo() ) &&
                !playing.containsKey( event.getPlayer().getName() ) )
        {
            // Cancel it
            event.setCancelled( true );
        }
    }

    @EventHandler
    public void onPlayerCommand( PlayerCommandPreprocessEvent event )
    {
        // Check if the player is in the arena
        Player target = event.getPlayer();
        if( playing.containsKey( target.getName() ) )
        {
            // Check if it is a floor command
            String word = event.getMessage().split( "\\s+" )[0];
            if( !target.hasPermission( "FloorIsLava.staff" ) &&
                    !setting.inWhitelistCommands( word ) )
            {
                // Cancel the event
                event.setCancelled( true );
                target.sendMessage( tagNeg + '\"' + event.getMessage() +
                        '\"' + " is not allowed while in FloorIsLava." );
            }
        }
    }

    @EventHandler
    public void onPlayerQuit( PlayerQuitEvent event )
    {
        // Check if the player is in the arena
        Player target = event.getPlayer();
        if( playing.containsKey( target.getName() ) ||
                waiting.contains( target.getName() ) )
        {
            // Remove them
            removePlayer(event.getPlayer());
        }
    }

    private void broadcastToFloor(String message)
    {
        // Loop
        for( String name : waiting )
        {
            // Get the player
            Player target = Bukkit.getPlayer( name );
            if( target != null && target.isOnline() )
            {
                target.sendMessage( message );
            }
        }
    }

    private void log( String input )
    {
        if( setting != null && setting.isLogEnabled() )
        {
            Bukkit.getLogger().info( "FIL_LOG " + input );
        }
    }
}
