package com.yahoo.tracebachi.FloorIsLava.Floor;

import com.yahoo.tracebachi.FloorIsLava.Json.LoggedJsonParser;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by tracebachi@yahoo.com (BigBossZee) on 12/20/2014.
 */
public class FloorSetting
{
    // Class members
    private boolean logEnabled = true;
    public int wager;
    private int baseReward = 0;
    private int winnerBonus = 0;
    private int minimumPlayers = 2;
    private int countdownMax = 5;

    private int upper_x;
    private int upper_y;
    private int upper_z;

    private int lower_x;
    private int lower_y;
    private int lower_z;

    private int watch_x;
    private int watch_y;
    private int watch_z;

    private String worldName;
    private String[] prestartCommands;
    private String[] whitelistCommands;
    private Location watchLocation;
    private List<BlockState> arena = new LinkedList<BlockState>();

    private Random rand = new Random();

    // Methods
    public FloorSetting(String fileName)
    {
        // Initialize variables
        JsonObject elem;
        try
        {
            // Open and parse the file
            JsonParser parser = new JsonParser();
            elem = parser.parse( new FileReader( fileName ) )
                    .getAsJsonObject();
        }
        catch( FileNotFoundException ex )
        {
            // Print the stack trace
            ex.printStackTrace();
            Bukkit.getLogger().severe( "Please restart the server after " +
                    "fixing to ensure proper functionality." );
            return;
        }

        // Parse the values
        upper_x = LoggedJsonParser.getInt( "FIL", elem, "Upper_X", 0);
        upper_y = LoggedJsonParser.getInt( "FIL", elem, "Upper_Y", 0 );
        upper_z = LoggedJsonParser.getInt( "FIL", elem, "Upper_Z", 0 );

        lower_x = LoggedJsonParser.getInt( "FIL", elem, "Lower_X", 0 );
        lower_y = LoggedJsonParser.getInt( "FIL", elem, "Lower_Y", 0 );
        lower_z = LoggedJsonParser.getInt( "FIL", elem, "Lower_Z", 0 );

        watch_x = LoggedJsonParser.getInt( "FIL", elem, "Watch_X", 0 );
        watch_y = LoggedJsonParser.getInt( "FIL", elem, "Watch_Y", 0 );
        watch_z = LoggedJsonParser.getInt( "FIL", elem, "Watch_Z", 0 );

        minimumPlayers = LoggedJsonParser.getInt(
                "FIL", elem, "Minimum Players", 2 );

        worldName = LoggedJsonParser.getString(
                "FIL", elem, "World Name", "world" );

        baseReward = LoggedJsonParser.getInt(
                "FIL", elem, "Base Reward", 0 );

        winnerBonus = LoggedJsonParser.getInt(
                "FIL", elem, "Winner Bonus", 0 );

        countdownMax = LoggedJsonParser.getInt(
                "FIL", elem, "Countdown Max", 5 );

        prestartCommands = LoggedJsonParser.getStringArray(
                "FIL", elem, "Prestart Commands" );

        whitelistCommands = LoggedJsonParser.getStringArray(
                "FIL", elem, "Whitelist Commands" );

        logEnabled = LoggedJsonParser.getBoolean(
                "FIL", elem, "Enable Logging", true );

        // "Fix" the coordinates if needed into the right form
        int temp;
        if( upper_x < lower_x )
        {
            temp = upper_x;
            upper_x = lower_x;
            lower_x = temp;
        }
        if( upper_y < lower_y )
        {
            temp = upper_y;
            upper_y = lower_y;
            lower_y = temp;
        }
        if( upper_z < lower_z )
        {
            temp = upper_z;
            upper_z = lower_z;
            lower_z = temp;
        }

        // Set the watch location
        watchLocation = new Location( Bukkit.getWorld( worldName ),
                watch_x, watch_y, watch_z );
    }

    public boolean isLogEnabled() { return logEnabled; }

    public int getBaseReward() { return baseReward; }

    public int getWinnerReward() { return baseReward + winnerBonus + wager; }

    public int getMinPlayers() { return minimumPlayers; }

    public int getCountdownMax() { return countdownMax; }

    public Location getWatchLocation() { return watchLocation; }

    public String[] getPrestartCommands() { return prestartCommands; }

    public boolean inWhitelistCommands( String input )
    {
        // Loop through every command allowed
        for( String s : whitelistCommands )
        {
            if( input.equalsIgnoreCase( s ) ) { return true; }
        }

        // Else
        return false;
    }

    public boolean isInside( Location loc )
    {
        // Check if within bounds
        if( loc.getBlockY() > upper_y ) return false;
        if( loc.getBlockY() < lower_y ) return false;

        if( loc.getBlockX() > upper_x ) return false;
        if( loc.getBlockX() < lower_x ) return false;

        if( loc.getBlockZ() > upper_z ) return false;
        if( loc.getBlockZ() < lower_z ) return false;

        // Else
        return true;
    }

    public boolean isAboveOrBelow( Location loc )
    {
        // Check if within bounds
        return ( loc.getBlockY() <= lower_y ) ||
                ( loc.getBlockY() >= upper_y );
    }

    public void saveFloor()
    {
        // Initialize variables
        World world = Bukkit.getWorld(worldName);

        // Loop through the coordinates
        for( int i = lower_x; i <= upper_x ; ++i )
        {
            for( int j = lower_z; j <= upper_z ; ++j )
            {
                for( int k = lower_y; k <= upper_y ; ++k )
                {
                    arena.add( world.getBlockAt( i, k, j ).getState() );
                }
            }
        }
    }

    public void restoreFloor()
    {
        // Loop through the list and update the block
        for( BlockState state : arena ) { state.update(true); }

        // Clear the list
        arena.clear();
    }

    public void degradeFloor( int amount )
    {
        // Loop
        World world = Bukkit.getWorld( worldName );
        for( int i = lower_x; i <= upper_x; ++i )
        {
            for( int j = lower_z; j <= upper_z; ++j )
            {
                // Check if within
                if( i == (lower_x + amount) || i == (upper_x - amount)
                        || j == (lower_z + amount) || j == (upper_z - amount))
                {
                    // Loop at all heights
                    for( int k = lower_y; k <= upper_y; ++k )
                    {
                        world.getBlockAt( i, k, j ).setType( Material.AIR );
                    }
                }
            }
        }
    }

    public Location getRandomStartLocation()
    {
        // Build a location
        return new Location( Bukkit.getWorld( worldName ),
                lower_x + 1 + rand.nextInt( upper_x - lower_x - 1 ),
                upper_y - 1,
                lower_z + 1 + rand.nextInt( upper_z - lower_z - 1 ) );
    }
}
