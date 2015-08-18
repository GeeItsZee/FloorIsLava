package com.yahoo.tracebachi.FloorIsLava;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by tracebachi@yahoo.com (BigBossZee) on 12/19/2014.
 */
public class PlayerState
{
    // Class Members
    boolean flyModeAllowed;
    GameMode gm;
    Location location;
    ItemStack[] armor;
    ItemStack[] content;

    // Methods
    public void saveGameAndFlyMode( Player target )
    {
        gm = target.getGameMode();
        flyModeAllowed = target.getAllowFlight();
    }

    public void saveLocationAndInventory( Player target )
    {
        // Save
        location = target.getLocation();
        armor = target.getInventory().getArmorContents();
        content = target.getInventory().getContents();

        // Clear contents
        target.getInventory().setArmorContents( null );
        target.getInventory().setContents( new ItemStack[] {} );
    }

    public void restoreGameAndFlyMode( Player target )
    {
        target.setGameMode( gm );
        if( flyModeAllowed )
        {
            target.setAllowFlight( true );
            target.setFlying( true );
        }
    }

    public void restoreLocationAndInventory( Player target )
    {
        // Set location, and inventory
        target.teleport(location);
        target.getInventory().setArmorContents( armor );
        target.getInventory().setContents( content );
        target.setHealth( 20.0 );

        // Remove references
        armor = content = null;
        location = null;
    }
}
