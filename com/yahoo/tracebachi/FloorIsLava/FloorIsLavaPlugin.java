package com.yahoo.tracebachi.FloorIsLava;

import com.yahoo.tracebachi.FloorIsLava.Commands.FloorCommand;
import com.yahoo.tracebachi.FloorIsLava.Commands.ManageFloorCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class FloorIsLavaPlugin extends JavaPlugin
{
    private FloorArena arena;
    private Economy economy = null;

    public Economy getEconomy()
    {
        return economy;
    }

    @Override
    public void onLoad()
    {
        File config = new File(getDataFolder(), "config.yml");
        if(!config.exists()) { saveDefaultConfig(); }
    }

    @Override
    public void onEnable()
    {
        /*********************************************************************/
        // Link with Vault Economy
        RegisteredServiceProvider<Economy> economyProvider = getServer()
            .getServicesManager()
            .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if(economyProvider != null) { economy = economyProvider.getProvider(); }
        /*********************************************************************/

        ItemStack winTato = new ItemStack(Material.POTATO_ITEM);
        ItemMeta winTatoMeta = winTato.getItemMeta();
        winTatoMeta.setDisplayName(ChatColor.GOLD + "WinTato");
        List<String> lore = new ArrayList<>();
        lore.add("You won a round of FloorIsLava!");
        lore.add("--");
        lore.add("May the WinTato be with you - Zee");
        winTatoMeta.setLore(lore);
        winTato.setItemMeta(winTatoMeta);

        reloadConfig();
        arena = new FloorArena(this, winTato);
        arena.loadConfig(getConfig());
        getServer().getPluginManager().registerEvents(arena, this);

        getCommand("floor").setExecutor(new FloorCommand(arena));
        getCommand("mfloor").setExecutor(new ManageFloorCommand(this, arena));
    }

    @Override
    public void onDisable()
    {
        getCommand("mfloor").setExecutor(null);
        getCommand("floor").setExecutor(null);

        arena.forceStop();
        arena = null;
    }
}
