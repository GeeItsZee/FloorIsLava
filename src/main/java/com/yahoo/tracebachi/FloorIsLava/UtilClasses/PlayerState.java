package com.yahoo.tracebachi.FloorIsLava.UtilClasses;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class PlayerState
{
    private boolean flyAllowed;
    private Location location;
    private ItemStack[] armor;
    private ItemStack[] content;
    private GameMode gameMode;

    public void save(Player target)
    {
        gameMode = target.getGameMode();
        flyAllowed = target.getAllowFlight();

        target.setGameMode(GameMode.SURVIVAL);

        location = target.getLocation();
        armor = target.getInventory().getArmorContents();
        content = target.getInventory().getContents();
    }

    public void restoreInventory(Player target)
    {
        target.setHealth(20.0);
        target.getInventory().setArmorContents(armor);
        target.getInventory().setContents(content);
    }

    public void restoreLocation(Player target)
    {
        target.teleport(location);
    }

    public void restoreGameMode(Player target)
    {
        target.setGameMode(gameMode);

        if(flyAllowed)
        {
            target.setAllowFlight( true );
            target.setFlying( true );
        }
    }
}
