package com.yahoo.tracebachi.FloorIsLava;

import com.yahoo.tracebachi.FloorIsLava.Floor.Floor;
import com.yahoo.tracebachi.FloorIsLava.Floor.FloorCommand;
import com.yahoo.tracebachi.FloorIsLava.Floor.FloorSetting;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by tracebachi@yahoo.com (BigBossZee) on 12/19/2014.
 */
public class FloorIsLava extends JavaPlugin
{
    // Class members
    public static FloorIsLava core = null;
    public static Economy economy = null;
    private Floor arena;

    @Override
    public void onDisable() { core = null; }

    @Override
    public void onEnable()
    {
        // Set the core
        core = this;

        /*********************************************************************/
        // Link with Vault Economy
        RegisteredServiceProvider<Economy> economyProvider = getServer()
                .getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if( economyProvider != null )
        {
            economy = economyProvider.getProvider();
        }
        /*********************************************************************/

        // Create the arena and parse its settings
        arena = new Floor();
        arena.parseSettings();

        // Set up the command executors
        getCommand( "floor" ).setExecutor( new FloorCommand( arena ) );

        // Set up the listeners
        getServer().getPluginManager().registerEvents( arena, this );
    }
}
